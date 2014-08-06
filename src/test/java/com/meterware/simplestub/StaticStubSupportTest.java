package com.meterware.simplestub;

import org.junit.Test;

import static com.meterware.simplestub.StaticStubSupport.Momento;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
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
    public void whenInstallCalled_superclassStaticValueIsChanged() throws Exception {
        SubStatics.setStringValue("original Value");
        StaticStubSupport.install(SubStatics.class, "stringValue", "test value");
        assertThat(SubStatics.getStringValue(), equalTo("test value"));
    }

    @Test
    public void whenMomentoInvokedAfterInstall_staticValueIsReverted() throws Exception {
        Statics.setStringValue("original Value");
        Momento momento = StaticStubSupport.install(Statics.class, "stringValue", "test value");
        momento.revert();
        assertThat(Statics.getStringValue(), equalTo("original Value"));
    }

    @Test
    public void whenMomentoInvokedAfterInstall_superclassStaticValueIsReverted() throws Exception {
        SubStatics.setStringValue("original Value");
        Momento momento = StaticStubSupport.install(SubStatics.class, "stringValue", "test value");
        momento.revert();
        assertThat(SubStatics.getStringValue(), equalTo("original Value"));
    }

    @Test
    public void afterInstall_retrievePreservedValue() throws Exception {
        Statics.setStringValue("original Value");
        Momento momento = StaticStubSupport.install(Statics.class, "stringValue", "test value");

        String original = momento.getOriginalValue();

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
        Momento momento = StaticStubSupport.preserve(Statics.class, "stringValue");
        Statics.setStringValue("test value");
        momento.revert();
        assertThat(Statics.getStringValue(), equalTo("original Value"));
    }

    @Test
    public void nullObject_doesNothing() {
        Momento momento = Momento.NULL;
        momento.revert();
    }

    @Test(expected = NoSuchFieldException.class)
    public void whenTheFieldNameIsWrong_throwException() throws NoSuchFieldException {
        StaticStubSupport.install(Statics.class, "noSuchValue", "test value");
    }

    static class Statics {
        private static String stringValue;

        public static String getStringValue() {
            return stringValue;
        }

        public static void setStringValue(String stringValue) {
            Statics.stringValue = stringValue;
        }
    }

    static class SubStatics extends Statics {

    }
}
