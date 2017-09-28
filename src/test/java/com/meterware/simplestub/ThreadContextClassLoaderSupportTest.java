package com.meterware.simplestub;

import com.meterware.simplestub.classes.ClassWithConstructorParameters;
import com.meterware.simplestub.classes.ConcreteClass;
import com.meterware.simplestub.classes.Interface1;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.EventListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.typeCompatibleWith;

/**
 * Tests support for context class loaders.
 */
public class ThreadContextClassLoaderSupportTest {
    private static final String CREATED_CLASS_NAME = "a.b.C";
    private ClassLoader savedClassLoader;

    @Before
    public void setUp() throws Exception {
        savedClassLoader = Thread.currentThread().getContextClassLoader();
    }

    @After
    public void tearDown() throws Exception {
        Thread.currentThread().setContextClassLoader(savedClassLoader);
    }

    @Test
    public void whenInstalled_classLoaderIsSetForThread() throws Exception {
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        ThreadContextClassLoaderSupport.install(classLoader);

        assertThat(Thread.currentThread().getContextClassLoader(), sameInstance(classLoader));
    }

    @Test
    public void afterInstalled_retrieveOriginalValue() throws Exception {
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        Memento memento = ThreadContextClassLoaderSupport.install(classLoader);

        assertThat((ClassLoader) memento.getOriginalValue(), sameInstance(savedClassLoader));
    }

    @Test
    public void whenMementoReverted_originalValueIsRestored() throws Exception {
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        Memento memento = ThreadContextClassLoaderSupport.install(classLoader);

        memento.revert();

        assertThat(Thread.currentThread().getContextClassLoader(), sameInstance(savedClassLoader));
    }

    @Test
    public void whenPreserved_originalValueIsUnchanged() throws Exception {
        ThreadContextClassLoaderSupport.preserve();

        assertThat(Thread.currentThread().getContextClassLoader(), sameInstance(savedClassLoader));
    }

    @Test
    public void afterPreservedAndSet_retrieveOriginalClassLoader() throws Exception {
        Memento memento = ThreadContextClassLoaderSupport.preserve();
        Thread.currentThread().setContextClassLoader(new URLClassLoader(new URL[0]));

        assertThat((ClassLoader) memento.getOriginalValue(), sameInstance(savedClassLoader));
    }

    @Test
    public void whenMementoRevertedAfterPreserve_originalClassLoaderIsRestored() throws Exception {
        Memento memento = ThreadContextClassLoaderSupport.preserve();
        Thread.currentThread().setContextClassLoader(new URLClassLoader(new URL[0]));

        memento.revert();
        assertThat(Thread.currentThread().getContextClassLoader(), sameInstance(savedClassLoader));
    }

    @Test
    public void whenClassDefinedFromInterface_retrieveFromClassLoader() throws Exception {
        ClassLoader classLoader = createStubInThreadContextClassLoader(Interface1.class);

        assertThat(classLoader.loadClass(CREATED_CLASS_NAME), typeCompatibleWith(Interface1.class));
    }

    private ClassLoader createStubInThreadContextClassLoader(Class<?> baseClass) {
        ClassLoader classLoader = new URLClassLoader(new URL[0], baseClass.getClassLoader());
        ThreadContextClassLoaderSupport.install(classLoader);
        ThreadContextClassLoaderSupport.createStubInThreadContextClassLoader(CREATED_CLASS_NAME, baseClass);
        return classLoader;
    }

    @Test
    public void whenClassDefinedFromInterface_instantiateWithNoArgConstructor() throws Exception {
        ClassLoader classLoader = createStubInThreadContextClassLoader(Interface1.class);

        assertThat(classLoader.loadClass(CREATED_CLASS_NAME).getDeclaredConstructor().newInstance(), instanceOf(Interface1.class));
    }

    @Test
    public void whenClassDefinedFromJDKInterface_retrieveFromClassLoader() throws Exception {
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        ThreadContextClassLoaderSupport.install(classLoader);
        ThreadContextClassLoaderSupport.createStubInThreadContextClassLoader(CREATED_CLASS_NAME, EventListener.class);

        assertThat(classLoader.loadClass(CREATED_CLASS_NAME), typeCompatibleWith(EventListener.class));
    }

    @Test
    public void whenClassDefinedFromTestClass_retrieveFromClassLoader() throws Exception {
        ClassLoader classLoader = createStubInThreadContextClassLoader(ConcreteClass.class);

        assertThat(classLoader.loadClass(CREATED_CLASS_NAME), typeCompatibleWith(ConcreteClass.class));
    }

    @Test(expected = SimpleStubException.class)
    public void whenBaseClassLacksNoArgContructor_throwException() throws Exception {
        createStubInThreadContextClassLoader(ClassWithConstructorParameters.class);
    }

    @Test(expected = SimpleStubException.class)
    public void whenBaseClassNotAvailableFromCCL_throwException() throws Exception {
        ClassLoader classLoader = createStubInThreadContextClassLoader(ConcreteClass.class);

        ThreadContextClassLoaderSupport.install(new URLClassLoader(new URL[0]));
        ThreadContextClassLoaderSupport.createStubInThreadContextClassLoader("new.Subclass", classLoader.loadClass(CREATED_CLASS_NAME));
    }

    // todo non-public base class in different package
}
