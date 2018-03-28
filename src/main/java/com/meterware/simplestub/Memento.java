package com.meterware.simplestub;
/*
 * Copyright (c) 2015 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

/**
 * An object which contains all the information needed to revert something to its original value.
 *
 * @author Russell Gold
 */
public interface Memento {
    Memento NULL = new NullMemento();

    void revert();

    <T> T getOriginalValue();
}
