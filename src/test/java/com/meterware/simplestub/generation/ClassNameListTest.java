package com.meterware.simplestub.generation;
/*
 * Copyright (c) 2016 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;

/**
 * A test of the ClassNameList
 *
 * @author Russell Gold
 */
public class ClassNameListTest {

    @Test
    public void whenNullArgVoidMethod_iteratorIsEmpty() throws Exception {
        Iterable<String> list = new ClassNameList("()V");
        assertThat(list, emptyIterable());
    }

    @Test
    public void whenMethodReturnsPrimitive_iteratorIsEmpty() throws Exception {
        Iterable<String> list = new ClassNameList("()I");
        assertThat(list, emptyIterable());
    }

    @Test
    public void whenMethodReturnsClass_iteratorReportsClass() throws Exception {
        Iterable<String> list = new ClassNameList("(II)Ljava/lang/String;");
        assertThat(list, Matchers.contains("java/lang/String"));
    }

    @Test
    public void whenTypeIsClass_iteratorReportsClass() throws Exception {
        Iterable<String> list = new ClassNameList("Ljava/lang/String;");
        assertThat(list, Matchers.contains("java/lang/String"));
    }
}
