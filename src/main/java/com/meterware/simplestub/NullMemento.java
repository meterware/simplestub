package com.meterware.simplestub;

class NullMemento implements Memento {
    @Override
    public void revert() {}

    @Override
    public <T> T getOriginalValue() {
        throw new UnsupportedOperationException();
    }
}
