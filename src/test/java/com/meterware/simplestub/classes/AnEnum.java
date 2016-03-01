package com.meterware.simplestub.classes;

/**
 * An enum to test class reloading
 */
public enum AnEnum {

    value1 {
        @Override
        public int getSize() {
            return 1;
        }
    }, value2 {
        @Override
        public int getSize() {
            return 2;
        }
    }, value3 {
        @Override
        public int getSize() {
            return 3;
        }
    };

    abstract public int getSize();
}
