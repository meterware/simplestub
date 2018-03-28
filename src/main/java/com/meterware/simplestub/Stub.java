package com.meterware.simplestub;

import com.meterware.simplestub.generation.StubKind;

/**
 * This class allows for the instantiation of auto-generated simple stubs from abstract classes.
 */
@SuppressWarnings("WeakerAccess")
abstract public class Stub {

    /**
     * Instantiates a stub from an abstract class or interface, generating implementations for any abstract methods.
     * Generated methods will do nothing. If they are defined as returning value, they will return zero, false, or null, as appropriate.
     * @param aClass the class from which a stub should be generated.
     * @param parameters any parameters needed for the constructor. If the class is an inner class, the first parameter
     *                   must be the outer class instance.
     * @param <T> the abstract class
     * @return a newly instantiated stub
     */
    public static <T> T createStub(Class<T> aClass, Object... parameters) {
        return createStub(aClass, StubKind.DEFAULT, parameters);
    }

    private static <T> T createStub(Class<T> aClass, StubKind stubKind, Object[] parameters) {
        return new StubLoader(aClass, stubKind).create(parameters);
    }

    /**
     * Instantiates a stub from an abstract class or interface, generating implementations for any abstract methods.
     * Generated methods will do nothing. If they are defined as returning value, they will create an appropriate value:
     * zero, false, empty strings and arrays. Object values will be returned as null, and interface values will be returned
     * as generated 'nice' stubs.
     * @param aClass the class from which a stub should be generated.
     * @param parameters any parameters needed for the constructor. If the class is an inner class, the first parameter
     *                   must be the outer class instance.
     * @param <T> the abstract class
     * @return a newly instantiated stub
     * @since 1.2.2
     */
    public static <T> T createNiceStub(Class<T> aClass, Object... parameters) {
        return createStub(aClass, StubKind.NICE, parameters);
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
        return createStub(aClass, StubKind.STRICT, parameters);
    }

}
