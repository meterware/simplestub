package com.meterware.simplestub;

import javassist.*;

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
public class StubLoader {

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

    private static final String SIMPLESTUB_SUFFIX = "$$_com_meterware_SimpleStub";
    private final ClassPool pool = new ClassPool(ClassPool.getDefault());
    private final Map<ClassLoader, Loader> loaders = new HashMap<ClassLoader, Loader>();
    private final Map<Class<?>, Class<?>> stubClasses = new HashMap<Class<?>, Class<?>>();

    /**
     * Instantiates a stub from an abstract class. The class must have been marked with the @SimpleStub annotation.
     * @param aClass the class from which a stub should be generated.
     * @param parameters any parameters needed for the constructor. If the class is an inner class, the first parameter
     *                   must be the outer class instance.
     * @param <T> the abstract class
     * @return a newly instantiated stub
     */
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> aClass, Object... parameters) {
        try {
            Class<?> stubClass = getStubClass(aClass);
            Constructor<?> constructor = getConstructor(stubClass, parameters);
            constructor.setAccessible(true);
            if (isVarArgs(constructor))
                return (T) constructor.newInstance(toVarArgList(constructor, parameters));
            else
                return (T) constructor.newInstance(parameters);
        } catch (InstantiationException e) {
            throw new SimpleStubException("Unable to instantiate stub for " + aClass.getName(), e);
        } catch (IllegalAccessException e) {
            throw new SimpleStubException("Unable to instantiate stub for " + aClass.getName(), e);
        } catch (NoSuchMethodException e) {
            throw new SimpleStubException("Unable to instantiate stub for " + aClass.getName(), e);
        } catch (InvocationTargetException e) {
            throw new SimpleStubException("Unable to instantiate stub for " + aClass.getName(), e);
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

    <T> Class<?> getStubClass(Class<T> aClass) {
        validate(aClass);

        if (stubClasses.containsKey(aClass))
            return stubClasses.get(aClass);
        else
            return loadStubClass(aClass);
    }

    private <T> void validate(Class<T> aClass) {
        SimpleStub annotation = aClass.getAnnotation(SimpleStub.class);
        if (annotation == null)
            throw new SimpleStubException("No @SimpleStub annotation defined for class " + aClass.getName());
        if (!isAbstract(aClass))
            throw new SimpleStubException("Class " + aClass.getName() + " is marked with @SimpleStub but is not abstract");
        if (isPackageOrPrivate(aClass))
            throw new SimpleStubException("Class " + aClass.getName() + " is marked with @SimpleStub but is not public or protected");
    }

    private <T> boolean isAbstract(Class<T> aClass) {
        return Modifier.isAbstract(aClass.getModifiers());
    }

    private boolean isPackageOrPrivate(Class<?> aClass) {
        return !Modifier.isPublic(aClass.getModifiers()) && !Modifier.isProtected(aClass.getModifiers());
    }

    private <T> Class<?> loadStubClass(Class<T> aClass) {
        try {
            String stubClassName = createStubClassName(aClass.getName());
            defineStubClass(aClass, stubClassName);

            Loader loader = getLoaderFor(aClass);
            Class<?> stubClass = loader.loadClass(stubClassName);
            stubClasses.put(aClass, stubClass);

            return stubClass;
        } catch (NotFoundException e) {
            throw new SimpleStubException("Unable to create stub class", e);
        } catch (ClassNotFoundException e) {
            throw new SimpleStubException("Unable to create stub class", e);
        } catch (CannotCompileException e) {
            throw new SimpleStubException("Unable to create stub class", e);
        }
    }

    private <T> void defineStubClass(Class<T> aClass, String stubClassName) throws NotFoundException, CannotCompileException {
        CtClass ctClass = pool.makeClass(stubClassName, pool.get(aClass.getName()));
        for (CtMethod method : ctClass.getMethods()) {
            if (isAbstract(method))
                addStubMethod(aClass, ctClass, method);
        }
    }

    private <T> void addStubMethod(Class<T> aClass, CtClass ctClass, CtMethod method) throws CannotCompileException, NotFoundException {
        if (isPackagePrivate(method))
            throw new SimpleStubException("Unable to generate stub method for " + aClass.getName() + '.' + method.getLongName() + " because it is package private");
        else
            addNewMethod(aClass, ctClass, createMethod(ctClass, method));
    }

    private void addNewMethod(Class<?> aClass, CtClass ctClass, CtMethod method) throws CannotCompileException, NotFoundException {
        if (isStrictStub(aClass))
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

    private boolean isStrictStub(Class<?> aClass) {
        return aClass.getAnnotation(SimpleStub.class).strict();
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

    private <T> Loader getLoaderFor(Class<T> aClass) {
        Loader loader = getLoaderFor(aClass.getClassLoader());
        loader.delegateLoadingOf(aClass.getName());
        return loader;
    }

    private Loader getLoaderFor(ClassLoader classLoader) {
        if (loaders.containsKey(classLoader))
            return loaders.get(classLoader);
        else
            return createLoader(classLoader);
    }

    private Loader createLoader(ClassLoader classLoader) {
        Loader loader = new Loader(classLoader, pool);
        loader.delegateLoadingOf(SimpleStubException.class.getName());
        loaders.put(classLoader, loader);
        return loader;
    }
}
