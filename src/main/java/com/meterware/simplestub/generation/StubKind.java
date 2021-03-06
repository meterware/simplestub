package com.meterware.simplestub.generation;
/*
 * Copyright (c) 2016-2018 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

/**
 * The kinds of stubs to generate.
 *
 * @author Russell Gold
 */
public enum StubKind {
    DEFAULT {
        @Override
        public String getStubClassSuffix() {
            return SIMPLESTUB_SUFFIX;
        }
    }, NICE {
        @Override
        public String getStubClassSuffix() {
            return SIMPLESTUB_NICE_SUFFIX;
        }
    }, STRICT {
        @Override
        public String getStubClassSuffix() {
            return SIMPLESTUB_STRICT_SUFFIX;
        }
    };

    private final static String SIMPLESTUB_SUFFIX = "$$_com_meterware_SimpleStub";
    private final static String SIMPLESTUB_NICE_SUFFIX = "$$_com_meterware_SimpleStub_Nice";
    private final static String SIMPLESTUB_STRICT_SUFFIX = "$$_com_meterware_SimpleStub_Strict";

    abstract public String getStubClassSuffix();

}
