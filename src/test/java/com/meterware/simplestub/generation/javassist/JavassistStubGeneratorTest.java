package com.meterware.simplestub.generation.javassist;
/*
 * Copyright (c) 2015-2022 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

import com.meterware.simplestub.generation.StubGeneratorTestBase;

/**
 * Tests for creating stubs using Javassist.
 *
 * @author Russell Gold
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
