package com.meterware.simplestub;
/*
 * Copyright (c) 2014-2017 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

import java.io.InputStream;
import java.math.BigInteger;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;

import com.meterware.simplestub.classes.AbstractImplementation;
import com.meterware.simplestub.classes.ClassWithConstructorParameters;
import com.meterware.simplestub.classes.ConcreteClass;
import com.meterware.simplestub.classes.Interface1;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the runtime stub-loader.
 *
 * @author Russell Gold
 */
class StubLoaderTest {

    @Test
    void whenClassNotAbstract_throwException() {
        assertThrows(SimpleStubException.class, ()-> Stub.createStub(ConcreteClass.class));
    }

    @Test
    void createdStub_isAssignableFromBaseClass() {
        assertThat(Stub.createStub(SimpleAbstractTestClass.class), instanceOf(SimpleAbstractTestClass.class));
    }

    @Test
    void createdStub_runsDefinedMethod() {
        SimpleAbstractTestClass testObject = Stub.createStub(SimpleAbstractTestClass.class);
        assertThat(testObject.getName(), is("name"));
    }

    @Test
    void whenClassNotAnnotated_generateStub() {
        UnannotatedClass testObject = Stub.createStub(UnannotatedClass.class);
        assertThat(testObject.doIt(), is(0L));
    }

    @Test
    void whenClassIsPackagePrivate_createStub() {
        PackagePrivateClass testObject = Stub.createStub(PackagePrivateClass.class);
        assertThat(testObject.doIt(), is(0));
    }

    @Test
    void whenAbstractMethodIsPackagePrivate_handleNormally() {
        SimpleAbstractTestClass testObject = Stub.createStub(SimpleAbstractTestClass.class);
        assertThat(testObject.packagePrivateMethod(null), is(false));
    }

    @Test
    void createdStubNoArgMethod_returnsNullWhenReturnNullsEnabled() {
        SimpleAbstractTestClass testObject = Stub.createStub(SimpleAbstractTestClass.class);

        assertThat(testObject.getPolicy(), nullValue(CookiePolicy.class));
    }

    @Test
    void createdNiceStubNoArgMethod_returnsStub() {
        SimpleAbstractTestClass testObject = Stub.createNiceStub(SimpleAbstractTestClass.class);

        assertThat(testObject.getPolicy(), instanceOf(CookiePolicy.class));
    }

    @Test
    void createdStub_generatesNoArgMethodInProtectedSubclass() {
        ProtectedClass testObject = Stub.createStub(ProtectedClass.class);
        assertThat(testObject.doIt(), isEmptyOrNullString());
    }

    @Test
    void createdStub_generatesOneArgMethod() {
        SimpleAbstractTestClass testObject = Stub.createStub(SimpleAbstractTestClass.class);

        assertThat(testObject.doSomething(5), is(0));
    }

    @Test
    void createdStub_generatesMultiArgMethod() {
        SimpleAbstractTestClass testObject = Stub.createStub(SimpleAbstractTestClass.class);

        assertThat(testObject.multiArgumentMethod(7, ""), isEmptyOrNullString());
    }

    @Test
    void whenConstructorArgumentsSpecified_invokeAppropriateConstructor() {
        ClassWithConstructorParameters testObject = Stub.createStub(ClassWithConstructorParameters.class, 5, "age");

        assertThat(testObject.getId(), is("age:5"));
    }

    @Test
    void whenListOfArgumentsMatchesVarArg_invokeVarArgConstructor() {
        ClassWithConstructorParameters testObject
            = Stub.createStub(ClassWithConstructorParameters.class, new ArrayList<>(), "height", "age", null);

        assertThat(testObject.getId(), is("height:3"));
    }

    @Test
    void whenArrayMatchVarArg_invokeVarArgConstructor() {
        ClassWithConstructorParameters testObject
            = Stub.createStub(ClassWithConstructorParameters.class, new ArrayList<>(), new String[] {"height", "age", "sex"});

        assertThat(testObject.getId(), is("height:3"));
    }

    @Test
    void whenArgumentsDontMatchMatchVarArgConstructor_throwException() {
        final ArrayList<Object> list = new ArrayList<>();
        
        assertThrows(SimpleStubException.class,
                     ()-> Stub.createStub(ClassWithConstructorParameters.class, null, list));
    }

    @Test
    void whenMethodsAreInherited_generateStubs() {
        AbstractImplementation testObject = Stub.createStub(AbstractImplementation.class);
        assertThat(testObject.getLength(), is(0));
    }

    @Test
    void whenTooFewArgumentsSpecified_throwException() {
        assertThrows(SimpleStubException.class,
                    ()-> Stub.createStub(ClassWithConstructorParameters.class, 5));
    }

    @Test
    void whenTooManyArgumentsSpecified_throwException() {
        assertThrows(SimpleStubException.class,
                    ()-> Stub.createStub(ClassWithConstructorParameters.class, 5, "age", null));
    }

    @Test
    void whenWrongArgumentTypesSpecified_throwException() {
        assertThrows(SimpleStubException.class,
                    ()-> Stub.createStub(ClassWithConstructorParameters.class, "age", 7));
    }

    @Test
    void whenCreateStrictCalled_throwException() {
        ProtectedClass strictClass = Stub.createStrictStub(ProtectedClass.class);
        assertThrows(UnexpectedMethodCallException.class, strictClass::doIt);
    }

    @Test
    void whenErrorInstantiating_throwException() {
        assertThrows(SimpleStubException.class,
                    ()-> Stub.createStub(ClassWithConstructorParameters.class, true));
    }

    @Test
    void whenBaseClassIsInterface_generateStub() {
        Interface1 testObject = Stub.createStub(Interface1.class);
        assertThat(testObject.getAge(), is(0));
    }

    @Test
    void whenNonStaticInnerClassWithMatchingArguments_throwInformativeException() {
        try {
            Stub.createStub(ProblemClass.class, 4);
        } catch (SimpleStubException e) {
            assertThat(e.getMessage(), containsString("This appears to be a non-static inner class, but the first parameter is not the enclosing class."));
        }
    }

    @Test
    void whenStaticInnerClassWithoutMatchingArguments_throwOrdinaryException() {
        try {
            Stub.createStub(ProtectedClass.class, 4);
        } catch (SimpleStubException e) {
            assertThat(e.getMessage(), not(containsString("This appears to be a non-static inner class")));
        }
    }

    @Test
    void whenBaseClassInJDK_useDefaultClassLoaderForStub() {
        assertThat(Stub.createStub(InputStream.class), instanceOf(InputStream.class));
    }

    @Test
    void whenBaseClassInJDK_useApplicationClassLoaderForStrictStub() {
        assertThat(Stub.createStrictStub(InputStream.class), instanceOf(InputStream.class));
    }

    @Test
    void whenBaseClassInJDK_useDefaultClassLoaderForNiceStub() {
        assertThat(Stub.createNiceStub(InputStream.class), instanceOf(InputStream.class));
    }

    abstract static class UnannotatedClass {
        abstract long doIt();
    }

    abstract static class ProtectedClass {
        abstract String doIt();
    }

    abstract static class PackagePrivateClass {
        abstract int doIt();
    }

    @SuppressWarnings("SameParameterValue")
    abstract static class SimpleAbstractTestClass {

        String getName() { return "name"; }

        abstract int doSomething( int value );

        abstract String multiArgumentMethod( int value1, String value2 );

        abstract boolean packagePrivateMethod(List<BigInteger> list);

        abstract CookiePolicy getPolicy();
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    abstract class ProblemClass {
        ProblemClass(int i) {aNumber = i;}
        int aNumber;
    }
}
