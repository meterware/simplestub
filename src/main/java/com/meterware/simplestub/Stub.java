package com.meterware.simplestub;

/**
 * This class allows for the instantiation of auto-generated simple stubs from abstract classes.
 */
abstract public class Stub {

    private static boolean returnNulls = true;

    /**
     * Sets the behavior of generated methods for "nice" stubs that return instances. If 'returnNulls' is true
     * (the default), those methods will always return null. If it is set to false, methods that return String
     * will return the empty string, methods returning arrays will return empty arrays, and methods returning
     * interfaces will return generated stubs. Methods that return class instances will always return null,
     * regardless of this setting. That prevents unexpected behavior which could result from running
     * class constructors.
     *
     * @param returnNulls if true, generated methods for nice stubs will return null.
     */
    public static void setReturnNulls(boolean returnNulls) {
        Stub.returnNulls = returnNulls;
    }

    /**
     * Instantiates a stub from an abstract class or interface, generated implementations of any abstract methods.
     * Generated methods will do nothing. If they are defined as returning value, they will return zero, false, or null, as appropriate.
     * @param aClass the class from which a stub should be generated.
     * @param parameters any parameters needed for the constructor. If the class is an inner class, the first parameter
     *                   must be the outer class instance.
     * @param <T> the abstract class
     * @return a newly instantiated stub
     */
    public static <T> T createStub(Class<T> aClass, Object... parameters) {
        return createStub(aClass, isStrict(aClass), parameters);
    }

    private static <T> T createStub(Class<T> aClass, boolean strict, Object[] parameters) {
        return new StubLoader(aClass, strict, returnNulls).create(parameters);
    }

    private static boolean isStrict(Class<?> aClass) {
        SimpleStub annotation = aClass.getAnnotation(SimpleStub.class);
        return annotation != null && annotation.strict();
    }

    /**
     * Instantiates a stub from an abstract class. Generated methods will throw an exception.
     * @param aClass the class from which a stub should be generated.
     * @param parameters any parameters needed for the constructor. If the class is an inner class, the first parameter
     *                   must be the outer class instance.
     * @param <T> the abstract class
     * @return a newly instantiated stub
     */
    public static <T> T createStrictStub(Class<T> aClass, Object... parameters) {
        return createStub(aClass, true, parameters);
    }

    /**
     * Instantiates a stub from an abstract class
     * @deprecated as of 1.1 use #createStub
     * @param aClass the class from which a stub should be generated.
     * @param parameters any parameters needed for the constructor. If the class is an inner class, the first parameter
     *                   must be the outer class instance.
     * @param <T> the abstract class
     * @return a newly instantiated stub
     */
    public static <T> T create(Class<T> aClass, Object... parameters) {
        return createStub(aClass, parameters);
    }
}
