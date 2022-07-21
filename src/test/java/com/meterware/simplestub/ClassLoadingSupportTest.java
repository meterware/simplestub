package com.meterware.simplestub;
/*
 * Copyright (c) 2015-2022 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.meterware.simplestub.classes.AnEnum;
import com.meterware.simplestub.classes.ClassWithPackagePrivateReference;
import com.meterware.simplestub.classes.ClassWithPrivateNestedClass;
import com.meterware.simplestub.classes.PropertyReader;
import com.meterware.simplestub.classes.PropertyReaderImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Tests the class reloading functionality.
 *
 * @author Russell Gold
 */
class ClassLoadingSupportTest {
    private final List<Memento> mementos = new ArrayList<>();

    @AfterEach
    void tearDown() {
        mementos.forEach(Memento::revert);
    }

    @Test
    void byDefaultStaticClassIgnoresPropertyChange() {
        PropertyReader defaultReader = new PropertyReaderImpl();
        mementos.add(SystemPropertySupport.install("test.property", "zork"));
        assertThat(defaultReader.getPropertyValue("test.property"), nullValue());
    }

    @Test
    @SuppressWarnings("all")
    void whenReloadClassCalled_classRunsStaticInitialization() throws Exception {
        mementos.add(SystemPropertySupport.install("test.property", "zork"));
        PropertyReader secondReader = (PropertyReader) ClassLoadingSupport.reloadClass(PropertyReaderImpl.class).getDeclaredConstructor().newInstance();
        assertThat(secondReader.getPropertyValue("test.property"), is("zork"));
    }

    @Test
    void whenClassHasPrivateNestedClass_reloadedClassGetsOwnVersion() throws Exception {
        Object original = getListenerFromInstance(ClassWithPrivateNestedClass.class);
        Object reloadedValue = getListenerFromInstance(ClassLoadingSupport.reloadClass(ClassWithPrivateNestedClass.class));

        assertThat(original.getClass().getName(), equalTo(reloadedValue.getClass().getName()));
        assertThat(original, not(sameInstance(reloadedValue)));
    }

    private Object getListenerFromInstance(Class<?> aClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Method getListenerMethod = aClass.getMethod("getListener");
        return getListenerMethod.invoke(aClass.getDeclaredConstructor().newInstance());
    }

    @Test
    void whenClassHasPackageReference_reloadIt() throws Exception {
        Class<?> aClass = ClassLoadingSupport.reloadClass(ClassWithPackagePrivateReference.class);
        
        assertDoesNotThrow(() -> aClass.getDeclaredConstructor().newInstance());
    }

    @Test
    void whenClassIsEnum_reloadingWorks() throws Exception {
        Class<?> aClass = ClassLoadingSupport.reloadClass(AnEnum.class);

        assertThat(aClass.getEnumConstants(), arrayWithSize(3));
    }
}
