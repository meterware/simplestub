package com.meterware.simplestub.generation;
/*
 * Copyright (c) 2015-2016 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
/**
 * A class which cannot be created automatically when returned from a stub.
 *
 * @author Russell Gold
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
