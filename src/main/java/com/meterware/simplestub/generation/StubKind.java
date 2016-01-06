package com.meterware.simplestub.generation;

/**
 * The kinds of stubs to generate.
 */
public enum StubKind {
    NICE {
        @Override
        public String getStubClassSuffix() {
            return SIMPLESTUB_SUFFIX;
        }
    }, NON_NULL {
        @Override
        public String getStubClassSuffix() {
            return SIMPLESTUB_NONNULL_SUFFIX;
        }
    }, STRICT {
        @Override
        public String getStubClassSuffix() {
            return SIMPLESTUB_STRICT_SUFFIX;
        }
    };

    private final static String SIMPLESTUB_SUFFIX = "$$_com_meterware_SimpleStub";
    private final static String SIMPLESTUB_NONNULL_SUFFIX = "$$_com_meterware_SimpleStub_NonNulls";
    private final static String SIMPLESTUB_STRICT_SUFFIX = "$$_com_meterware_SimpleStub_Strict";

    abstract public String getStubClassSuffix();

}
