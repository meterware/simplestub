package com.meterware.simplestub.generation.javassist;

import com.meterware.simplestub.generation.StubGeneratorTestBase;

/**
 * Tests for creating stubs using Javassist.
 */
public class JavassistStubGeneratorTest extends StubGeneratorTestBase {
    private static final JavassistStubGeneratorFactory STUB_GENERATOR_FACTORY = new JavassistStubGeneratorFactory();

    public JavassistStubGeneratorTest() {
        super(STUB_GENERATOR_FACTORY);
    }

    @Override
    protected String getImplementationType() {
        return "JA";
    }
}
