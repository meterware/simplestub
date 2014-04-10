package com.meterware.simplestub;

import com.meterware.simplestub.classes.AbstractImplementation;
import com.meterware.simplestub.classes.ClassWithConstructorParameters;
import com.meterware.simplestub.classes.ConcreteClass;
import com.meterware.simplestub.classes.Interface1;
import org.junit.Test;

import java.math.BigInteger;
import java.net.CookiePolicy;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests the Javassist-based runtime stub-loader.
 */
public class StubLoaderTest {

    @Test(expected = SimpleStubException.class)
    public void whenClassNotAbstract_throwException() {
        Stub.createStub(ConcreteClass.class);
    }

    @Test
    public void createdStub_isAssignableFromBaseClass() {
        assertThat(Stub.createStub(SimpleAbstractTestClass.class), instanceOf(SimpleAbstractTestClass.class));
    }

    @Test
    public void createdStub_runsDefinedMethod() throws IllegalAccessException, InstantiationException {
        SimpleAbstractTestClass testObject = Stub.createStub(SimpleAbstractTestClass.class);
        assertThat(testObject.getName(), is("name"));
    }

    @Test
    public void whenClassNotAnnotated_generateStub() {
        UnannotatedClass testObject = Stub.createStub(UnannotatedClass.class);
        assertThat(testObject.doIt(), is(0L));
    }

    @Test
    public void whenClassIsPackagePrivate_createStub() {
        PackagePrivateClass testObject = Stub.createStub(PackagePrivateClass.class);
        assertThat(testObject.doIt(), is(0));
    }

    @Test
    public void whenAbstractMethodIsPackagePrivate_handleNormally() throws IllegalAccessException, InstantiationException {
        SimpleAbstractTestClass testObject = Stub.createStub(SimpleAbstractTestClass.class);
        assertThat(testObject.packagePrivateMethod(null), is(false));
    }

    @Test
    public void createdStub_generatesNoArgMethod() throws IllegalAccessException, InstantiationException {
        SimpleAbstractTestClass testObject = Stub.createStub(SimpleAbstractTestClass.class);
        assertThat(testObject.getPolicy(), nullValue());
    }

    @Test
    public void createdStub_generatesNoArgMethodInProtectedSubclass() throws IllegalAccessException, InstantiationException {
        ProtectedClass testObject = Stub.createStub(ProtectedClass.class);
        assertThat(testObject.doIt(), nullValue());
    }

    @Test
    public void createdStub_generatesOneArgMethod() throws IllegalAccessException, InstantiationException {
        SimpleAbstractTestClass testObject = Stub.createStub(SimpleAbstractTestClass.class);
        assertThat(testObject.doSomething(5), is(0));
    }

    @Test
    public void createdStub_generatesMultiArgMethod() throws IllegalAccessException, InstantiationException, NoSuchMethodException {
        SimpleAbstractTestClass testObject = Stub.createStub(SimpleAbstractTestClass.class);
        assertThat(testObject.multiArgumentMethod(7, ""), nullValue());
    }

    @Test
    public void whenConstructorArgumentsSpecified_invokeAppropriateConstructor() {
        ClassWithConstructorParameters testObject = Stub.createStub(ClassWithConstructorParameters.class, 5, "age");
        assertThat(testObject.getId(), is("age:5"));
    }

    @Test
    public void whenArgumentsMatchVarArgConstructor_invokeAppropriateConstructor() {
        ClassWithConstructorParameters testObject = Stub.createStub(ClassWithConstructorParameters.class, new ArrayList(), "height", "age", null);
        assertThat(testObject.getId(), is("height:3"));
    }

    @Test(expected = SimpleStubException.class)
    public void whenArgumentsDontMatchMatchVarArgConstructor_throwException() {
        ClassWithConstructorParameters testObject = Stub.createStub(ClassWithConstructorParameters.class, null, new ArrayList());
        assertThat(testObject.getId(), is("height:3"));
    }

    @Test
    public void whenMethodsAreInherited_generateStubs() {
        AbstractImplementation testObject = Stub.createStub(AbstractImplementation.class);
        assertThat(testObject.getLength(), is(0));
    }

    @Test(expected = SimpleStubException.class)
    public void whenTooFewArgumentsSpecified_throwException() {
        Stub.createStub(ClassWithConstructorParameters.class, 5);
    }

    @Test(expected = SimpleStubException.class)
    public void whenTooManyArgumentsSpecified_throwException() {
        Stub.createStub(ClassWithConstructorParameters.class, 5, "age", null);
    }

    @Test(expected = SimpleStubException.class)
    public void whenWrongArgumentTypesSpecified_throwException() {
        Stub.createStub(ClassWithConstructorParameters.class, "age", 7);
    }

    @Test(expected = UnexpectedMethodCallException.class)
    public void whenStrictAnnotationUsed_throwException() {
        StrictClass strictClass = Stub.createStub(StrictClass.class);
        strictClass.doIt();
    }

    @Test(expected = UnexpectedMethodCallException.class)
    public void whenCreateStrictCalled_throwException() {
        ProtectedClass strictClass = Stub.createStrictStub(ProtectedClass.class);
        strictClass.doIt();
    }

    @Test(expected = SimpleStubException.class)
    public void whenErrorInstantiating_throwException() {
        Stub.createStub(ClassWithConstructorParameters.class, true);
    }

    @Test
    public void whenBaseClassIsInterface_generateStub() {
        Interface1 testObject = Stub.createStub(Interface1.class);
        assertThat(testObject.getAge(), is(0));
    }

    @Test
    public void whenNonStaticInnerClassWithMatchingArguments_throwInformativeException() {
        try {
            Stub.createStub(ProblemClass.class, 4);
        } catch (SimpleStubException e) {
            assertThat(e.getMessage(), containsString("This appears to be a non-static inner class, but the first parameter is not the enclosing class."));
        }
    }

    @Test
    public void whenStaticInnerClassWithoutMatchingArguments_throwOrdinaryException() {
        try {
            Stub.createStub(ProtectedClass.class, 4);
        } catch (SimpleStubException e) {
            assertThat(e.getMessage(), not(containsString("This appears to be a non-static inner class")));
        }
    }

    @Test
    public void whenBaseClassInJDK_useDefaultClassLoader() {
        assertThat(Stub.createStub(Remote.class), instanceOf( Remote.class));
    }

    abstract public static class UnannotatedClass {
        abstract protected long doIt();
    }

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

        protected abstract String multiArgumentMethod(int value1, String value2);

        abstract boolean packagePrivateMethod(List<BigInteger> list);

        public abstract CookiePolicy getPolicy();
    }

    abstract class ProblemClass {
        ProblemClass(int i) {aNumber = i;}
        int aNumber;
    }
}
