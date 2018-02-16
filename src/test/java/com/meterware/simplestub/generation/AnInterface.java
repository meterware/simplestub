package com.meterware.simplestub.generation;

import java.util.List;

/**
 * An interface that returns a variety of types.
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

    byte[] getByteArray();

    int[][] getIntArrayArray();

    List<Integer> getIntList();
}
