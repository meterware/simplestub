package com.meterware.simplestub;
/*
 * Copyright (c) 2014-2017 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import com.meterware.simplestub.classes.ClassWithPrivateNestedClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.EventListener;

import static com.meterware.simplestub.StaticStubSupport.nestedClass;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Verifies support for static stubs.
 *
 * @author Russell Gold
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
        ArrayList<String> testValue = new ArrayList<>();
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
    public void whenInstallCalledForBoolean_haveOriginalValue() throws Exception {
        boolean originalValue = Statics.isaBoolean();
        Memento memento = StaticStubSupport.install(Statics.class, "aBoolean", true);

        assertThat((boolean) memento.getOriginalValue(), equalTo(originalValue));
    }

    @Test
    public void whenInstallCalledForChar_haveOriginalValue() throws Exception {
        char originalValue = Statics.getaChar();
        Memento memento = StaticStubSupport.install(Statics.class, "aChar", 'x');

        assertThat((char) memento.getOriginalValue(), equalTo(originalValue));
    }

    @Test
    public void whenInstallCalledForByte_haveOriginalValue() throws Exception {
        byte originalValue = Statics.getaByte();
        Memento memento = StaticStubSupport.install(Statics.class, "aByte", (byte) 12);

        assertThat((byte) memento.getOriginalValue(), equalTo(originalValue));
    }

    @Test
    public void whenInstallCalledForShort_haveOriginalValue() throws Exception {
        short originalValue = Statics.getaShort();
        Memento memento = StaticStubSupport.install(Statics.class, "aShort", (short) 123);

        assertThat((short) memento.getOriginalValue(), equalTo(originalValue));
    }

    @Test
    public void whenInstallCalledForInt_haveOriginalValue() throws Exception {
        int originalValue = Statics.getAnInt();
        Memento memento = StaticStubSupport.install(Statics.class, "anInt", 1234);

        assertThat((int) memento.getOriginalValue(), equalTo(originalValue));
    }

    @Test
    public void whenInstallCalledForLong_haveOriginalValue() throws Exception {
        long originalValue = Statics.getaLong();
        Memento memento = StaticStubSupport.install(Statics.class, "aLong", 12345);

        assertThat((long) memento.getOriginalValue(), equalTo(originalValue));
    }

    @Test
    public void whenInstallCalledForFloat_haveOriginalValue() throws Exception {
        float originalValue = Statics.getaFloat();
        Memento memento = StaticStubSupport.install(Statics.class, "aFloat", 123.5F);

        assertThat((float) memento.getOriginalValue(), equalTo(originalValue));
    }

    @Test
    public void whenInstallCalledForDouble_haveOriginalValue() throws Exception {
        double originalValue = Statics.getaDouble();
        Memento memento = StaticStubSupport.install(Statics.class, "aDouble", 123);

        assertThat((double) memento.getOriginalValue(), equalTo(originalValue));
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
        StaticStubSupport.install(Statics.class, "finalByte", (byte) 12);
        assertThat(Statics.getFinalByte(), equalTo((byte) 12));
    }

    @Test
    public void whenInstallCalledForFinalShort_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "finalShort", (short) 123);
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
        StaticStubSupport.install(Statics.class, "finalFloat", 123.5F);
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

    @Test
    public void canUseStaticMethodToGetInnerClass() throws Exception {
        assertThat(nestedClass(ClassWithPrivateNestedClass.class, "ListenerImpl"), typeCompatibleWith(EventListener.class));
    }

    static class Statics {
        private static String stringValue;

        private static final ArrayList<String> finalValue = new ArrayList<>();
        private static final boolean finalBoolean;
        private static final char finalChar;
        private static final byte finalByte;
        private static final short finalShort;
        private static final int finalInt;
        private static final long finalLong;
        private static final float finalFloat;
        private static final double finalDouble;

        private static String aString = "asdf";
        private static boolean aBoolean = getRandomBoolean();
        private static char aChar = (char) getRandom(Character.MAX_VALUE);
        private static byte aByte = (byte) getRandom(Byte.MAX_VALUE);
        private static short aShort = (short) getRandom(Short.MAX_VALUE);
        private static int anInt = (int) getRandom(Integer.MAX_VALUE);
        private static long aLong = (long) getRandom(Long.MAX_VALUE);
        private static float aFloat = (float) getRandom(Integer.MAX_VALUE);
        private static double aDouble = getRandom(Integer.MAX_VALUE);

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

        static boolean getRandomBoolean() {
            return ((short) getRandom(Short.MAX_VALUE)) %2 == 0;
        }

        static double getRandom(double max) {
            return max * Math.random();
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

        static boolean isaBoolean() {
            return aBoolean;
        }

        static char getaChar() {
            return aChar;
        }

        static byte getaByte() {
            return aByte;
        }

        static short getaShort() {
            return aShort;
        }

        static int getAnInt() {
            return anInt;
        }

        static long getaLong() {
            return aLong;
        }

        static float getaFloat() {
            return aFloat;
        }

        static double getaDouble() {
            return aDouble;
        }
    }

    private static class SubStatics extends Statics {

    }
}
