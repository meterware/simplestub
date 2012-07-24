package org.glassfish.simplestub;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * This class allows for the instantiation of auto-generated simple stubs from abstract classes annotated with
 * the @SimpleStub annotation.
 */
public class Stub {
    /**
     * Instantiates a stub from an abstract class. The class must have been marked with the @SimpleStub annotation.
     * @param aClass the class from which a stub should be generated.
     * @param parameters any parameters needed for the constructor. If the class is an inner class, the first parameter
     *                   must be the outer class instance.
     * @param <T> the abstract class
     * @return a newly instantiated stub
     */
    public static <T> T create(Class<T> aClass, Object... parameters) {
        try {
            return tryToCreate(aClass, parameters);
        } catch (ClassNotFoundException e) {
            throw new SimpleStubException("No stub defined for " + aClass.getName() + ". Possibly the @SimpleStub annotation was omitted.");
        } catch (Exception e) {
            throw new SimpleStubException("Unable to create stub for " + aClass.getName(), e);
        }
    }

    private static <T> T tryToCreate(Class<T> aClass, Object[] parameters) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<?> stubClass = Class.forName(getStubClassName(aClass));
        if (parameters.length == 0)
            return (T) stubClass.newInstance();
        else {
            Class<?>[] actualParameterTypes = getActualParameterTypes(parameters);
            for (Constructor<?> constructor : stubClass.getDeclaredConstructors()) {
                if (isCompatible(constructor.getParameterTypes(), actualParameterTypes))
                    return (T) constructor.newInstance(parameters);
            }
            throw new SimpleStubException("No matching constructor found for generated stub");
        }
    }

    private static <T> String getStubClassName(Class<T> aClass) {
        if (isOuterClass(aClass))
            return aClass.getName() + ClassGenerator.CLASS_NAME_SUFFIX;
        else
            return aClass.getEnclosingClass().getName() + "__" + aClass.getSimpleName() + ClassGenerator.CLASS_NAME_SUFFIX;
    }

    private static <T> boolean isOuterClass(Class<T> aClass) {
        return aClass.getEnclosingClass() == null;
    }

    private static Class<?>[] getActualParameterTypes(Object[] parameters) {
        Class<?>[] actualParameterTypes = new Class<?>[parameters.length];
        for (int i = 0; i < parameters.length; i++)
            actualParameterTypes[i] = parameters[i].getClass();
        return actualParameterTypes;
    }

    private static boolean isCompatible(Class<?>[] constructorParameterTypes, Class<?>[] actualParameterTypes) {
        if (actualParameterTypes.length != constructorParameterTypes.length) return false;

        for (int i = 0; i < actualParameterTypes.length; i++) {
            if (!constructorParameterTypes[i].isAssignableFrom(actualParameterTypes[i])) return false;
        }
        return true;
    }
}
