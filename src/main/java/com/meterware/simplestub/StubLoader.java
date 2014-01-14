package com.meterware.simplestub;

import javassist.*;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to load generated simple stubs.
 */
class StubLoader {

    private final static String SIMPLESTUB_SUFFIX = "$$_com_meterware_SimpleStub";
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
    private final ClassLoader classLoader;

    StubLoader(Class<?> baseClass) {
        this.baseClass = baseClass;
        this.classLoader = new URLClassLoader(new URL[0], baseClass.getClassLoader());
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
        Object[] result = new Object[constructor.getParameterTypes().length];
        System.arraycopy(parameters, 0, result, 0, result.length-1);
        result[result.length-1] = slice(lastElement(constructor.getParameterTypes()).getComponentType(), parameters, result.length-1);
        return result;
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
        for (Constructor constructor : stubClass.getDeclaredConstructors())
            if (isCompatible(constructor, parameters)) return constructor;
        throw new SimpleStubException("Unable to instantiate stub for " + stubClass.getSuperclass().getName() + " because no constructor matches " + Arrays.toString(parameters));
    }

    private boolean isCompatible(Constructor constructor, Object[] parameters) throws NoSuchMethodException {
        return isCompatible(constructor.getParameterTypes(), parameters, isVarArgs(constructor));
    }

    @SuppressWarnings("unchecked")
    private boolean isVarArgs(Constructor constructor) throws NoSuchMethodException {
        return constructor.getDeclaringClass().getSuperclass().getDeclaredConstructor(constructor.getParameterTypes()).isVarArgs();
    }

    private boolean isCompatible(Class[] formalArguments, Object[] actualParameters, boolean isVarArgs) {
        if (isVarArgs)
            return areCompatibleVarArgs(formalArguments, actualParameters);
        else
            return areCompatibleFixedArgs(formalArguments, actualParameters);
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

        return loadStubClass();
    }

    private void validateBaseClass() {
        SimpleStub annotation = baseClass.getAnnotation(SimpleStub.class);
        if (annotation == null)
            throw new SimpleStubException("No @SimpleStub annotation defined for class " + baseClass.getName());
        if (!isAbstractClass())
            throw new SimpleStubException("Class " + baseClass.getName() + " is marked with @SimpleStub but is not abstract");
        if (isPackageOrPrivateClass())
            throw new SimpleStubException("Class " + baseClass.getName() + " is marked with @SimpleStub but is not public or protected");
    }

    private boolean isAbstractClass() {
        return Modifier.isAbstract(baseClass.getModifiers());
    }

    private boolean isPackageOrPrivateClass() {
        return !Modifier.isPublic(baseClass.getModifiers()) && !Modifier.isProtected(baseClass.getModifiers());
    }

    private Class<?> loadStubClass() {
        try {
            String stubClassName = createStubClassName(baseClass.getName());
            return createStubClass(stubClassName);
        } catch (NotFoundException e) {
            throw new SimpleStubException("Unable to create stub class", e);
        } catch (CannotCompileException e) {
            throw new SimpleStubException("Unable to create stub class", e);
        }
    }

    private Class<?> createStubClass(String stubClassName) throws NotFoundException, CannotCompileException {
        CtClass ctClass = pool.makeClass(stubClassName, pool.get(baseClass.getName()));
        for (CtMethod method : ctClass.getMethods()) {
            if (isAbstract(method))
                addStubMethod(ctClass, method);
        }
        return ctClass.toClass(classLoader, null);
    }

    private void addStubMethod(CtClass ctClass, CtMethod method) throws CannotCompileException, NotFoundException {
        if (isPackagePrivate(method))
            throw new SimpleStubException("Unable to generate stub method for " + baseClass.getName() + '.' + method.getLongName() + " because it is package private");
        else
            addNewMethod(ctClass, createMethod(ctClass, method));
    }

    private void addNewMethod(CtClass ctClass, CtMethod method) throws CannotCompileException, NotFoundException {
        if (isStrictStub())
            defineMethodToThrowException(method);
        ctClass.addMethod(method);
    }

    private void defineMethodToThrowException(CtMethod method) throws CannotCompileException {
        method.setBody("{ throw new com.meterware.simplestub.SimpleStubException( \"unexpected call to method " +
                withoutSuffix(method.getLongName()) + "\"); }" );
    }

    private String withoutSuffix(String longName) {
        int i = longName.indexOf(SIMPLESTUB_SUFFIX);
        return longName.substring(0, i) + longName.substring(i + SIMPLESTUB_SUFFIX.length());
    }

    private boolean isStrictStub() {
        return baseClass.getAnnotation(SimpleStub.class).strict();
    }

    private CtMethod createMethod(CtClass ctClass, CtMethod method) throws NotFoundException, CannotCompileException {
        return CtNewMethod.make(method.getModifiers() & ~javassist.Modifier.ABSTRACT,
                method.getReturnType(), method.getName(), method.getParameterTypes(), method.getExceptionTypes(), null, ctClass);
    }

    private boolean isAbstract(CtMethod method) {
        return javassist.Modifier.isAbstract(method.getModifiers());
    }

    private boolean isPackagePrivate(CtMethod method) {
        return javassist.Modifier.isPackage(method.getModifiers());
    }

    private String createStubClassName(String className) {
        return className + SIMPLESTUB_SUFFIX;
    }

}
