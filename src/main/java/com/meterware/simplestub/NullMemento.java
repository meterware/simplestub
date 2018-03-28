package com.meterware.simplestub;
/*
 * Copyright (c) 2015-2016 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

/**
 * @author Russell Gold
 */
class NullMemento implements Memento {
    @Override
    public void revert() {}

    @Override
    public <T> T getOriginalValue() {
        throw new UnsupportedOperationException();
    }
}
