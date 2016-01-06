package com.meterware.simplestub.generation.javassist;

import com.meterware.simplestub.generation.StubGenerator;
import com.meterware.simplestub.generation.StubGeneratorFactory;

/**
 * A factory for creating Javassist-based stub generators.
 */
public class JavassistStubGeneratorFactory implements StubGeneratorFactory {

    @Override
    public boolean isAvailable() {
        try {
            Class.forName("javassist.CtClass");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public StubGenerator createStubGenerator(Class<?> baseClass, boolean strict, boolean returnNulls) {
        return new JavassistStubGenerator(baseClass, strict, returnNulls);
    }

    @Override
    public String getLibraryName() {
        return "javassist";
    }
}
