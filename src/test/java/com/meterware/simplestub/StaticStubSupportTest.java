package com.meterware.simplestub;

import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Verifies support for static stubs.
 */
public class StaticStubSupportTest {

    @Test
    public void whenInstallCalled_staticValueIsChanged() throws Exception {
        Statics.setStringValue("original Value");
        StaticStubSupport.install(Statics.class, "stringValue", "test value");
        assertThat(Statics.getStringValue(), equalTo("test value"));
    }

    @Test
    public void whenInstallCalledForFinalObject_staticValueIsChanged() throws Exception {
        ArrayList<String> testValue = new ArrayList<String>();
        StaticStubSupport.install(Statics.class, "finalValue", testValue);
        assertThat(Statics.getFinalValue(), sameInstance(testValue));
    }

    @Test
    public void whenInstallCalled_superclassStaticValueIsChanged() throws Exception {
        SubStatics.setStringValue("original Value");
        StaticStubSupport.install(SubStatics.class, "stringValue", "test value");
        assertThat(SubStatics.getStringValue(), equalTo("test value"));
    }

    @Test
    public void whenInstallCalledForBoolean_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "aBoolean", true);
        assertThat(Statics.isaBoolean(), equalTo(true));
    }

    @Test
    public void whenInstallCalledForChar_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "aChar", 'x');
        assertThat(Statics.getaChar(), equalTo('x'));
    }

    @Test
    public void whenInstallCalledForByte_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "aByte", (byte) 12);
        assertThat(Statics.getaByte(), equalTo((byte) 12));
    }

    @Test
    public void whenInstallCalledForShort_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "aShort", (short) 123);
        assertThat(Statics.getaShort(), equalTo((short) 123));
    }

    @Test
    public void whenInstallCalledForInt_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "anInt", 1234);
        assertThat(Statics.getAnInt(), equalTo(1234));
    }

    @Test
    public void whenInstallCalledForLong_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "aLong", 12345);
        assertThat(Statics.getaLong(), equalTo(12345L));
    }

    @Test
    public void whenInstallCalledForFloat_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "aFloat", 123.5F);
        assertThat(Statics.getaFloat(), equalTo(123.5F));
    }

    @Test
    public void whenInstallCalledForDouble_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "aDouble", 123);
        assertThat(Statics.getaDouble(), equalTo(123.0));
    }

    @Test
    public void whenInstallCalledForFinalBoolean_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "finalBoolean", true);
        assertThat(Statics.isFinalBoolean(), equalTo(true));
    }

    @Test
    public void whenInstallCalledForFinalChar_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "finalChar", 'x');
        assertThat(Statics.getFinalChar(), equalTo('x'));
    }

    @Test
    public void whenInstallCalledForFinalByte_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "finalByte", 12L);
        assertThat(Statics.getFinalByte(), equalTo((byte) 12));
    }

    @Test
    public void whenInstallCalledForFinalShort_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "finalShort", 123);
        assertThat(Statics.getFinalShort(), equalTo((short) 123));
    }

    @Test
    public void whenInstallCalledForFinalInt_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "finalInt", 1234);
        assertThat(Statics.getFinalInt(), equalTo(1234));
    }

    @Test
    public void whenInstallCalledForFinalLong_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "finalLong", 12345);
        assertThat(Statics.getFinalLong(), equalTo(12345L));
    }

    @Test
    public void whenInstallCalledForFinalFloat_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "finalFloat", 123.5);
        assertThat(Statics.getFinalFloat(), equalTo(123.5F));
    }

    @Test
    public void whenInstallCalledForFinalDouble_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "finalDouble", 123);
        assertThat(Statics.getFinalDouble(), equalTo(123.0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenInstallCalledForFinalWithIncompatibleType() throws Exception {
        StaticStubSupport.install(Statics.class, "finalDouble", "abcde");
    }

    @Test
    public void whenMementoInvokedAfterInstall_staticValueIsReverted() throws Exception {
        Statics.setStringValue("original Value");
        Memento memento = StaticStubSupport.install(Statics.class, "stringValue", "test value");
        memento.revert();
        assertThat(Statics.getStringValue(), equalTo("original Value"));
    }

    @Test
    public void whenMementoInvokedAfterInstall_superclassStaticValueIsReverted() throws Exception {
        SubStatics.setStringValue("original Value");
        Memento memento = StaticStubSupport.install(SubStatics.class, "stringValue", "test value");
        memento.revert();
        assertThat(SubStatics.getStringValue(), equalTo("original Value"));
    }

    @Test
    public void afterInstall_retrievePreservedValue() throws Exception {
        Statics.setStringValue("original Value");
        Memento memento = StaticStubSupport.install(Statics.class, "stringValue", "test value");

        String original = memento.getOriginalValue();

        assertThat(original, is("original Value"));
    }

    @Test
    public void whenPreserveCalled_staticValueIsUnchanged() throws Exception {
        Statics.setStringValue("original Value");
        StaticStubSupport.preserve(Statics.class, "stringValue");
        assertThat(Statics.getStringValue(), equalTo("original Value"));
    }

    @Test
    public void whenMomentoInvokedAfterPreserve_staticValueIsReverted() throws Exception {
        Statics.setStringValue("original Value");
        Memento memento = StaticStubSupport.preserve(Statics.class, "stringValue");
        Statics.setStringValue("test value");
        memento.revert();
        assertThat(Statics.getStringValue(), equalTo("original Value"));
    }

    @Test
    public void nullObject_doesNothing() {
        Memento momento = Memento.NULL;
        momento.revert();
    }

    @Test
    public void nullMemento_doesNothing() {
        Memento memento = Memento.NULL;
        memento.revert();
    }

    @Test(expected = NoSuchFieldException.class)
    public void whenTheFieldNameIsWrong_throwException() throws NoSuchFieldException {
        StaticStubSupport.install(Statics.class, "noSuchValue", "test value");
    }

    static class Statics {
        private static String stringValue;

        private static final ArrayList<String> finalValue = new ArrayList<String>();
        private static final boolean finalBoolean;
        private static final char finalChar;
        private static final byte finalByte;
        private static final short finalShort;
        private static final int finalInt;
        private static final long finalLong;
        private static final float finalFloat;
        private static final double finalDouble;

        private static String aString;
        private static boolean aBoolean;
        private static char aChar;
        private static byte aByte;
        private static short aShort;
        private static int anInt;
        private static long aLong;
        private static float aFloat;
        private static double aDouble;

        static {
            finalValue.add("abc");
            finalBoolean = false;
            finalChar = ' ';
            finalByte = 0;
            finalShort = 0;
            finalInt = 0;
            finalLong = 0;
            finalFloat = 0.0f;
            finalDouble = 0.0;
        }

        static String getStringValue() {
            return stringValue;
        }

        static void setStringValue(String stringValue) {
            Statics.stringValue = stringValue;
        }

        static ArrayList<String> getFinalValue() {
            return finalValue;
        }

        static boolean isFinalBoolean() {
            return finalBoolean;
        }

        static char getFinalChar() {
            return finalChar;
        }

        static byte getFinalByte() {
            return finalByte;
        }

        static short getFinalShort() {
            return finalShort;
        }

        static int getFinalInt() {
            return finalInt;
        }

        static long getFinalLong() {
            return finalLong;
        }

        static float getFinalFloat() {
            return finalFloat;
        }

        static double getFinalDouble() {
            return finalDouble;
        }

        public static String getaString() {
            return aString;
        }

        public static boolean isaBoolean() {
            return aBoolean;
        }

        public static char getaChar() {
            return aChar;
        }

        public static byte getaByte() {
            return aByte;
        }

        public static short getaShort() {
            return aShort;
        }

        public static int getAnInt() {
            return anInt;
        }

        public static long getaLong() {
            return aLong;
        }

        public static float getaFloat() {
            return aFloat;
        }

        public static double getaDouble() {
            return aDouble;
        }
    }

    private static class SubStatics extends Statics {

    }
}
