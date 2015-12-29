package com.meterware.simplestub.classes;

import com.meterware.simplestub.SimpleStub;

import java.util.List;

@SimpleStub
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
