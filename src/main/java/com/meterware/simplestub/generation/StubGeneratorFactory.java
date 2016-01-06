package com.meterware.simplestub.generation;

/**
 * An interface for creating stub generators.
 */
public interface StubGeneratorFactory {
    StubGenerator createStubGenerator(Class<?> baseClass, StubKind kind);

    boolean isAvailable();

    String getLibraryName();
}
