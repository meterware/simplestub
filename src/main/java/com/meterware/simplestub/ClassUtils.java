package com.meterware.simplestub;
/*
 * Copyright (c) 2018 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * Utilities to handle JDK-specific functionality for defining classes.
 *
 * @author Russell Gold
 */
public class ClassUtils {
    /** The method used to define a class in a classloader. */
    private static java.lang.reflect.Method defineClassMethod;

    static {
        try {
            defineClassMethod = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
                public Method run() throws Exception {
                    Class<?> cl = Class.forName("java.lang.ClassLoader");
                    return cl.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
                }
            });
            defineClassMethod.setAccessible(true);
        } catch (PrivilegedActionException pae) {
            throw new RuntimeException("cannot initialize defineClassMethod", pae.getException());
        }
    }

    /**
     * Defines a new class from bytecode. The class will be defined in the classloader and package associated with a
     * specified 'anchor class'.
     *
     * @param anchorClass the class from which the package and classloader of the new class are to be taken.
     * @param className the name of the class to define
     * @param classBytes the bytes used to define the class
     * @throws ClassFormatError if the bytecode doesn't properly define a class.
     * @throws IllegalAccessException if unable to get access to the defineClassMethod.
     * @return a new instantiable class, in the package and classloader of the anchor class.
     */
    public static Class<?> defineClass(Class<?> anchorClass, String className, byte[] classBytes)
            throws ClassFormatError, IllegalAccessException {
        try {
            return (Class<?>) defineClassMethod.invoke(anchorClass.getClassLoader(), className, classBytes, 0, classBytes.length);
        } catch (InvocationTargetException e) {
            final Throwable targetException = e.getTargetException();
            if (targetException instanceof ClassFormatError) {
                throw (ClassFormatError) targetException;
            } else {
                throw new RuntimeException("Unexpected exception", targetException);
            }
        }
    }
}
