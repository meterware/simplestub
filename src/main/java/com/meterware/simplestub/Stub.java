package com.meterware.simplestub;

/**
 * This class allows for the instantiation of auto-generated simple stubs from abstract classes.
 */
abstract public class Stub {

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
        return new StubLoader(aClass, isStrict(aClass)).create(parameters);
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
        return new StubLoader(aClass, true).create(parameters);
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
