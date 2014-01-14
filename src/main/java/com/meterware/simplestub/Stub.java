package com.meterware.simplestub;

/**
 * This class allows for the instantiation of auto-generated simple stubs from abstract classes, optionally annotated with
 * the @SimpleStub annotation.
 */
abstract public class Stub {

    /**
     * Instantiates a stub from an abstract class, generated implementations of any abstract methods. By default,
     * any abstract methods will do nothing, returning a zero, false, or null value; however, if the class is annotated
     * with @SimpleStub and the strict parameter is set true, generated methods will throw an exception.
     * @param aClass the class from which a stub should be generated.
     * @param parameters any parameters needed for the constructor. If the class is an inner class, the first parameter
     *                   must be the outer class instance.
     * @param <T> the abstract class
     * @return a newly instantiated stub
     */
    public static <T> T create(Class<T> aClass, Object... parameters) {
        return new StubLoader(aClass, isStrict(aClass)).create(parameters);
    }

    private static boolean isStrict(Class<?> aClass) {
        SimpleStub annotation = aClass.getAnnotation(SimpleStub.class);
        return annotation != null && annotation.strict();
    }

    /**
     * Instantiates a stub from an abstract class. Any abstract methods will throw an exception.
     * @param aClass the class from which a stub should be generated.
     * @param parameters any parameters needed for the constructor. If the class is an inner class, the first parameter
     *                   must be the outer class instance.
     * @param <T> the abstract class
     * @return a newly instantiated stub
     */
    public static <T> T createStrict(Class<T> aClass, Object... parameters) {
        return new StubLoader(aClass, true).create(parameters);
    }
}
