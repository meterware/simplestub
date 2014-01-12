package com.meterware.simplestub.classes;

import com.meterware.simplestub.SimpleStub;

@SimpleStub
abstract public class ClassWithConstructorParameters {

    private final int size;
    private final String name;

    protected ClassWithConstructorParameters(int size, String name) {
        this.size = size;
        this.name = name;
    }

    protected ClassWithConstructorParameters(String... name) {
        this.size = name.length;
        this.name = size == 0 ? "" : name[0];
    }

    public String getId() { return name + ":" + size; }

    abstract public int getCount();
}
