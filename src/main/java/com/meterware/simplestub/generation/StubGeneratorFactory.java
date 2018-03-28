package com.meterware.simplestub.generation;
/*
 * Copyright (c) 2015-2016 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

/**
 * An interface for creating stub generators.
 *
 * @author Russell Gold
 */
public interface StubGeneratorFactory {
    StubGenerator createStubGenerator(Class<?> baseClass, StubKind kind);

    ClassReferenceFinder getClassReferenceFinder();

    boolean isAvailable();

    String getLibraryName();
}
