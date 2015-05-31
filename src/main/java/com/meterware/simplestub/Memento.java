package com.meterware.simplestub;

/**
 * An object which contains all the information needed to revert something to its original value.
 */
public interface Memento {
    Memento NULL = new NullMemento();

    void revert();

    <T> T getOriginalValue();
}
