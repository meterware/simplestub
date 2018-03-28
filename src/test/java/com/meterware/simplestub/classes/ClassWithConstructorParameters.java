package com.meterware.simplestub.classes;
/*
 * Copyright (c) 2014-2016 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import java.util.List;

/**
 * @author Russell Gold
 */
abstract public class ClassWithConstructorParameters {

    private final int size;
    private final String name;

    protected ClassWithConstructorParameters(int size, String name) {
        this.size = size;
        this.name = name;
    }

    protected ClassWithConstructorParameters(List list, String... name) {
        this.size = name.length;
        this.name = size == 0 ? "" : name[0];
    }

    protected ClassWithConstructorParameters(boolean throwInstantiationException) throws InstantiationException {
        size = 0;
        name = "";
        if (throwInstantiationException)
            throw new InstantiationException("throw during unit test");
    }

    public String getId() { return name + ":" + size; }

    abstract public int getCount();
}
