package com.meterware.simplestub.generation;

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
    public StubGenerator createStubGenerator(Class<?> baseClass, boolean strict) {
        return new JavassistStubGenerator(baseClass, strict);
    }

    @Override
    public String getLibraryName() {
        return "javassist";
    }
}
