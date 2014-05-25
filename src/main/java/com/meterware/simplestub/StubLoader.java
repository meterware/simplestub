package com.meterware.simplestub;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to load generated simple stubs.
 */
class StubLoader {

    private final static String SIMPLESTUB_SUFFIX = "$$_com_meterware_SimpleStub";
    private final static String SIMPLESTUB_STRICT_SUFFIX = "$$_com_meterware_SimpleStub_Strict";
    private final static Map<Class<?>, Class<?>> PRIMITIVE_TYPES;

    static {
        PRIMITIVE_TYPES = new HashMap<Class<?>, Class<?>>();
        PRIMITIVE_TYPES.put(Void.TYPE,      Void.class);
        PRIMITIVE_TYPES.put(Character.TYPE, Character.class);
        PRIMITIVE_TYPES.put(Byte.TYPE,      Byte.class);
        PRIMITIVE_TYPES.put(Short.TYPE,     Short.class);
        PRIMITIVE_TYPES.put(Integer.TYPE,   Integer.class);
        PRIMITIVE_TYPES.put(Long.TYPE,      Long.class);
        PRIMITIVE_TYPES.put(Boolean.TYPE,   Boolean.class);
        PRIMITIVE_TYPES.put(Float.TYPE,     Float.class);
        PRIMITIVE_TYPES.put(Double.TYPE,    Double.class);
    }

    private final ClassPool pool = new ClassPool(ClassPool.getDefault());
    private final Class<?> baseClass;
    private boolean strict;
    private Type type;

    StubLoader(Class<?> baseClass, boolean strict) {
        this.baseClass = baseClass;
        this.strict = strict;
        this.type = baseClass.getClassLoader() == null ? Type.jdkClass : Type.userClass;
    }

    enum Type {
        jdkClass {
            @Override
            String getPackagePrefix() {
                return "com.meterware.simplestub.";
            }

            @Override
            Class<?> loadClass(String stubClassName, ClassLoader classLoader) throws ClassNotFoundException {
                return Class.forName(stubClassName);
            }
        }, userClass {
            @Override
            String getPackagePrefix() {
                return "";
            }

            @Override
            Class<?> loadClass(String stubClassName, ClassLoader classLoader) throws ClassNotFoundException {
                return classLoader.loadClass(stubClassName);
            }
        };

        abstract String getPackagePrefix();

        abstract Class<?> loadClass(String stubClassName, ClassLoader classLoader) throws ClassNotFoundException;
    }

    /**
     * Instantiates a stub from an abstract class. The class must have been marked with the @SimpleStub annotation.
     *
     * @param parameters any parameters needed for the constructor. If the class is an inner class, the first parameter
     *                   must be the outer class instance.
     * @return a newly instantiated stub
     */
    @SuppressWarnings("unchecked")
    <T> T create(Object... parameters) {
        try {
            Class<?> stubClass = getStubClass();
            Constructor<?> constructor = getConstructor(stubClass, parameters);
            constructor.setAccessible(true);
            if (isVarArgs(constructor))
                return (T) constructor.newInstance(toVarArgList(constructor, parameters));
            else
                return (T) constructor.newInstance(parameters);
        } catch (InstantiationException e) {
            throw new SimpleStubException("Unable to instantiate stub for " + baseClass.getName(), e);
        } catch (IllegalAccessException e) {
            throw new SimpleStubException("Unable to instantiate stub for " + baseClass.getName(), e);
        } catch (NoSuchMethodException e) {
            throw new SimpleStubException("Unable to instantiate stub for " + baseClass.getName(), e);
        } catch (InvocationTargetException e) {
            throw new SimpleStubException("Unable to instantiate stub for " + baseClass.getName(), e);
        }
    }

    private Object[] toVarArgList(Constructor<?> constructor, Object[] parameters) {
        Object[] result = new Object[numConstructorArgs(constructor)];
        System.arraycopy(parameters, 0, result, 0, result.length-1);
        result[result.length - 1] = isNonVarArgsInvocation(constructor, parameters)
                                        ? lastElement(parameters)
                                        : remainingArgsAsArray(parameters, constructor);
        return result;
    }

    private Object remainingArgsAsArray(Object[] parameters, Constructor<?> constructor) {
        return slice(lastConstructorArg(constructor).getComponentType(), parameters, numConstructorArgs(constructor)-1);
    }

    private boolean isNonVarArgsInvocation(Constructor<?> constructor, Object[] parameters) {
        return parameters.length == numConstructorArgs(constructor)
                && isAssignableFrom(lastConstructorArg(constructor), lastElement(parameters));
    }

    private int numConstructorArgs(Constructor<?> constructor) {
        return constructor.getParameterTypes().length;
    }

    private Class<?> lastConstructorArg(Constructor<?> constructor) {
        return lastElement(constructor.getParameterTypes());
    }

    private <T> T lastElement(T[] array) {
        return array[array.length-1];
    }

    private Object slice(Class<?> componentType, Object[] source, int first) {
        Object result = Array.newInstance(componentType, source.length - first);
        for (int i = 0; i < source.length - first; i++)
            Array.set(result, i, source[first + i]);
        return result;
    }

    Constructor<?> getConstructor(Class<?> stubClass, Object... parameters) throws NoSuchMethodException {
        Constructor<?> constructor = findConstructor(stubClass, parameters);
        if (constructor != null) return constructor;

        StringBuilder sb = new StringBuilder("Unable to instantiate stub for ");
        sb.append(stubClass.getSuperclass().getName()).append(" because no constructor matches ").append(Arrays.toString(parameters));
        Class<?> enclosingClass = baseClass.getEnclosingClass();
        if (!isStatic(baseClass) && enclosingClass != null && !matchesFirstParameter(enclosingClass, parameters))
            sb.append(". This appears to be a non-static inner class, but the first parameter is not the enclosing class.");
            
        throw new SimpleStubException(sb.toString());
    }

    private boolean isStatic(Class<?> aClass) {
        return Modifier.isStatic(aClass.getModifiers());
    }

    private boolean matchesFirstParameter(Class<?> enclosingClass, Object[] parameters) {
        return parameters.length > 0 && enclosingClass.isAssignableFrom(parameters[0].getClass());
    }

    private Constructor<?> findConstructor(Class<?> stubClass, Object... parameters) throws NoSuchMethodException {
        for (Constructor constructor : stubClass.getDeclaredConstructors())
            if (isCompatible(constructor, parameters)) return constructor;
        return null;
    }

    private boolean isCompatible(Constructor constructor, Object[] parameters) throws NoSuchMethodException {
        return isCompatible(constructor.getParameterTypes(), parameters, isVarArgs(constructor));
    }

    @SuppressWarnings("unchecked")
    private boolean isVarArgs(Constructor constructor) throws NoSuchMethodException {
        return constructor.getDeclaringClass().getSuperclass().getDeclaredConstructor(constructor.getParameterTypes()).isVarArgs();
    }

    private boolean isCompatible(Class[] formalArguments, Object[] actualParameters, boolean isVarArgs) {
        return areCompatibleFixedArgs(formalArguments, actualParameters)
                || (isVarArgs && areCompatibleVarArgs(formalArguments, actualParameters));
    }

    private boolean areCompatibleVarArgs(Class[] formalArguments, Object[] actualParameters) {
        return areFirstNElementsCompatible(formalArguments, actualParameters, formalArguments.length - 1) &&
               variableArgumentsAreCompatible(formalArguments, actualParameters, formalArguments.length - 1);
    }

    private boolean variableArgumentsAreCompatible(Class[] formalArguments, Object[] actualParameters, int numFixedArguments) {
        Class<?> variableArgumentType = formalArguments[numFixedArguments].getComponentType();
        for (int i = numFixedArguments; i < actualParameters.length; i++)
            if (!isAssignableFrom(variableArgumentType, actualParameters[i])) return false;
        return true;
    }

    private boolean areCompatibleFixedArgs(Class[] formalArguments, Object[] actualParameters) {
        return formalArguments.length == actualParameters.length &&
               areFirstNElementsCompatible(formalArguments, actualParameters, actualParameters.length);
    }

    private boolean areFirstNElementsCompatible(Class[] formalArguments, Object[] actualParameters, int numElements) {
        for (int i = 0; i < numElements; i++)
            if (!isAssignableFrom(formalArguments[i], actualParameters[i])) return false;
        return true;
    }

    private static boolean isAssignableFrom(Class<?> constructorParameterType, Object actualParameter) {
        if (actualParameter == null)
            return !constructorParameterType.isPrimitive();
        else
            return constructorParameterType.isAssignableFrom(actualParameter.getClass()) ||
                constructorParameterType.isPrimitive() && PRIMITIVE_TYPES.get(constructorParameterType).isAssignableFrom(actualParameter.getClass());
    }

    Class<?> getStubClass() {
        validateBaseClass();

        String stubClassName = createStubClassName(baseClass.getName());
        ClassLoader classLoader = baseClass.getClassLoader();
        try {
            return type.loadClass(stubClassName, classLoader);
        } catch (ClassNotFoundException e) {
            return loadStubClass(stubClassName);
        }
    }

    private void validateBaseClass() {
        if (!isAbstractClass())
            throw new SimpleStubException("Class " + baseClass.getName() + " is not abstract");
    }

    private boolean isAbstractClass() {
        return Modifier.isAbstract(baseClass.getModifiers());
    }

    private Class<?> loadStubClass(String stubClassName) {
        try {
            return createStubClass(stubClassName);
        } catch (NotFoundException e) {
            throw new SimpleStubException("Unable to create stub class", e);
        } catch (CannotCompileException e) {
            throw new SimpleStubException("Unable to create stub class", e);
        }
    }

    private Class<?> createStubClass(String stubClassName) throws NotFoundException, CannotCompileException {
        CtClass ctClass = createStubClassBase(stubClassName);
        for (CtMethod method : ctClass.getMethods()) {
            if (isAbstract(method))
                addStubMethod(ctClass, method);
        }
        return ctClass.toClass(baseClass.getClassLoader(), null);
    }

    private CtClass createStubClassBase(String stubClassName) throws NotFoundException {
        if (baseClass.isInterface()) {
            return createStubClassFromInterface(stubClassName);
        } else {
            return createStubClassFromAbstractClass(stubClassName);
        }
    }

    private CtClass createStubClassFromInterface(String stubClassName) throws NotFoundException {
        CtClass ctClass = pool.makeClass(stubClassName);
        ctClass.addInterface(pool.getCtClass(baseClass.getName()));
        return ctClass;
    }

    private CtClass createStubClassFromAbstractClass(String stubClassName) throws NotFoundException {
        return pool.makeClass(stubClassName, pool.get(baseClass.getName()));
    }

    private void addStubMethod(CtClass ctClass, CtMethod method) throws CannotCompileException, NotFoundException {
        CtMethod method1 = createMethod(ctClass, method);
        if (generateStrictStub())
            defineMethodToThrowException(method1);
        ctClass.addMethod(method1);
    }

    private void defineMethodToThrowException(CtMethod method) throws CannotCompileException {
        method.setBody("{ throw new com.meterware.simplestub.UnexpectedMethodCallException( \"unexpected call to method " +
                withoutSuffix(method.getLongName()) + "\"); }" );
    }

    private String withoutSuffix(String longName) {
        int i = longName.indexOf(SIMPLESTUB_SUFFIX);
        return longName.substring(0, i) + longName.substring(i + SIMPLESTUB_SUFFIX.length());
    }

    private boolean generateStrictStub() {
        return strict;
    }

    private CtMethod createMethod(CtClass ctClass, CtMethod method) throws NotFoundException, CannotCompileException {
        return CtNewMethod.make(method.getModifiers() & ~javassist.Modifier.ABSTRACT,
                method.getReturnType(), method.getName(), method.getParameterTypes(), method.getExceptionTypes(), null, ctClass);
    }

    private boolean isAbstract(CtMethod method) {
        return javassist.Modifier.isAbstract(method.getModifiers());
    }

    private String createStubClassName(String className) {
        return type.getPackagePrefix() + className + (strict ? SIMPLESTUB_STRICT_SUFFIX : SIMPLESTUB_SUFFIX);
    }

}
