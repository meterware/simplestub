package com.meterware.simplestub;
/*
 * Copyright (c) 2015 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Tests behavior for setting and restoring system properties.
 *
 * @author Russell Gold
 */
public class SystemPropertySupportTest {

    /** The name of a property originally set. **/
    private static final String PROPERTY_1 = "FirstProperty";

    /** The name of a property originally clear. **/
    private static final String PROPERTY_2 = "SecondProperty";

    /** The original value of property 1. */
    private static final String ORIGINAL_VALUE = "original value";

    @Before
    public void setUp() throws Exception {
        System.setProperty(PROPERTY_1, ORIGINAL_VALUE);
        System.clearProperty(PROPERTY_2);
    }

    @Test
    public void whenInstalled_systemPropertyIsChanged() throws Exception {
        SystemPropertySupport.install(PROPERTY_1, "alternate value");

        assertThat(System.getProperty(PROPERTY_1), is("alternate value"));
    }

    @Test
    public void afterPropertyInstalled_retrieveOriginalValue() throws Exception {
        Memento memento = SystemPropertySupport.install(PROPERTY_1, "alternate value");

        assertThat((String) memento.getOriginalValue(), is(ORIGINAL_VALUE));
    }

    @Test
    public void whenMementoRevertedAfterInstall_originalSystemPropertyValueIsRestored() throws Exception {
        Memento memento = SystemPropertySupport.install(PROPERTY_1, "alternate value");

        memento.revert();

        assertThat(System.getProperty(PROPERTY_1), is(ORIGINAL_VALUE));
    }

    @Test
    public void whenPreserved_systemPropertyIsUnchanged() throws Exception {
        SystemPropertySupport.preserve(PROPERTY_1);

        assertThat(System.getProperty(PROPERTY_1), is(ORIGINAL_VALUE));
    }

    @Test
    public void afterPropertyPreservedAndSet_retrieveOriginalValue() throws Exception {
        Memento memento = SystemPropertySupport.preserve(PROPERTY_1);
        System.setProperty(PROPERTY_1, "random");

        assertThat((String) memento.getOriginalValue(), is(ORIGINAL_VALUE));
    }

    @Test
    public void whenMementoRevertedAfterPreserve_originalSystemPropertyValueIsRestored() throws Exception {
        Memento memento = SystemPropertySupport.preserve(PROPERTY_1);
        System.setProperty(PROPERTY_1, "random");

        memento.revert();

        assertThat(System.getProperty(PROPERTY_1), is(ORIGINAL_VALUE));
    }

    @Test
    public void whenPropertyOriginallyNotSet_originalValueIsNull() throws Exception {
        Memento memento = SystemPropertySupport.install(PROPERTY_2, "alternate value");

        assertThat(memento.getOriginalValue(), nullValue());
    }

    @Test
    public void whenPropertyOriginallyNotSet_revertRestoresToNull() throws Exception {
        Memento memento = SystemPropertySupport.install(PROPERTY_2, "alternate value");

        memento.revert();

        assertThat(System.getProperty(PROPERTY_2), nullValue());
    }
}
