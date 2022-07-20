package com.meterware.simplestub.generation;
/*
 * Copyright (c) 2015-2022 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import java.util.ArrayList;
import java.util.List;

/**
 * A base class for stub generation.
 *
 * @author Russell Gold
 */
public abstract class StubGenerator {
    private static final String[] FACTORY_NAMES = {
            "com.meterware.simplestub.generation.asm.AsmStubGeneratorFactory",
            "com.meterware.simplestub.generation.javassist.JavassistStubGeneratorFactory"
    };

    private static StubGeneratorFactory factory;

    public static StubGenerator create(Class<?> baseClass, StubKind kind) {
        return getStubGeneratorFactory().createStubGenerator(baseClass, kind);
    }

    public static StubGeneratorFactory getStubGeneratorFactory() {
        if (factory == null)
            factory = loadStubGeneratoryFactory();
        return factory;
    }

    private static StubGeneratorFactory loadStubGeneratoryFactory() {
        List<String> libraries = new ArrayList<>();

        for (String factoryName : FACTORY_NAMES) {
            StubGeneratorFactory candidate = loadCandidate(factoryName);
            if (candidate != null && candidate.isAvailable())
                return candidate;
            else if (candidate != null)
                libraries.add(candidate.getLibraryName());
        }

        return new NullStubGeneratorFactory(libraries);
    }

    private static StubGeneratorFactory loadCandidate(String factoryName) {
        try {
            return (StubGeneratorFactory) Class.forName(factoryName).getConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public abstract Class<?> generateStubClass(String stubClassName, Class<?> anchorClass);

}
