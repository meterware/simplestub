package com.meterware.simplestub.generation;

import com.meterware.simplestub.Memento;
import com.meterware.simplestub.StaticStubSupport;
import com.meterware.simplestub.classes.Interface1;
import com.meterware.simplestub.generation.asm.AsmStubGeneratorFactory;
import com.meterware.simplestub.generation.javassist.JavassistStubGeneratorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * A test of the stub generator factory selection. The tests work by manipulating the list of factory names.
 * It would be better to tamper with the classloader in some fashion to hide the expected classes.
 */
public class StubGeneratorTest {

    private List<Memento> mementos = new ArrayList<Memento>();

    @Before
    public void setUp() throws Exception {
        mementos.add(StaticStubSupport.install(StubGenerator.class, "factory", null));
    }

    @After
    public void tearDown() throws Exception {
        for (Memento memento : mementos)
            memento.revert();
    }

    @Test(expected = RuntimeException.class)
    @SuppressWarnings("unchecked")
    public void whenNoFactoriesFound_throwException() throws Exception {
        setUpFactories();

        StubGenerator.create(Interface1.class, false);
    }

    private boolean setUpFactories(Class<? extends StubGeneratorFactory>... knownFactories) throws NoSuchFieldException {
        return mementos.add(StaticStubSupport.install(StubGenerator.class, "FACTORY_NAMES", createFactoryNameList(knownFactories)));
    }

    private String[] createFactoryNameList(Class<? extends StubGeneratorFactory>[] knownFactories) {
        List<String> factoryNames = new ArrayList<String>();
        for (Class<? extends StubGeneratorFactory> factoryClass : knownFactories)
            factoryNames.add(factoryClass.getName());
        return factoryNames.toArray(new String[factoryNames.size()]);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void whenOnlyJavassistFactoryFound_createStubGenerator() throws Exception {
        setUpFactories(JavassistStubGeneratorFactory.class);

        assertThat(StubGenerator.create(Interface1.class, false).getClass().getSimpleName(), equalTo("JavassistStubGenerator"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void whenOnlyAsmFactoryFound_createStubGenerator() throws Exception {
        setUpFactories(AsmStubGeneratorFactory.class);

        assertThat(StubGenerator.create(Interface1.class, false).getClass().getSimpleName(), equalTo("AsmStubGenerator"));
    }
}