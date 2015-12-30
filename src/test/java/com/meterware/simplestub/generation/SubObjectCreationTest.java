package com.meterware.simplestub.generation;

import com.meterware.simplestub.classes.AbstractImplementation;
import com.meterware.simplestub.classes.ClassWithConstructorParameters;
import com.meterware.simplestub.classes.Interface1;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * This test verifies that created stubs which return objects will create stubs for them, when possible.
 */
public class SubObjectCreationTest {

    @Test
    public void whenTypeIsInterface_findNullConstructor() throws Exception {
        assertThat(StubGenerator.hasNullConstructor(Interface1.class), is(true));
    }

    @Test
    public void whenTypeIsAbstractClassWithNullConstructor_findNullConstructor() throws Exception {
        assertThat(StubGenerator.hasNullConstructor(AbstractImplementation.class), is(true));
    }

    @Test
    public void whenTypeIsAbstractClassWithArgumentConstructors_dontFindNullConstructor() throws Exception {
        assertThat(StubGenerator.hasNullConstructor(ClassWithConstructorParameters.class), is(false));
    }
}
