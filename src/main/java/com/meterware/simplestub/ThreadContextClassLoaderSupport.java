package com.meterware.simplestub;
/*
 * Copyright (c) 2015-2018 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import com.meterware.simplestub.generation.StubKind;

/**
 * Support for manipulating the thread context class-loader in a unit test.
 *
 * @author Russell Gold
 */
@SuppressWarnings("WeakerAccess")
public class ThreadContextClassLoaderSupport {

    /**
     * Installs a classloader as the current thread context classloader, returning a memento which can be used to
     * undo the installation.
     * @param classLoader the classloader to install.
     * @return an object which holds the information needed to revert the thread context classloader.
     */
    public static Memento install(ClassLoader classLoader) {
        Memento memento = new ThreadContextClassLoaderMemento();
        Thread.currentThread().setContextClassLoader(classLoader);
        return memento;
    }

    /**
     * Returns a memento to restore the current thread context classloader.
     * @return an object which holds the information needed to revert the thread context classloader.
     */
    public static Memento preserve() {
        return new ThreadContextClassLoaderMemento();
    }

    /**
     * Creates a test implementation or subclass of a given class with a specified name in the current thread context
     * class-loader. Note that the base class must be accessible from the context class-loader, and if not an interface,
     * must have a public no-arg constructor.
     * 
     * Note: Only the class name portion of the name will be used; the package will be computed by SimpleStub,
     * due to JDK restrictions. If an existing class with the same name has been created for a different base class,
     * an exception will be thrown.
     * @param className the name to use for the created class.
     * @param aClass a base class or interface to use as a parent for the specified class.
     * @return the created stub.
     */
    public static Class<?> createStubInThreadContextClassLoader(String className, Class<?> aClass) {
        return new StubLoader(aClass, StubKind.DEFAULT).getStubClassForThread(className);
    }

    static private class ThreadContextClassLoaderMemento implements Memento {
        private ClassLoader originalValue;

        ThreadContextClassLoaderMemento() {
            originalValue = Thread.currentThread().getContextClassLoader();
        }

        @Override
        public void revert() {
            Thread.currentThread().setContextClassLoader(originalValue);
        }

        @Override
        @SuppressWarnings("unchecked")
        public ClassLoader getOriginalValue() {
            return originalValue;
        }
    }
}
