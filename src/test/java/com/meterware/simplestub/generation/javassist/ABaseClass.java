package com.meterware.simplestub.generation.javassist;

/**
 * A class with both a default and non-default constructor. Used for testing auto-creation of returned classes.
 */
@SuppressWarnings("unused")
abstract class ABaseClass implements AnInterface {
    private String aString;

    public ABaseClass() {
    }

    ABaseClass(String aString) {
        this.aString = aString;
    }

    public String getString() {
        return aString;
    }
}
