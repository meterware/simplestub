package com.meterware.simplestub;
/*
 * Copyright (c) 2015-2022 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Tests behavior for setting and restoring system properties.
 *
 * @author Russell Gold
 */
class SystemPropertySupportTest {

    /** The name of a property originally set. **/
    private static final String PROPERTY_1 = "FirstProperty";

    /** The name of a property originally clear. **/
    private static final String PROPERTY_2 = "SecondProperty";

    /** The original value of property 1. */
    private static final String ORIGINAL_VALUE = "original value";

    @BeforeEach
    void setUp() {
        System.setProperty(PROPERTY_1, ORIGINAL_VALUE);
        System.clearProperty(PROPERTY_2);
    }

    @Test
    void whenInstalled_systemPropertyIsChanged() {
        SystemPropertySupport.install(PROPERTY_1, "alternate value");

        assertThat(System.getProperty(PROPERTY_1), is("alternate value"));
    }

    @Test
    void afterPropertyInstalled_retrieveOriginalValue() {
        Memento memento = SystemPropertySupport.install(PROPERTY_1, "alternate value");

        assertThat(memento.getOriginalValue(), is(ORIGINAL_VALUE));
    }

    @Test
    void whenMementoRevertedAfterInstall_originalSystemPropertyValueIsRestored() {
        Memento memento = SystemPropertySupport.install(PROPERTY_1, "alternate value");

        memento.revert();

        assertThat(System.getProperty(PROPERTY_1), is(ORIGINAL_VALUE));
    }

    @Test
    void whenPreserved_systemPropertyIsUnchanged() {
        SystemPropertySupport.preserve(PROPERTY_1);

        assertThat(System.getProperty(PROPERTY_1), is(ORIGINAL_VALUE));
    }

    @Test
    void afterPropertyPreservedAndSet_retrieveOriginalValue() {
        Memento memento = SystemPropertySupport.preserve(PROPERTY_1);
        System.setProperty(PROPERTY_1, "random");

        assertThat(memento.getOriginalValue(), is(ORIGINAL_VALUE));
    }

    @Test
    void whenMementoRevertedAfterPreserve_originalSystemPropertyValueIsRestored() {
        Memento memento = SystemPropertySupport.preserve(PROPERTY_1);
        System.setProperty(PROPERTY_1, "random");

        memento.revert();

        assertThat(System.getProperty(PROPERTY_1), is(ORIGINAL_VALUE));
    }

    @Test
    void whenPropertyOriginallyNotSet_originalValueIsNull() {
        Memento memento = SystemPropertySupport.install(PROPERTY_2, "alternate value");

        assertThat(memento.getOriginalValue(), nullValue());
    }

    @Test
    void whenPropertyOriginallyNotSet_revertRestoresToNull() {
        Memento memento = SystemPropertySupport.install(PROPERTY_2, "alternate value");

        memento.revert();

        assertThat(System.getProperty(PROPERTY_2), nullValue());
    }
}
