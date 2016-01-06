package com.meterware.simplestub.generation.asm;

import com.meterware.simplestub.SimpleStubException;
import com.meterware.simplestub.generation.StubGenerator;
import com.meterware.simplestub.generation.StubKind;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
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
        Set<Method> abstractMethods = new HashSet<Method>();
        for (Method method : baseClass.getMethods())
            if (Modifier.isAbstract(method.getModifiers()))
                abstractMethods.add(method);

        return abstractMethods;
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

    private Class<?> defineClass(ClassLoader classLoader, String className, byte[] classBytes) {
        try {
            defineClassMethod.setAccessible(true);
            return (Class<?>) defineClassMethod.invoke(classLoader, className, classBytes, 0, classBytes.length);
        } catch (InvocationTargetException e) {
            throw new SimpleStubException("error creating stub for " + getStubName(), e.getTargetException());
        } catch (IllegalAccessException e) {
            throw new SimpleStubException("error creating stub for " + getStubName(), e);
        } finally {
            defineClassMethod.setAccessible(false);
        }
    }

    private String getStubName() {
        return baseClass.getName();
    }

}
