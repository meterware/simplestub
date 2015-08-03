package com.meterware.simplestub.generation;

/**
 * An interface for creating stub generators.
 */
public interface StubGeneratorFactory {
    StubGenerator createStubGenerator(Class<?> baseClass, boolean strict);

    boolean isAvailable();

    String getLibraryName();
}
