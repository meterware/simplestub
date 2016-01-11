package com.meterware.simplestub.generation.asm;

import com.meterware.simplestub.generation.StubGeneratorTestBase;

/**
 * Tests for creating stubs using ASM.
 */
public class AsmStubGeneratorTest extends StubGeneratorTestBase {

    public AsmStubGeneratorTest() {
        super(new AsmStubGeneratorFactory());
    }

    @Override
    protected String getImplementationType() {
        return "ASM";
    }
}
