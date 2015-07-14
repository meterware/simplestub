package com.meterware.simplestub.generation;

import java.util.List;

/**
 * The stub generator factory selected to report that none of the supporting libraries is available.
 */
public class NullStubGeneratorFactory implements StubGeneratorFactory {
    private String libraryList;

    public NullStubGeneratorFactory(List<String> libraryNames) {
        StringBuilder sb = new StringBuilder(libraryNames.get(0));
        for (int i = 1; i < libraryNames.size(); i++)
            sb.append(", ").append(libraryNames.get(i));
        libraryList = sb.toString();
    }

    @Override
    public StubGenerator createStubGenerator(Class<?> baseClass, boolean strict) {
        throw new RuntimeException("No stub generators are available. Ensure that one of the required libraries (" + libraryList + ") is on the class path");
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public String getLibraryName() {
        return "none";
    }
}
