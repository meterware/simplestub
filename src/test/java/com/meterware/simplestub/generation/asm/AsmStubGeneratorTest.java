package com.meterware.simplestub.generation.asm;
/*
 * Copyright (c) 2015-2022 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import com.meterware.simplestub.generation.StubGeneratorTestBase;

/**
 * Tests for creating stubs using ASM.
 *
 * @author Russell Gold
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
