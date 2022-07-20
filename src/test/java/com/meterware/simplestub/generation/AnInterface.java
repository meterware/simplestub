package com.meterware.simplestub.generation;
/*
 * Copyright (c) 2015-2022 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * An interface that returns a variety of types.
 *
 * @author Russell Gold
 */
interface AnInterface {
    boolean isTrue();

    byte getByte();

    char getChar();

    short getShort();

    int getInt();

    long getLong();

    float getFloat();

    double getDouble();

    String getString();

    void doNothing();

    default byte[] getByteArray() {
        return "Result".getBytes(StandardCharsets.UTF_8);
    }

    int[][] getIntArrayArray();

    List<Integer> getIntList();
}
