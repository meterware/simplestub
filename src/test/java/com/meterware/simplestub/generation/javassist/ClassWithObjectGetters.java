package com.meterware.simplestub.generation.javassist;

/**
 * An interface which returns various objects for SimpleStub to stub or instantiate.
 */
public interface ClassWithObjectGetters {

    AnInterface getAnInterface();

    ABaseClass getABaseClass();

    AConcreteClass getAConcreteClass();

    AClassWithNoDefaultConstructor getAClassWithNoDefaultConstructor();

}

