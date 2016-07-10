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
    public void whenInstallCalledForFinal_staticValueIsChanged() throws Exception {
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

        static {
            finalValue.add("abc");
        }

        public static String getStringValue() {
            return stringValue;
        }

        public static void setStringValue(String stringValue) {
            Statics.stringValue = stringValue;
        }

        static ArrayList<String> getFinalValue() {
            return finalValue;
        }
    }

    static class SubStatics extends Statics {

    }
}
