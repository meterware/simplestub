package com.meterware.simplestub.generation.asm;

import com.meterware.simplestub.generation.StubGenerator;
import com.meterware.simplestub.generation.StubGeneratorFactory;

/**
 * A factory to create a stub generator using the ASM library.
 */
public class AsmStubGeneratorFactory implements StubGeneratorFactory {
    @Override
    public StubGenerator createStubGenerator(Class<?> baseClass, boolean strict, boolean returnNulls) {
        if (baseClass.isInterface())
            return new AsmStubGenerator(Object.class, strict, returnNulls, baseClass);
        else
            return new AsmStubGenerator(baseClass, strict, returnNulls);
    }

    @Override
    public boolean isAvailable() {
        try {
            Class.forName("org.objectweb.asm.ClassVisitor");
            Class.forName("org.objectweb.asm.commons.GeneratorAdapter");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public String getLibraryName() {
        return "asm and asm-commons";
    }
}
