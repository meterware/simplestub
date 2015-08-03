package com.meterware.simplestub.generation;

import java.util.ArrayList;
import java.util.List;

/**
 * A base class for stub generation.
 */
public abstract class StubGenerator {
    private static String[] FACTORY_NAMES = {
            "com.meterware.simplestub.generation.javassist.JavassistStubGeneratorFactory",
            "com.meterware.simplestub.generation.asm.AsmStubGeneratorFactory"
    };

    private static NameFilter nameFilter;
    private static StubGeneratorFactory factory;

    public static void setNameFilter(NameFilter nameFilter) {
        StubGenerator.nameFilter = nameFilter;
    }

    protected static NameFilter getNameFilter() {
        return nameFilter;
    }

    public static StubGenerator create(Class<?> baseClass, boolean strict) {
        return getStubGeneratorFactory().createStubGenerator(baseClass, strict);
    }

    private static StubGeneratorFactory getStubGeneratorFactory() {
        if (factory == null)
            factory = loadStubGeneratoryFactory();
        return factory;
    }

    private static StubGeneratorFactory loadStubGeneratoryFactory() {
        List<String> libraries = new ArrayList<String>();

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
            return (StubGeneratorFactory) Class.forName(factoryName).newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    abstract public Class<?> loadStubClass(String stubClassName, ClassLoader classLoader);
}
