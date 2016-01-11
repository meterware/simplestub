package com.meterware.simplestub.generation;

/**
 * The kinds of stubs to generate.
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
