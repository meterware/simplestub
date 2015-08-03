package com.meterware.simplestub.generation.asm;

import com.meterware.simplestub.SimpleStubException;
import com.meterware.simplestub.generation.StubGenerator;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A stub generator which uses the ASM library.
 */
class AsmStubGenerator extends StubGenerator {

    /** The method used to define a class in a classloader. */
    private static java.lang.reflect.Method defineClassMethod;

    static {
        try {
            defineClassMethod = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
                public Method run() throws Exception {
                    Class<?> cl = Class.forName("java.lang.ClassLoader");
                    return cl.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
                }
            });
        }
        catch (PrivilegedActionException pae) {
            throw new RuntimeException("cannot initialize AsmStubGenerator", pae.getException());
        }
    }

    private final Class<?> baseClass;
    private Class<?>[] interfaces;
    private MethodGeneration.MethodGenerator methodGenerator;

    AsmStubGenerator(Class<?> baseClass, boolean strict, Class<?>... interfaces) {
        this.baseClass = baseClass;
        this.interfaces = interfaces;
        methodGenerator = strict ? MethodGeneration.getStrictMethodGenerator() : MethodGeneration.getNiceMethodGenerator();
    }

    @Override
    public Class<?> loadStubClass(String stubClassName, ClassLoader classLoader) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, toInternalName(stubClassName), null, toInternalName(baseClass), toInternalNames(interfaces));

        for (Constructor constructor : baseClass.getDeclaredConstructors())
            MethodGeneration.addConstructor(cw, constructor);

        for (Method method : getAbstractMethods())
            methodGenerator.addMethod(cw, method);

        cw.visitEnd();
        return defineClass(classLoader, stubClassName, cw.toByteArray());
    }

    private Set<Method> getAbstractMethods() {
        Set<Method> abstractMethods = new HashSet<Method>();
        addInterfaceMethods(abstractMethods, interfaces);

        for (Class<?> aClass = baseClass; aClass != null; aClass = aClass.getSuperclass())
            addAbstractDeclaredMethods(abstractMethods, baseClass);

        return abstractMethods;
    }

    private static void addInterfaceMethods(Set<Method> abstractMethods, Class<?>[] interfaces) {
        for (Class<?> anInterface : interfaces)
            Collections.addAll(abstractMethods, anInterface.getDeclaredMethods());
    }

    private static void addAbstractDeclaredMethods(Set<Method> abstractMethods, Class<?> aClass) {
        for (Method method : aClass.getDeclaredMethods())
            if (Modifier.isAbstract(method.getModifiers()))
                abstractMethods.add(method);

        addInterfaceMethods(abstractMethods, aClass.getInterfaces());
    }

    static String[] toInternalNames(Class<?>[] classes) {
        String[] result = new String[classes.length];
        for (int i = 0; i < classes.length; i++)
            result[i] = toInternalName(classes[i]);
        return result;
    }

    static String toInternalName(Class<?> aClass) {
        return toInternalName(aClass.getName());
    }

    static String toInternalName(String stubClassName) {
        return stubClassName.replace('.','/');
    }

    static private Class<?> defineClass(ClassLoader classLoader, String className, byte[] classBytes) {
        try {
            defineClassMethod.setAccessible(true);
            return (Class<?>) defineClassMethod.invoke(classLoader, className, classBytes, 0, classBytes.length);
        } catch (InvocationTargetException e) {
            throw new SimpleStubException("error creating stub for " + getFilteredName(className), e.getTargetException());
        } catch (IllegalAccessException e) {
            throw new SimpleStubException("error creating stub for " + getFilteredName(className), e);
        } finally {
            defineClassMethod.setAccessible(false);
        }
    }

    private static String getFilteredName(String className) {
        return StubGenerator.getNameFilter().toDisplayName(className);
    }
}
