package com.meterware.simplestub;

/**
 * This class allows for the instantiation of auto-generated simple stubs from abstract classes annotated with
 * the @SimpleStub annotation.
 */
abstract public class Stub {
    /**
     * Instantiates a stub from an abstract class. The class must have been marked with the @SimpleStub annotation.
     * @param aClass the class from which a stub should be generated.
     * @param parameters any parameters needed for the constructor. If the class is an inner class, the first parameter
     *                   must be the outer class instance.
     * @param <T> the abstract class
     * @return a newly instantiated stub
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> aClass, Object... parameters) {
        return new StubLoader().create(aClass, parameters);
    }
}
