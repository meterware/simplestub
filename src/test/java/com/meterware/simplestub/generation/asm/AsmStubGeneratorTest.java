package com.meterware.simplestub.generation.asm;

import com.meterware.simplestub.SimpleStubException;
import com.meterware.simplestub.classes.AbstractImplementation;
import com.meterware.simplestub.classes.ClassWithObjectGetters;
import com.meterware.simplestub.classes.ConcreteClass;
import com.meterware.simplestub.classes.Interface1;
import com.meterware.simplestub.generation.StubGenerator;
import com.meterware.simplestub.generation.StubGeneratorFactory;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

/**
 * Tests for creating stubs using ASM.
 */
public class AsmStubGeneratorTest {

    private static int stubNum = 0;
    private static StubGeneratorFactory factory = new AsmStubGeneratorFactory();
    private AnInterface anInterfaceStub;

    interface AnInterface {
        boolean isTrue();

        byte getByte();
        char getChar();
        short getShort();
        int getInt();
        long getLong();

        float getFloat();
        double getDouble();

        String getString();

        void doNothing();
    }

    @SuppressWarnings("unused")
    abstract static class ABaseClass {
        private String aString;

        public ABaseClass() {
        }

        ABaseClass(String aString) {
            this.aString = aString;
        }

        abstract int getInt();

        String getString() {
            return aString;
        }
    }

    @Before
    public void setUp() throws Exception {
        anInterfaceStub = createStub(AnInterface.class);
    }

    @SuppressWarnings("unchecked")
    private <T> T createStub(Class<T> baseClass) throws InstantiationException, IllegalAccessException {
        Class<T> aStubClass = createStubClass(baseClass);
        return create(aStubClass);
    }

    @SuppressWarnings("unchecked")
    private <T> T create(Class<?> aStubClass) throws InstantiationException, IllegalAccessException {
        return (T) aStubClass.newInstance();
    }

    @Test
    public void whenBaseClassIsInterface_stubImplementsInterface() throws Exception {
        Class<?> aStub = createStubClass(AnInterface.class);

        assertThat(aStub, typeCompatibleWith(AnInterface.class));
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> createStubClass(Class<T> baseClass) {
        StubGenerator generator = factory.createStubGenerator(baseClass, false);

        return (Class<T>) generator.loadStubClass(getStubClassName(baseClass), getClass().getClassLoader());
    }

    private String getStubClassName(Class<?> baseClass) {
        return baseClass.getName() + "_Stub" + (++stubNum);
    }

    @Test
    public void whenBaseClassIsAbstractClass_stubImplementsAbstractClass() throws Exception {
        Class<?> aStub = createStubClass(ABaseClass.class);

        assertThat(aStub, typeCompatibleWith(ABaseClass.class));
    }

    @Test
    public void whenBaseClassImplementsInterface_stubImplementsAbstractClass() throws Exception {
        Class<?> aStub = createStubClass(AbstractImplementation.class);

        assertThat(aStub, typeCompatibleWith(AbstractImplementation.class));
    }

    @Test
    public void whenBaseClassIsInterface_instantiatedObjectImplementsInterface() throws Exception {
        Class<?> aStubClass = createStubClass(AnInterface.class);

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
    public void whenUndefinedMethodReturnsStringValue_generatedMethodReturnsEmptyString() throws Exception {
        assertThat(anInterfaceStub.getString(), equalTo(""));
    }

    @Test
    public void whenUndefinedMethodReturnsVoid_generatedMethodIsNoOp() throws Exception {
        anInterfaceStub.doNothing();
    }

    @Test
    public void whenUndefinedMethodReturnsObjectWithoutNullArgConstructor_generatedMethodReturnsNull() throws Exception {
        ClassWithObjectGetters aClassStub = createStub(ClassWithObjectGetters.class);

        assertThat(aClassStub.getClassWithConstructorParameters(), nullValue());
    }

    @Test
    public void whenUndefinedMethodReturnsInterface_generatedMethodReturnsStub() throws Exception {
        ClassWithObjectGetters aClassStub = createStub(ClassWithObjectGetters.class);

        assertThat(aClassStub.getInterface1(), instanceOf(Interface1.class));
    }

    @Test
    public void whenUndefinedMethodReturnsConcreteClass_generatedMethodReturnsInstance() throws Exception {
        ClassWithObjectGetters aClassStub = createStub(ClassWithObjectGetters.class);

        assertThat(aClassStub.getConcreteClass(), instanceOf(ConcreteClass.class));
    }

    @Test
    public void whenUndefinedMethodReturnsAbstractClass_generatedMethodReturnsStub() throws Exception {
        ClassWithObjectGetters aClassStub = createStub(ClassWithObjectGetters.class);

        assertThat(aClassStub.getAbstractImplementation(), instanceOf(AbstractImplementation.class));
    }

    @Test
    public void whenAbstractClass_implementAbstractMethods() throws Exception {
        ABaseClass stub = createStub(ABaseClass.class);

        assertThat(stub.getInt(), is(0));
    }

    @Test
    public void whenNoArgContructorUsed_baseClassReceivesValue() throws Exception {
        Class<ABaseClass> aStubClass = createStubClass(ABaseClass.class);
        Constructor<ABaseClass> constructor = aStubClass.getConstructor(String.class);
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
        StubGenerator generator = factory.createStubGenerator(baseClass, true);

        return (Class<T>) generator.loadStubClass(getStubClassName(baseClass), getClass().getClassLoader());
    }

    @Test
    public void whenMethodsAreInherited_generateStubs() throws Exception {
        AbstractImplementation testObject = createStub(AbstractImplementation.class);

        MatcherAssert.assertThat(testObject.getLength(), is(0));
    }

}