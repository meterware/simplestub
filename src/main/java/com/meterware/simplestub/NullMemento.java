package com.meterware.simplestub;

class NullMemento implements StaticStubSupport.Momento {
    @Override
    public void revert() {}

    @Override
    public <T> T getOriginalValue() {
        throw new UnsupportedOperationException();
    }
}
