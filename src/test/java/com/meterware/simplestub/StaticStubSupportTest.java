package com.meterware.simplestub;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Verifies support for static stubs.
 */
public class StaticStubSupportTest {

    @Test
    public void setAndRestore() throws Exception {
        Statics.setStringValue("original Value");
        StaticStubSupport.Momento momento = StaticStubSupport.install(Statics.class, "stringValue", "test value");
        assertThat(Statics.getStringValue(), equalTo("test value"));

        momento.revert();
        assertThat(Statics.getStringValue(), equalTo("original Value"));
    }

    @Test(expected = NoSuchFieldException.class)
    public void whenTheFieldNameIsWrong_throwException() throws NoSuchFieldException {
        StaticStubSupport.Momento momento = StaticStubSupport.install(Statics.class, "noSuchValue", "test value");
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
}
