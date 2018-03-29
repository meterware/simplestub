package com.meterware.simplestub.generation.asm;
/*
 * Copyright (c) 2015-2018 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import com.meterware.simplestub.ClassUtils;
import com.meterware.simplestub.SimpleStubException;
import com.meterware.simplestub.generation.StubGenerator;
import com.meterware.simplestub.generation.StubKind;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * A stub generator which uses the ASM library.
 *
 * @author Russell Gold
 */
class AsmStubGenerator extends StubGenerator {

    private static final Map<StubKind,MethodGenerator> methodGenerators = new HashMap<>();

    static {
        methodGenerators.put(StubKind.DEFAULT, new DefaultMethodGenerator());
        methodGenerators.put(StubKind.NICE, new NiceMethodGenerator());
        methodGenerators.put(StubKind.STRICT, new StrictMethodGenerator());
    }

    private final Class<?> baseClass;
    private MethodGenerator methodGenerator;

    AsmStubGenerator(Class<?> baseClass, StubKind kind) {
        this.baseClass = baseClass;
        methodGenerator = methodGenerators.get(kind);
    }

    @Override
    public Class<?> generateStubClass(String stubClassName, Class<?> anchorClass) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        defineClass(stubClassName, cw);

        for (Method method : getAbstractMethods())
            methodGenerator.addMethod(cw, method);

        cw.visitEnd();
        return defineClass(anchorClass, stubClassName, cw.toByteArray());
    }

    private void defineClass(String stubClassName, ClassWriter cw) {
        if (baseClass.isInterface())
            defineClass(stubClassName, cw, Object.class, baseClass);
        else
            defineClass(stubClassName, cw, baseClass);
    }

    private void defineClass(String stubClassName, ClassWriter cw, Class<?> baseClass, Class<?>... interfaces) {
        cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, toInternalName(stubClassName), null, toInternalName(baseClass), toInternalNames(interfaces));

        for (Constructor constructor : baseClass.getDeclaredConstructors())
            addConstructor(cw, constructor);
    }

    private void addConstructor(ClassWriter cw, Constructor constructor) {
        org.objectweb.asm.commons.Method m = org.objectweb.asm.commons.Method.getMethod(constructor);
        GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, m, null, null, cw);
        mg.loadThis();
        mg.loadArgs();
        mg.invokeConstructor(Type.getType(constructor.getDeclaringClass()), m);
        mg.returnValue();
        mg.endMethod();
    }

    private Set<Method> getAbstractMethods() {
        Set<MethodSpec> abstractMethods = new HashSet<>();

        for (Class<?> aClass = baseClass; aClass != null; aClass = aClass.getSuperclass())
            for (Class<?> anInterface : aClass.getInterfaces())
                addInterfaceMethods(abstractMethods, anInterface);

        for (Class<?> aClass : getClassHierarchy(baseClass))
            updateAbstractMethods(abstractMethods, aClass);

        return toMethodSet(abstractMethods);
    }

    private Set<Method> toMethodSet(Set<MethodSpec> methodSpecs) {
        Set<Method> methods = new HashSet<>();
        for (MethodSpec methodSpec : methodSpecs)
            methods.add(methodSpec.getMethod());
        return methods;
    }

    private void addInterfaceMethods(Set<MethodSpec> abstractMethods, Class<?> anInterface) {
        for (Method method : anInterface.getMethods())
            abstractMethods.add(new MethodSpec(method));
    }

    private Class<?>[] getClassHierarchy(Class<?> baseClass) {
        List<Class<?>> hierarchy = new ArrayList<>();
        for (Class<?> aClass = baseClass; aClass != null; aClass = aClass.getSuperclass())
            hierarchy.add(0, aClass);
        return hierarchy.toArray(new Class[0]);
    }

    private void updateAbstractMethods(Set<MethodSpec> abstractMethods, Class<?> aClass) {
        for (Method method : aClass.getDeclaredMethods())
            if (Modifier.isAbstract(method.getModifiers()))
                abstractMethods.add(new MethodSpec(method));
            else
                abstractMethods.remove(new MethodSpec(method));
    }


    private static String[] toInternalNames( Class<?>[] classes ) {
        String[] result = new String[classes.length];
        for (int i = 0; i < classes.length; i++)
            result[i] = toInternalName(classes[i]);
        return result;
    }

    private static String toInternalName( Class<?> aClass ) {
        return toInternalName(aClass.getName());
    }

    private static String toInternalName( String stubClassName ) {
        return stubClassName.replace('.','/');
    }

    private Class<?> defineClass(Class<?> anchorClass, String className, byte[] classBytes) {
        try {
            return ClassUtils.defineClass(anchorClass, className, classBytes);
        } catch (Throwable e) {
            throw new SimpleStubException("error creating stub for %s", e, getStubName());
        }
    }

    private String getStubName() {
        return baseClass.getName();
    }

    static class MethodSpec {
        private Method method;

        MethodSpec(Method method) {
            this.method = method;
        }

        public Method getMethod() {
            return method;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof MethodSpec && equals((MethodSpec) other);
        }

        private boolean equals(MethodSpec other) {
            return method.getName().equals(other.method.getName()) &&
                    Arrays.equals(method.getParameterTypes(), other.method.getParameterTypes());
        }

        @Override
        public int hashCode() {
            return method.getName().hashCode();
        }
    }

}
