package com.meterware.simplestub.classes;
/*
 * Copyright (c) 2016 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

/**
 * An enum to test class reloading
 *
 * @author Russell Gold
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
