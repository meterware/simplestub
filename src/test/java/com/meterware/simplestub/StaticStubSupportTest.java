package com.meterware.simplestub;
/*
 * Copyright (c) 2014-2022 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

import java.util.ArrayList;
import java.util.EventListener;

import com.meterware.simplestub.classes.ClassWithPrivateNestedClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.meterware.simplestub.StaticStubSupport.nestedClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifies support for static stubs.
 *
 * @author Russell Gold
 */
class StaticStubSupportTest {

    @Test
    void whenInstallCalled_staticValueIsChanged() throws Exception {
        Statics.setStringValue("original Value");
        StaticStubSupport.install(Statics.class, "stringValue", "test value");
        assertThat(Statics.getStringValue(), equalTo("test value"));
    }

    @Test
    void whenInstallCalledForFinalObject_staticValueIsChanged() throws Exception {
        ArrayList<String> testValue = new ArrayList<>();
        StaticStubSupport.install(Statics.class, "finalValue", testValue);
        assertThat(Statics.getFinalValue(), sameInstance(testValue));
    }

    @Test
    void whenInstallCalled_superclassStaticValueIsChanged() throws Exception {
        SubStatics.setStringValue("original Value");
        StaticStubSupport.install(SubStatics.class, "stringValue", "test value");
        assertThat(SubStatics.getStringValue(), equalTo("test value"));
    }

    @Test
    void whenInstallCalledForBoolean_haveOriginalValue() throws Exception {
        boolean originalValue = Statics.isaBoolean();
        Memento memento = StaticStubSupport.install(Statics.class, "aBoolean", true);

        assertThat(memento.getOriginalValue(), equalTo(originalValue));
    }

    @Test
    void whenInstallCalledForChar_haveOriginalValue() throws Exception {
        char originalValue = Statics.getaChar();
        Memento memento = StaticStubSupport.install(Statics.class, "aChar", 'x');

        assertThat(memento.getOriginalValue(), equalTo(originalValue));
    }

    @Test
    void whenInstallCalledForByte_haveOriginalValue() throws Exception {
        byte originalValue = Statics.getaByte();
        Memento memento = StaticStubSupport.install(Statics.class, "aByte", (byte) 12);

        assertThat(memento.getOriginalValue(), equalTo(originalValue));
    }

    @Test
    void whenInstallCalledForShort_haveOriginalValue() throws Exception {
        short originalValue = Statics.getaShort();
        Memento memento = StaticStubSupport.install(Statics.class, "aShort", (short) 123);

        assertThat(memento.getOriginalValue(), equalTo(originalValue));
    }

    @Test
    void whenInstallCalledForInt_haveOriginalValue() throws Exception {
        int originalValue = Statics.getAnInt();
        Memento memento = StaticStubSupport.install(Statics.class, "anInt", 1234);

        assertThat(memento.getOriginalValue(), equalTo(originalValue));
    }

    @Test
    void whenInstallCalledForLong_haveOriginalValue() throws Exception {
        long originalValue = Statics.getaLong();
        Memento memento = StaticStubSupport.install(Statics.class, "aLong", 12345);

        assertThat(memento.getOriginalValue(), equalTo(originalValue));
    }

    @Test
    void whenInstallCalledForFloat_haveOriginalValue() throws Exception {
        float originalValue = Statics.getaFloat();
        Memento memento = StaticStubSupport.install(Statics.class, "aFloat", 123.5F);

        assertThat(memento.getOriginalValue(), equalTo(originalValue));
    }

    @Test
    void whenInstallCalledForDouble_haveOriginalValue() throws Exception {
        double originalValue = Statics.getaDouble();
        Memento memento = StaticStubSupport.install(Statics.class, "aDouble", 123);

        assertThat(memento.getOriginalValue(), equalTo(originalValue));
    }
    @Test
    void whenInstallCalledForBoolean_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "aBoolean", true);
        assertThat(Statics.isaBoolean(), equalTo(true));
    }

    @Test
    void whenInstallCalledForChar_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "aChar", 'x');
        assertThat(Statics.getaChar(), equalTo('x'));
    }

    @Test
    void whenInstallCalledForByte_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "aByte", (byte) 12);
        assertThat(Statics.getaByte(), equalTo((byte) 12));
    }

    @Test
    void whenInstallCalledForShort_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "aShort", (short) 123);
        assertThat(Statics.getaShort(), equalTo((short) 123));
    }

    @Test
    void whenInstallCalledForInt_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "anInt", 1234);
        assertThat(Statics.getAnInt(), equalTo(1234));
    }

    @Test
    void whenInstallCalledForLong_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "aLong", 12345);
        assertThat(Statics.getaLong(), equalTo(12345L));
    }

    @Test
    void whenInstallCalledForFloat_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "aFloat", 123.5F);
        assertThat(Statics.getaFloat(), equalTo(123.5F));
    }

    @Test
    void whenInstallCalledForDouble_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "aDouble", 123);
        assertThat(Statics.getaDouble(), equalTo(123.0));
    }

    @Test
    void whenInstallCalledForFinalBoolean_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "finalBoolean", true);
        assertThat(Statics.isFinalBoolean(), equalTo(true));
    }

    @Test
    void whenInstallCalledForFinalChar_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "finalChar", 'x');
        assertThat(Statics.getFinalChar(), equalTo('x'));
    }

    @Test
    void whenInstallCalledForFinalByte_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "finalByte", (byte) 12);
        assertThat(Statics.getFinalByte(), equalTo((byte) 12));
    }

    @Test
    void whenInstallCalledForFinalShort_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "finalShort", (short) 123);
        assertThat(Statics.getFinalShort(), equalTo((short) 123));
    }

    @Test
    void whenInstallCalledForFinalInt_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "finalInt", 1234);
        assertThat(Statics.getFinalInt(), equalTo(1234));
    }

    @Test
    void whenInstallCalledForFinalLong_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "finalLong", 12345);
        assertThat(Statics.getFinalLong(), equalTo(12345L));
    }

    @Test
    void whenInstallCalledForFinalFloat_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "finalFloat", 123.5F);
        assertThat(Statics.getFinalFloat(), equalTo(123.5F));
    }

    @Test
    void whenInstallCalledForFinalDouble_staticValueIsChanged() throws Exception {
        StaticStubSupport.install(Statics.class, "finalDouble", 123);
        assertThat(Statics.getFinalDouble(), equalTo(123.0));
    }

    @Test
    void whenInstallCalledForFinalWithIncompatibleType() {
        assertThrows(IllegalArgumentException.class,
                    ()-> StaticStubSupport.install(Statics.class, "finalDouble", "abcde"));
    }

    @Test
    void whenMementoInvokedAfterInstall_staticValueIsReverted() throws Exception {
        Statics.setStringValue("original Value");
        Memento memento = StaticStubSupport.install(Statics.class, "stringValue", "test value");
        memento.revert();
        assertThat(Statics.getStringValue(), equalTo("original Value"));
    }

    @Test
    void whenMementoInvokedAfterInstall_superclassStaticValueIsReverted() throws Exception {
        SubStatics.setStringValue("original Value");
        Memento memento = StaticStubSupport.install(SubStatics.class, "stringValue", "test value");
        memento.revert();
        assertThat(SubStatics.getStringValue(), equalTo("original Value"));
    }

    @Test
    void afterInstall_retrievePreservedValue() throws Exception {
        Statics.setStringValue("original Value");
        Memento memento = StaticStubSupport.install(Statics.class, "stringValue", "test value");

        String original = memento.getOriginalValue();

        assertThat(original, is("original Value"));
    }

    @Test
    void whenPreserveCalled_staticValueIsUnchanged() throws Exception {
        Statics.setStringValue("original Value");
        StaticStubSupport.preserve(Statics.class, "stringValue");
        assertThat(Statics.getStringValue(), equalTo("original Value"));
    }

    @Test
    void whenMomentoInvokedAfterPreserve_staticValueIsReverted() throws Exception {
        Statics.setStringValue("original Value");
        Memento memento = StaticStubSupport.preserve(Statics.class, "stringValue");
        Statics.setStringValue("test value");
        memento.revert();
        assertThat(Statics.getStringValue(), equalTo("original Value"));
    }

    @Test
    void nullMemento_doesNothing() {
        final Memento memento = Memento.NULL;

        Assertions.assertDoesNotThrow(memento::revert);
    }

    @Test
    void whenTheFieldNameIsWrong_throwException() {
        assertThrows(NoSuchFieldException.class,
                    ()-> StaticStubSupport.install(Statics.class, "noSuchValue", "test value"));
    }

    @Test
    void whenTheFieldTypeIsWrong_throwException() {
        assertThrows(NoSuchFieldException.class,
                    ()-> StaticStubSupport.install(Statics.class, "finalValue", "test value"));
    }

    @Test
    void canUseStaticMethodToGetInnerClass() throws Exception {
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

        private static final boolean aBoolean = getRandomBoolean();
        private static final char aChar = (char) getRandom(Character.MAX_VALUE);
        private static final byte aByte = (byte) getRandom(Byte.MAX_VALUE);
        private static final short aShort = (short) getRandom(Short.MAX_VALUE);
        private static final int anInt = (int) getRandom(Integer.MAX_VALUE);
        private static final long aLong = (long) getRandom(Long.MAX_VALUE);
        private static final float aFloat = (float) getRandom(Integer.MAX_VALUE);
        private static final double aDouble = getRandom(Integer.MAX_VALUE);

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
