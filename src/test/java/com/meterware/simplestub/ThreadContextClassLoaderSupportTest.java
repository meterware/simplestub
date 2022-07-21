package com.meterware.simplestub;
/*
 * Copyright (c) 2015-2022 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

import java.net.URL;
import java.net.URLClassLoader;
import java.util.EventListener;

import com.meterware.simplestub.classes.ClassWithConstructorParameters;
import com.meterware.simplestub.classes.ConcreteClass;
import com.meterware.simplestub.classes.Interface1;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.typeCompatibleWith;

/**
 * Tests support for context class loaders.
 *
 * @author Russell Gold
 */
class ThreadContextClassLoaderSupportTest {
    private static int testNum = 0;
    private final String proposedClassName = "a.b.C" + (++testNum);
    private String createdClassName;
    private ClassLoader savedClassLoader;

    @BeforeEach
    void setUp() {
        savedClassLoader = Thread.currentThread().getContextClassLoader();
    }

    @AfterEach
    void tearDown() {
        Thread.currentThread().setContextClassLoader(savedClassLoader);
    }

    @Test
    void whenInstalled_classLoaderIsSetForThread() {
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        ThreadContextClassLoaderSupport.install(classLoader);

        assertThat(Thread.currentThread().getContextClassLoader(), sameInstance(classLoader));
    }

    @Test
    void afterInstalled_retrieveOriginalValue() {
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        Memento memento = ThreadContextClassLoaderSupport.install(classLoader);

        assertThat(memento.getOriginalValue(), sameInstance(savedClassLoader));
    }

    @Test
    void whenMementoReverted_originalValueIsRestored() {
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        Memento memento = ThreadContextClassLoaderSupport.install(classLoader);

        memento.revert();

        assertThat(Thread.currentThread().getContextClassLoader(), sameInstance(savedClassLoader));
    }

    @Test
    void whenPreserved_originalValueIsUnchanged() {
        ThreadContextClassLoaderSupport.preserve();

        assertThat(Thread.currentThread().getContextClassLoader(), sameInstance(savedClassLoader));
    }

    @Test
    void afterPreservedAndSet_retrieveOriginalClassLoader() {
        Memento memento = ThreadContextClassLoaderSupport.preserve();
        Thread.currentThread().setContextClassLoader(new URLClassLoader(new URL[0]));

        assertThat(memento.getOriginalValue(), sameInstance(savedClassLoader));
    }

    @Test
    void whenMementoRevertedAfterPreserve_originalClassLoaderIsRestored() {
        Memento memento = ThreadContextClassLoaderSupport.preserve();
        Thread.currentThread().setContextClassLoader(new URLClassLoader(new URL[0]));

        memento.revert();
        assertThat(Thread.currentThread().getContextClassLoader(), sameInstance(savedClassLoader));
    }

    @Test
    void whenClassDefinedFromInterface_retrieveFromClassLoader() throws Exception {
        ClassLoader classLoader = createStubInThreadContextClassLoader(Interface1.class);

        assertThat(classLoader.loadClass(getStubClassName()), typeCompatibleWith(Interface1.class));
    }

    private String getStubClassName() {
        return createdClassName;
    }

    private ClassLoader createStubInThreadContextClassLoader(Class<?> baseClass) {
        ClassLoader classLoader = new URLClassLoader(new URL[0], baseClass.getClassLoader());
        ThreadContextClassLoaderSupport.install(classLoader);
        Class<?> stub = ThreadContextClassLoaderSupport.createStubInThreadContextClassLoader(proposedClassName, baseClass);
        createdClassName = stub.getName();
        return classLoader;
    }

    @Test
    void whenClassDefinedFromInterface_instantiateWithNoArgConstructor() throws Exception {
        ClassLoader classLoader = createStubInThreadContextClassLoader(Interface1.class);

        assertThat(classLoader.loadClass(getStubClassName()).getDeclaredConstructor().newInstance(), instanceOf(Interface1.class));
    }

    @Test
    void whenClassDefinedFromJDKInterface_retrieveFromClassLoader() throws Exception {
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        ThreadContextClassLoaderSupport.install(classLoader);
        Class<?> stub = ThreadContextClassLoaderSupport.createStubInThreadContextClassLoader(proposedClassName, EventListener.class);
        createdClassName = stub.getName();

        assertThat(classLoader.loadClass(getStubClassName()), typeCompatibleWith(EventListener.class));
    }

    @Test
    void whenClassDefinedFromTestClass_retrieveFromClassLoader() throws Exception {
        ClassLoader classLoader = createStubInThreadContextClassLoader(ConcreteClass.class);

        assertThat(classLoader.loadClass(getStubClassName()), typeCompatibleWith(ConcreteClass.class));
    }

    @Test
    void whenTwoClassesDefinedWithSameNameAndBase_useFirstClass() {
        ClassLoader classLoader = new URLClassLoader(new URL[0], ConcreteClass.class.getClassLoader());
        ThreadContextClassLoaderSupport.install(classLoader);

        Class<?> stub1 = ThreadContextClassLoaderSupport.createStubInThreadContextClassLoader(proposedClassName, ConcreteClass.class);
        Class<?> stub2 = ThreadContextClassLoaderSupport.createStubInThreadContextClassLoader(proposedClassName, ConcreteClass.class);

        assertThat(stub1, sameInstance(stub2));
    }

    @Test
    void whenTwoClassesDefinedWithSameNameAndDifferentBasePackages_createSeparateClasses() {
        ClassLoader classLoader = new URLClassLoader(new URL[0], ConcreteClass.class.getClassLoader());
        ThreadContextClassLoaderSupport.install(classLoader);

        Class<?> stub1 = ThreadContextClassLoaderSupport.createStubInThreadContextClassLoader(proposedClassName, ConcreteClass.class);
        Class<?> stub2 = ThreadContextClassLoaderSupport.createStubInThreadContextClassLoader(proposedClassName, EventListener.class);

        assertThat(stub1, not(sameInstance(stub2)));
    }

    @Test
    void whenTwoClassesDefinedWithSameNameAndBasePackagesButDifferentClasses_fail() {
        ClassLoader classLoader = new URLClassLoader(new URL[0], ConcreteClass.class.getClassLoader());
        ThreadContextClassLoaderSupport.install(classLoader);

        ThreadContextClassLoaderSupport.createStubInThreadContextClassLoader(proposedClassName, ConcreteClass.class);

        Assertions.assertThrows(SimpleStubException.class,
                () -> ThreadContextClassLoaderSupport.createStubInThreadContextClassLoader(proposedClassName, Interface1.class));
    }

    @Test
    void whenBaseClassLacksNoArgContructor_throwException() {
        Assertions.assertThrows(SimpleStubException.class,
                        () -> createStubInThreadContextClassLoader(ClassWithConstructorParameters.class));
    }

    // todo non-public base class in different package
}
