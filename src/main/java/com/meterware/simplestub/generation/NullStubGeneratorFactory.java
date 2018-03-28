package com.meterware.simplestub.generation;
/*
 * Copyright (c) 2015-2016 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import java.util.List;

/**
 * The stub generator factory selected to report that none of the supporting libraries is available.
 *
 * @author Russell Gold
 */
public class NullStubGeneratorFactory implements StubGeneratorFactory {
    private String libraryList;

    public NullStubGeneratorFactory(List<String> libraryNames) {
        StringBuilder sb = new StringBuilder();
        for (String libraryName : libraryNames)
            appendTo(sb, libraryName);
        libraryList = sb.toString();
    }

    private void appendTo(StringBuilder sb, String libraryName) {
        if (sb.length() > 0)
            sb.append(", ");
        sb.append(libraryName);
    }

    @Override
    public StubGenerator createStubGenerator(Class<?> baseClass, StubKind kind) {
        throw new RuntimeException("No stub generators are available. Ensure that one of the required libraries (" + libraryList + ") is on the class path");
    }

    @Override
    public ClassReferenceFinder getClassReferenceFinder() {
        throw new RuntimeException("No class reference finders are available. . Ensure that one of the required libraries (" + libraryList + ") is on the class path");
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
