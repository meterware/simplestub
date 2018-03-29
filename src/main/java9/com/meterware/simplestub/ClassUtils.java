package com.meterware.simplestub;
/*
 * Copyright (c) 2018 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

import java.lang.invoke.MethodHandles;

/**
 * Utilities to handle JDK-9 or later functionality for defining classes. Note that, due to lack of good tooling
 * support for multi-release jars, this class may not have any dependencies on the rest of SimpleStub. That allows
 * it to be compiled independently.
 *
 * @author Russell Gold
 */
public class ClassUtils {

    /**
     * Defines a new class from bytecode. The class will be defined in the classloader and package associated with a
     * specified 'anchor class'.
     *
     * @param anchorClass the class from which the package and classloader of the new class are to be taken.
     * @param className the name of the class to define
     * @param classBytes the bytes used to define the class
     * @return a new instantiable class, in the package and classloader of the anchor class.
     */
    public static Class<?> defineClass(Class<?> anchorClass, String className, byte[] classBytes) throws IllegalAccessException {
        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(anchorClass, MethodHandles.lookup())
                                            .dropLookupMode(MethodHandles.Lookup.PRIVATE);
        return lookup.defineClass(classBytes);
    }
}
