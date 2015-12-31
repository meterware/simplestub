package com.meterware.simplestub.generation.javassist;

/**
 * A class which cannot be created automatically when returned from a stub.
 */
public class AClassWithNoDefaultConstructor {
    private int size;

    public AClassWithNoDefaultConstructor(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
