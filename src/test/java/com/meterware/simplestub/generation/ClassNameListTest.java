package com.meterware.simplestub.generation;

import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;

/**
 * A test of the ClassNameList
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
