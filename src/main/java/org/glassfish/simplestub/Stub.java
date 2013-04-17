package org.glassfish.simplestub;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class allows for the instantiation of auto-generated simple stubs from abstract classes annotated with
 * the @SimpleStub annotation.
 */
public class Stub {

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

    @SuppressWarnings("unchecked")
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
            StringBuilder sb = new StringBuilder();
            sb.append("\nactual parameters: ").append(toString(actualParameterTypes));
            sb.append("\ntried to match constructors with:");
            for (Constructor<?> constructor : stubClass.getDeclaredConstructors()) {
                sb.append('\n').append(toString(constructor.getParameterTypes()));
            }

            throw new SimpleStubException("No matching constructor found for generated stub" + sb);
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
            Class<?> constructorParameterType = constructorParameterTypes[i];
            Class<?> actualParameterType = actualParameterTypes[i];
            if (!isAssignableFrom(constructorParameterType, actualParameterType)) return false;
        }
        return true;
    }

    private static boolean isAssignableFrom(Class<?> constructorParameterType, Class<?> actualParameterType) {
        return constructorParameterType.isAssignableFrom(actualParameterType) ||
                constructorParameterType.isPrimitive() && PRIMITIVE_TYPES.get(constructorParameterType).isAssignableFrom(actualParameterType);
    }

    private static String toString(Class<?>[] parameterTypes) {
        StringBuilder sb = new StringBuilder("(");
        boolean first = true;
        for (Class<?> parameterType : parameterTypes) {
            if (!first) sb.append(", ");
            first = false;
            sb.append(parameterType.getName());
        }
        return sb.append(')').toString();
    }
}
