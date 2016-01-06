package com.meterware.simplestub.generation;

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
