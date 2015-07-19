package com.meterware.simplestub.generation;

import com.meterware.simplestub.generation.asm.AsmStubGeneratorFactory;
import com.meterware.simplestub.generation.javassist.JavassistStubGeneratorFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A base class for stub generation.
 */
public abstract class StubGenerator {
    private static StubGeneratorFactory[] FACTORIES = {new JavassistStubGeneratorFactory(), new AsmStubGeneratorFactory()};

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

        for (StubGeneratorFactory candidate : FACTORIES)
            if (candidate.isAvailable())
                return candidate;
            else
                libraries.add(candidate.getLibraryName());

        return new NullStubGeneratorFactory(libraries);
    }

    abstract public Class<?> loadStubClass(String stubClassName, ClassLoader classLoader);
}
