package com.meterware.simplestub.generation.javassist;

import com.meterware.simplestub.SimpleStubException;
import com.meterware.simplestub.classes.AbstractImplementation;
import com.meterware.simplestub.generation.StubGenerator;
import com.meterware.simplestub.generation.StubGeneratorFactory;
import com.meterware.simplestub.generation.StubKind;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

/**
 * Tests for creating stubs using Javassist.
 */
public class JavassistStubGeneratorTest {

    private static int stubNum = 0;
    private static StubGeneratorFactory factory = new JavassistStubGeneratorFactory();

    private AnInterface anInterfaceStub;
    private boolean returnNulls = true;

    @Before
    public void setUp() throws Exception {
        anInterfaceStub = createStub(AnInterface.class);
    }

    @SuppressWarnings("unchecked")
    private <T> T createStub(Class<T> baseClass) throws InstantiationException, IllegalAccessException {
        Class<T> aStubClass = getOrCreateStubClass(baseClass);
        return create(aStubClass);
    }

    @SuppressWarnings("unchecked")
    private <T> T create(Class<?> aStubClass) throws InstantiationException, IllegalAccessException {
        return (T) aStubClass.newInstance();
    }

    @Test
    public void whenBaseClassIsInterface_stubImplementsInterface() throws Exception {
        Class<?> aStub = getOrCreateStubClass(AnInterface.class);

        assertThat(aStub, typeCompatibleWith(AnInterface.class));
    }

    private <T> Class<T> getOrCreateStubClass(Class<T> baseClass) {
        return createStubClass(baseClass);
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> createStubClass(Class<T> baseClass) {
        String stubClassName = getStubClassName(baseClass);
        try {
            return (Class<T>) getClass().getClassLoader().loadClass(stubClassName);
        } catch (ClassNotFoundException e) {
            return createStubClass(baseClass, stubClassName);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> createStubClass(Class<T> baseClass, String stubClassName) {
        StubGenerator generator = factory.createStubGenerator(baseClass, returnNulls ? StubKind.NICE : StubKind.NON_NULL);

        return (Class<T>) generator.loadStubClass(stubClassName, getClass().getClassLoader());
    }

    private String getStubClassName(Class<?> baseClass) {
        return baseClass.getName() + "_Stub" + (++stubNum);
    }

    @Test
    public void whenBaseClassIsAbstractClass_stubExtendsAbstractClass() throws Exception {
        Class<?> aStub = getOrCreateStubClass(ABaseClass.class);

        assertThat(aStub, typeCompatibleWith(ABaseClass.class));
    }

    @Test
    public void whenBaseClassImplementsInterface_stubImplementsInterface() throws Exception {
        Class<?> aStub = getOrCreateStubClass(AbstractImplementation.class);

        assertThat(aStub, typeCompatibleWith(AbstractImplementation.class));
    }

    @Test
    public void whenMethodsAreInherited_generateStubs() throws Exception {
        AbstractImplementation testObject = createStub(AbstractImplementation.class);

        MatcherAssert.assertThat(testObject.getLength(), is(0));
    }

    @Test
    public void whenBaseClassIsInterface_instantiatedObjectImplementsInterface() throws Exception {
        Class<?> aStubClass = getOrCreateStubClass(AnInterface.class);

        assertThat(aStubClass.newInstance(), instanceOf(AnInterface.class));
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
        enableReturnNulls();
        assertThat(anInterfaceStub.getString(), nullValue());
    }

    private void enableReturnNulls() throws NoSuchFieldException {
        returnNulls = true;
    }

    @Test
    public void whenUndefinedMethodReturnsStringValueAndReturnNullsDisabled_generatedMethodReturnsEmptyString() throws Exception {
        disableReturnNulls();
        anInterfaceStub = createStub(AnInterface.class);

        assertThat(anInterfaceStub.getString(), isEmptyString());
    }

    private void disableReturnNulls() throws NoSuchFieldException {
        returnNulls = false;
    }

    @Test
    public void whenUndefinedMethodReturnsVoid_generatedMethodIsNoOp() throws Exception {
        anInterfaceStub.doNothing();
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
    public void whenUndefinedMethodReturnsInterfaceAndReturnNullsDisabled_generatedMethodReturnsStub() throws Exception {
        disableReturnNulls();
        ClassWithObjectGetters aClassStub = createStub(ClassWithObjectGetters.class);

        assertThat(aClassStub.getAnInterface(), instanceOf(AnInterface.class));
    }

    @Test
    public void whenUndefinedMethodReturnsInterfaceAndReturnNullsEnabled_generatedMethodReturnsNull() throws Exception {
        enableReturnNulls();
        ClassWithObjectGetters aClassStub = createStub(ClassWithObjectGetters.class);

        assertThat(aClassStub.getAnInterface(), nullValue());
    }

    @Test
    public void whenUndefinedMethodReturnsArrayAndReturnNullsEnabled_generatedMethodReturnsNull() throws Exception {
        enableReturnNulls();
        ClassWithObjectGetters aClassStub = createStub(ClassWithObjectGetters.class);

        assertThat(aClassStub.getAnInterfaceArray(), nullValue());
    }

    @Test
    public void whenUndefinedMethodReturnsArrayAndReturnNullsDisabled_generatedMethodReturnsEmptyArray() throws Exception {
        disableReturnNulls();
        ClassWithObjectGetters aClassStub = createStub(ClassWithObjectGetters.class);

        assertThat(aClassStub.getAnInterfaceArray(), Matchers.<AnInterface>emptyArray());
    }

    @Test
    public void whenUndefinedMethodReturnsTwoDArrayAndReturnNullsEnabled_generatedMethodReturnsNull() throws Exception {
        enableReturnNulls();
        ClassWithObjectGetters aClassStub = createStub(ClassWithObjectGetters.class);

        assertThat(aClassStub.getATwoDArray(), nullValue());
    }

    @Test
    public void whenUndefinedMethodReturnsTwoDArrayAndReturnNullsDisabled_generatedMethodReturnsEmptyArray() throws Exception {
        disableReturnNulls();
        ClassWithObjectGetters aClassStub = createStub(ClassWithObjectGetters.class);

        assertThat(aClassStub.getATwoDArray(), Matchers.<AnInterface[]>emptyArray());
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
    public void whenAbstractClass_implementAbstractMethods() throws Exception {
        ABaseClass stub = createStub(ABaseClass.class);

        assertThat(stub.getInt(), is(0));
    }

    @Test
    public void whenNoArgContructorUsed_baseClassReceivesValue() throws Exception {
        Class<ABaseClass> aStubClass = getOrCreateStubClass(ABaseClass.class);
        Constructor<ABaseClass> constructor = aStubClass.getDeclaredConstructor(String.class);
        ABaseClass aBaseClass = constructor.newInstance("Test Value");

        assertThat(aBaseClass.getString(), is("Test Value"));
    }

    @Test(expected = SimpleStubException.class)
    public void whenStrictStubGenerated_throwSimpleStubException() throws Exception {
        AnInterface stub = createStrictStub(AnInterface.class);
        stub.getByte();
    }

    private <T> T createStrictStub(Class<T> baseClass) throws InstantiationException, IllegalAccessException {
        Class<T> aStubClass = createStrictStubClass(baseClass);
        return create(aStubClass);
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> createStrictStubClass(Class<T> baseClass) {
        StubGenerator generator = factory.createStubGenerator(baseClass, StubKind.STRICT);

        return (Class<T>) generator.loadStubClass(getStubClassName(baseClass), getClass().getClassLoader());
    }
}
