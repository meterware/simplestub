package com.meterware.simplestub;

import com.meterware.simplestub.classes.AbstractImplementation;
import com.meterware.simplestub.classes.ClassWithAbstractPackageMethod;
import com.meterware.simplestub.classes.ClassWithConstructorParameters;
import com.meterware.simplestub.classes.ConcreteClass;
import org.junit.Test;

import java.math.BigInteger;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests the Javassist-based runtime stub-loader.
 */
public class StubLoaderTest {

    @Test(expected = SimpleStubException.class)
    public void whenClassNotAnnotated_throwException() {
        Stub.create(String.class);
    }

    @Test(expected = SimpleStubException.class)
    public void whenClassNotAbstract_throwException() {
        Stub.create(ConcreteClass.class);
    }

    @Test(expected = SimpleStubException.class)
    public void whenClassHasAbstractPackageMethod_throwException() {
        Stub.create(ClassWithAbstractPackageMethod.class);
    }

    @Test(expected = SimpleStubException.class)
    public void whenClassIsPackagePrivate_throwException() {
        Stub.create(PackagePrivateClass.class);
    }

    @Test
    public void createdStub_isAssignableFromBaseClass() {
        assertThat(Stub.create(SimpleAbstractTestClass.class), instanceOf(SimpleAbstractTestClass.class));
    }

    @Test
    public void createdStub_runsDefinedMethod() throws IllegalAccessException, InstantiationException {
        SimpleAbstractTestClass testObject = Stub.create(SimpleAbstractTestClass.class);
        assertThat(testObject.getName(), is("name"));
    }

    @Test
    public void createdStub_generatesNoArgMethod() throws IllegalAccessException, InstantiationException {
        SimpleAbstractTestClass testObject = Stub.create(SimpleAbstractTestClass.class);
        assertThat(testObject.getPolicy(), nullValue());
    }

    @Test
    public void createdStub_generatesNoArgMethodInProtectedSubclass() throws IllegalAccessException, InstantiationException {
        ProtectedClass testObject = Stub.create(ProtectedClass.class);
        assertThat(testObject.doIt(), nullValue());
    }

    @Test
    public void createdStub_generatesOneArgMethod() throws IllegalAccessException, InstantiationException {
        SimpleAbstractTestClass testObject = Stub.create(SimpleAbstractTestClass.class);
        assertThat(testObject.doSomething(5), is(0));
    }

    @Test
    public void createdStub_generatesBooleanMethod() throws IllegalAccessException, InstantiationException {
        SimpleAbstractTestClass testObject = Stub.create(SimpleAbstractTestClass.class);
        assertThat(testObject.doSomething3(null), is(false));
    }

    @Test
    public void createdStub_generatesMultiArgMethod() throws IllegalAccessException, InstantiationException, NoSuchMethodException {
        SimpleAbstractTestClass testObject = Stub.create(SimpleAbstractTestClass.class);
        assertThat(testObject.doSomething2(7, ""), nullValue());
    }

    @Test
    public void whenConstructorArgumentsSpecified_invokeAppropriateConstructor() {
        ClassWithConstructorParameters testObject = Stub.create(ClassWithConstructorParameters.class, 5, "age");
        assertThat(testObject.getId(), is("age:5"));
    }

    @Test
    public void whenArgumentsMatchVarArgConstructor_invokeAppropriateConstructor() {
        ClassWithConstructorParameters testObject = Stub.create(ClassWithConstructorParameters.class, new ArrayList(), "height", "age", null);
        assertThat(testObject.getId(), is("height:3"));
    }

    @Test(expected = SimpleStubException.class)
    public void whenArgumentsDontMatchMatchVarArgConstructor_throwException() {
        ClassWithConstructorParameters testObject = Stub.create(ClassWithConstructorParameters.class, null, new ArrayList());
        assertThat(testObject.getId(), is("height:3"));
    }

    @Test
    public void whenMethodsAreInherited_generateStubs() {
        AbstractImplementation testObject = Stub.create(AbstractImplementation.class);
        assertThat(testObject.getLength(), is(0));
    }

    @Test(expected = SimpleStubException.class)
    public void whenTooFewArgumentsSpecified_throwException() {
        Stub.create(ClassWithConstructorParameters.class, 5);
    }

    @Test(expected = SimpleStubException.class)
    public void whenTooManyArgumentsSpecified_throwException() {
        Stub.create(ClassWithConstructorParameters.class, 5, "age", null);
    }

    @Test(expected = SimpleStubException.class)
    public void whenWrongArgumentTypesSpecified_throwException() {
        Stub.create(ClassWithConstructorParameters.class, "age", 7);
    }

    @Test(expected = SimpleStubException.class)
    public void whenStrictGeneratedMethodCalled_throwException() {
        StrictClass strictClass = Stub.create(StrictClass.class);
        strictClass.doIt();
    }

    @Test(expected = SimpleStubException.class)
    public void whenErrorInstantiating_throwException() {
        Stub.create(ClassWithConstructorParameters.class, true);
    }

    @SimpleStub
    abstract protected static class ProtectedClass {
        abstract protected String doIt();
    }

    @SimpleStub
    abstract static class PackagePrivateClass {
        abstract public int doIt();
    }

    @SimpleStub(strict=true)
    abstract protected static class StrictClass {
        abstract public void doIt();
    }

    @SimpleStub
    abstract public static class SimpleAbstractTestClass {

        public String getName() { return "name"; }

        protected abstract int doSomething(int value);

        protected abstract String doSomething2(int value1, String value2);

        protected abstract boolean doSomething3(List<BigInteger> list);

        public abstract CookiePolicy getPolicy();
    }
}
