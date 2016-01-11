package com.meterware.simplestub.generation;

/**
 * A class with both a default and non-default constructor. Used for testing auto-creation of returned classes.
 */
@SuppressWarnings("unused")
abstract class ABaseClass implements AnInterface {
    static final int INT_VALUE = 7;
    private String aString;

    public ABaseClass() {
    }

    ABaseClass(String aString) {
        this.aString = aString;
    }

    public String getString() {
        return aString;
    }

    @Override
    public int getInt() {
        return INT_VALUE;
    }

    abstract
    protected int getProtectedInt();
}
