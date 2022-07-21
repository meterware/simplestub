package com.meterware.simplestub.generation;
/*
 * Copyright (c) 2015-2022 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.meterware.simplestub.Memento;
import com.meterware.simplestub.StaticStubSupport;
import com.meterware.simplestub.classes.Interface1;
import com.meterware.simplestub.generation.asm.AsmStubGeneratorFactory;
import com.meterware.simplestub.generation.javassist.JavassistStubGeneratorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A test of the stub generator factory selection. The tests work by manipulating the list of factory names.
 * It would be better to tamper with the classloader in some fashion to hide the expected classes.
 *
 * @author Russell Gold
 */
class StubGeneratorSelectorTest {

    private final List<Memento> mementos = new ArrayList<>();

    @BeforeEach
    public void setUp() throws Exception {
        mementos.add(StaticStubSupport.install(StubGenerator.class, "factory", null));
    }

    @AfterEach
    public void tearDown() {
        mementos.forEach(Memento::revert);
    }

    @Test
    @SuppressWarnings("unchecked")
    void whenNoFactoriesFound_throwException() throws Exception {
        setUpFactories();

        assertThrows(RuntimeException.class, this::createStubGenerator);
    }

    @SuppressWarnings("unchecked")
    private void setUpFactories(Class<? extends StubGeneratorFactory>... knownFactories) throws NoSuchFieldException {
        mementos.add(StaticStubSupport.install(StubGenerator.class, "FACTORY_NAMES", createFactoryNameList(knownFactories)));
    }

    private StubGenerator createStubGenerator() {
        return StubGenerator.create(Interface1.class, StubKind.DEFAULT);
    }

    private String[] createFactoryNameList(Class<? extends StubGeneratorFactory>[] knownFactories) {
        return Arrays.stream(knownFactories).map(Class::getName).toArray(String[]::new);
    }

    @Test
    @SuppressWarnings("unchecked")
    void whenOnlyJavassistFactoryFound_createStubGenerator() throws Exception {
        setUpFactories(JavassistStubGeneratorFactory.class);

        assertThat(createStubGenerator().getClass().getSimpleName(), equalTo("JavassistStubGenerator"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void whenOnlyAsmFactoryFound_createStubGenerator() throws Exception {
        setUpFactories(AsmStubGeneratorFactory.class);

        assertThat(createStubGenerator().getClass().getSimpleName(), equalTo("AsmStubGenerator"));
    }
}
