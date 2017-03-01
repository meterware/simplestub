package com.meterware.simplestub.generation.asm;

import com.meterware.simplestub.SimpleStubException;
import com.meterware.simplestub.generation.StubGenerator;
import com.meterware.simplestub.generation.StubKind;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

/**
 * A stub generator which uses the ASM library.
 */
class AsmStubGenerator extends StubGenerator {

    /** Gain access to define class method. */
    private final Unsafe unsafe = AccessController.doPrivileged(
                    new PrivilegedAction<Unsafe>() {
                        public Unsafe run() {
                            try {
                                Field field = Unsafe.class.getDeclaredField("theUnsafe");
                                field.setAccessible(true);
                                return (Unsafe) field.get(null);
                            } catch (NoSuchFieldException exc) {
                                throw new Error("Could not access Unsafe", exc);
                            } catch (IllegalAccessException exc) {
                                throw new Error("Could not access Unsafe", exc);
                            }
                        }
                    }
            );

    private final Class<?> baseClass;
    private MethodGenerator methodGenerator;

    AsmStubGenerator(Class<?> baseClass, StubKind kind) {
        this.baseClass = baseClass;
        methodGenerator = MethodGenerator.getMethodGenerator(kind);
    }

    @Override
    public Class<?> loadStubClass(String stubClassName, ClassLoader classLoader) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        defineClass(stubClassName, cw);

        for (Method method : getAbstractMethods())
            methodGenerator.addMethod(cw, method);

        cw.visitEnd();
        return defineClass(classLoader, stubClassName, cw.toByteArray());
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
        Set<MethodSpec> abstractMethods = new HashSet<MethodSpec>();

        for (Class<?> aClass = baseClass; aClass != null; aClass = aClass.getSuperclass())
            for (Class<?> anInterface : aClass.getInterfaces())
                addInterfaceMethods(abstractMethods, anInterface);

        for (Class<?> aClass : getClassHierarchy(this.baseClass))
            updateAbstractMethods(abstractMethods, aClass);

        return toMethodSet(abstractMethods);
    }

    private Set<Method> toMethodSet(Set<MethodSpec> methodSpecs) {
        Set<Method> methods = new HashSet<Method>();
        for (MethodSpec methodSpec : methodSpecs)
            methods.add(methodSpec.getMethod());
        return methods;
    }

    private void addInterfaceMethods(Set<MethodSpec> abstractMethods, Class<?> anInterface) {
        for (Method method : anInterface.getMethods())
            abstractMethods.add(new MethodSpec(method));
    }

    private Class<?>[] getClassHierarchy(Class<?> baseClass) {
        List<Class<?>> hierarchy = new ArrayList<Class<?>>();
        for (Class<?> aClass = baseClass; aClass != null; aClass = aClass.getSuperclass())
            hierarchy.add(0, aClass);
        return hierarchy.toArray(new Class[hierarchy.size()]);
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

    private Class<?> defineClass(ClassLoader classLoader, String className, byte[] classBytes) {
        try {
            return (Class<?>) unsafe.defineClass( className, classBytes, 0, classBytes.length, classLoader, null );
        } catch (Throwable e) {
            throw new SimpleStubException("error creating stub for " + getStubName(), e);
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
