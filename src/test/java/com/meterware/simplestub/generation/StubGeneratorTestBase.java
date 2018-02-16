package com.meterware.simplestub.generation;

import com.meterware.simplestub.SimpleStubException;
import com.meterware.simplestub.classes.AbstractImplementation;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

abstract public class StubGeneratorTestBase {

    private static int stubNum = 0;
    private StubGeneratorFactory factory;

    private AnInterface anInterfaceStub;

    protected StubGeneratorTestBase(StubGeneratorFactory factory) {
        this.factory = factory;
    }

    protected abstract String getImplementationType();

    @Before
    public void setUp() throws Exception {
        anInterfaceStub = createStub(AnInterface.class);
    }

    @SuppressWarnings("unchecked")
    private <T> T createStub(Class<T> baseClass) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<T> aStubClass = createStubClass(baseClass);
        return create(aStubClass);
    }

    @SuppressWarnings("unchecked")
    private <T> T createNiceStub(Class<T> baseClass) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<T> aStubClass = createNiceStubClass(baseClass);
        return create(aStubClass);
    }

    @SuppressWarnings("unchecked")
    private <T> T create(Class<?> aStubClass) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return (T) aStubClass.getDeclaredConstructor().newInstance();
    }

    @Test
    public void whenBaseClassIsInterface_stubImplementsInterface() throws Exception {
        Class<?> aStub = createStubClass(AnInterface.class);

        assertThat(aStub, typeCompatibleWith(AnInterface.class));
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> createStubClass(Class<T> baseClass) {
        return createStubClass(baseClass, StubKind.DEFAULT);
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> createNiceStubClass(Class<T> baseClass) {
        return createStubClass(baseClass, StubKind.NICE);
    }

    private <T> Class<T> createStubClass(Class<T> baseClass, StubKind kind) {
        return createStubClass(baseClass, getStubClassName(baseClass), kind);
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> createStubClass(Class<T> baseClass, String stubClassName, StubKind kind) {
        StubGenerator generator = factory.createStubGenerator(baseClass, kind);

        return (Class<T>) generator.loadStubClass(stubClassName, getClass().getClassLoader());
    }

    private String getStubClassName(Class<?> baseClass) {
        return baseClass.getName() + "_" + getImplementationType() +  "Stub" + (++stubNum);
    }

    @Test
    public void whenBaseClassIsAbstractClass_stubExtendsAbstractClass() throws Exception {
        Class<?> aStub = createStubClass(ABaseClass.class);

        assertThat(aStub, typeCompatibleWith(ABaseClass.class));
    }

    @Test
    public void whenBaseClassImplementsInterface_stubImplementsInterface() throws Exception {
        Class<?> aStub = createStubClass(AbstractImplementation.class);

        assertThat(aStub, typeCompatibleWith(AbstractImplementation.class));
    }

    @Test
    public void whenMethodsAreInherited_generateStubs() throws Exception {
        AbstractImplementation testObject = createStub(AbstractImplementation.class);

        MatcherAssert.assertThat(testObject.getLength(), is(0));
    }

    @Test
    public void whenMethodsAreInheritedFromBaseClassInterface_generateStubs() throws Exception {
        ADerivedClass testObject = createStub(ADerivedClass.class);

        MatcherAssert.assertThat(testObject.getDouble(), is(0.0));
    }

    @Test
    public void whenBaseClassIsInterface_instantiatedObjectImplementsInterface() throws Exception {
        Class<?> aStubClass = createStubClass(AnInterface.class);

        assertThat(aStubClass.getDeclaredConstructor().newInstance(), instanceOf(AnInterface.class));
    }

    @Test
    public void whenUndefinedMethodReturnsBoolean_generatedMethodReturnsFalse() throws Exception {
        assertThat(anInterfaceStub.isTrue(), is(false));
    }

    @Test
    public void whenUndefinedMethodReturnsIntegerValue_generatedMethodReturnsZero() throws Exception {
        assertThat(anInterfaceStub.getByte(), is((byte)0));
        assertThat(anInterfaceStub.getChar(), is((char)0));
        assertThat(anInterfaceStub.getShort(), is((short)0));
        assertThat(anInterfaceStub.getInt(), is(0));
        assertThat(anInterfaceStub.getLong(), is(0L));
    }

    @Test
    public void whenUndefinedMethodReturnsFloatingPointValue_generatedMethodReturnsZero() throws Exception {
        assertThat(anInterfaceStub.getFloat(), is(0F));
        assertThat(anInterfaceStub.getDouble(), is(0D));
    }

    @Test
    public void whenUndefinedMethodReturnsStringValueAndReturnNullsEnabled_generatedMethodReturnsNull() throws Exception {
        assertThat(anInterfaceStub.getString(), nullValue());
    }

    @Test
    public void whenUndefinedMethodReturnsStringValue_generatedNiceStubMethodReturnsEmptyString() throws Exception {
        assertThat(createNiceStub(AnInterface.class).getString(), isEmptyString());
    }

    @Test
    public void whenUndefinedMethodReturnsVoid_generatedMethodIsNoOp() throws Exception {
        anInterfaceStub.doNothing();
    }

    @Test
    public void whenUndefinedMethodReturnsList_generatedNiceStubMethodIteratorHasNextIsFalse() throws Exception {
        List<Integer> intList = createNiceStub(AnInterface.class).getIntList();

        assertThat(intList.iterator().hasNext(), is(false));
    }

    @Test
    public void whenUndefinedMethodReturnsObjectWithoutNullArgConstructor_generatedMethodReturnsNull() throws Exception {
        ClassWithObjectGetters aClassStub = createStub(ClassWithObjectGetters.class);

        assertThat(aClassStub.getAClassWithNoDefaultConstructor(), nullValue());
    }

    @Test
    public void whenUndefinedMethodReturnsJavaLangObject_generatedMethodReturnsNull() throws Exception {
        ClassWithObjectGetters aClassStub = createStub(ClassWithObjectGetters.class);

        assertThat(aClassStub.getObject(), nullValue());
    }

    @Test
    public void whenUndefinedMethodReturnsInterface_generatedNiceMethodReturnsStub() throws Exception {
        ClassWithObjectGetters aClassStub = createNiceStub(ClassWithObjectGetters.class);

        assertThat(aClassStub.getAnInterface(), instanceOf(AnInterface.class));
    }

    @Test
    public void whenUndefinedMethodReturnsInterfaceAndReturnNullsEnabled_generatedMethodReturnsNull() throws Exception {
        ClassWithObjectGetters aClassStub = createStub(ClassWithObjectGetters.class);

        assertThat(aClassStub.getAnInterface(), nullValue());
    }

    @Test
    public void whenUndefinedMethodReturnsArrayAndReturnNullsEnabled_generatedMethodReturnsNull() throws Exception {
        ClassWithObjectGetters aClassStub = createStub(ClassWithObjectGetters.class);

        assertThat(aClassStub.getAnInterfaceArray(), nullValue());
    }

    @Test
    public void whenUndefinedMethodReturnsArray_generatedNiceMethodReturnsEmptyArray() throws Exception {
        ClassWithObjectGetters aClassStub = createNiceStub(ClassWithObjectGetters.class);

        assertThat(aClassStub.getAnInterfaceArray(), Matchers.emptyArray());
    }

    @Test
    public void whenUndefinedMethodReturnsTwoDArrayAndReturnNullsEnabled_generatedMethodReturnsNull() throws Exception {
        ClassWithObjectGetters aClassStub = createStub(ClassWithObjectGetters.class);

        assertThat(aClassStub.getATwoDArray(), nullValue());
    }

    @Test
    public void whenUndefinedMethodReturnsTwoDArray_generatedNiceMethodReturnsEmptyArray() throws Exception {
        ClassWithObjectGetters aClassStub = createNiceStub(ClassWithObjectGetters.class);

        assertThat(aClassStub.getATwoDArray(), Matchers.emptyArray());
    }

    @Test
    public void whenUndefinedMethodReturnsConcreteClass_generatedMethodReturnsNull() throws Exception {
        ClassWithObjectGetters aClassStub = createStub(ClassWithObjectGetters.class);

        assertThat(aClassStub.getAConcreteClass(), nullValue());
    }

    @Test
    public void whenUndefinedMethodReturnsAbstractClass_generatedMethodReturnsNull() throws Exception {
        ClassWithObjectGetters aClassStub = createStub(ClassWithObjectGetters.class);

        assertThat(aClassStub.getABaseClass(), nullValue());
    }

    @Test
    public void whenAbstractClassImplementsInterfaceMethods_useImplementations() throws Exception {
        ABaseClass stub = createStub(ABaseClass.class);

        assertThat(stub.getInt(), is(ABaseClass.INT_VALUE));
    }

    @Test
    public void whenAbstractMethodIsProtected_generateStubMethod() throws Exception {
        ADerivedClass stub = createStub(ADerivedClass.class);

        assertThat(stub.getProtectedInt(), equalTo(0));
    }

    @Test
    public void whenAbstractMethodIsPackage_generateStubMethod() throws Exception {
        ADerivedClass stub = createNiceStub(ADerivedClass.class);

        assertThat(stub.getPackageString(), isEmptyString());
    }

    @Test
    public void whenOneArgConstructorUsed_baseClassReceivesValue() throws Exception {
        Class<ABaseClass> aStubClass = createStubClass(ABaseClass.class);
        Constructor<ABaseClass> constructor = aStubClass.getDeclaredConstructor(String.class);
        ABaseClass aBaseClass = constructor.newInstance("Test Value");

        String string = aBaseClass.getString();
        assertThat(string, is("Test Value"));
    }

    @Test(expected = SimpleStubException.class)
    public void whenStrictStubGenerated_throwSimpleStubException() throws Exception {
        AnInterface stub = createStrictStub(AnInterface.class);
        stub.getByte();
    }

    private <T> T createStrictStub(Class<T> baseClass) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<T> aStubClass = createStrictStubClass(baseClass);
        return create(aStubClass);
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> createStrictStubClass(Class<T> baseClass) {
        StubGenerator generator = factory.createStubGenerator(baseClass, StubKind.STRICT);

        return (Class<T>) generator.loadStubClass(getStubClassName(baseClass), getClass().getClassLoader());
    }
}
