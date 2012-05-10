package org.glassfish.simplestub;

import org.glassfish.simplestub.classes.*;
import org.glassfish.simplestub.classes.ExtendingClass;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class StubCreationTest {

    @Test(expected = SimpleStubException.class)
    public void whenStubNotDefined_throwException() {
        Stub.create(SimpleAbstractTestClass.class);
    }

    @Test(expected = SimpleStubException.class)
    public void whenNoMatchingConstructor_throwException() {
        Stub.create(ExtendingClass.class, 15);
    }

    @Test
    public void whenStubCreatedWithDefaultConstructor_defaultValueIsUsed() {
        ExtendingClass extendingClass = Stub.create(ExtendingClass.class);
        assertEquals(10, extendingClass.doSomething(10));
    }

    @Test
    public void whenStubCreatedWithExplicitConstructor_suppliedValueIsUsed() {
        ExtendingClass extendingClass = Stub.create(ExtendingClass.class, new BigInteger("15"), Arrays.asList(1, 2, 3));
        assertEquals(25, extendingClass.doSomething(10));
    }

    @Test
    public void whenStubIsStaticNestedClass_computeStubClassName() {
        AbstractClass2.InnerClass1 innerClass = Stub.create(AbstractClass2.InnerClass1.class);
        assertEquals(3,innerClass.version());
    }

}
