package com.meterware.simplestub.generation;
/*
 * Copyright (c) 2015-2016 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
/**
 * A class with both a default and non-default constructor. Used for testing auto-creation of returned classes.
 *
 * @author Russell Gold
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
