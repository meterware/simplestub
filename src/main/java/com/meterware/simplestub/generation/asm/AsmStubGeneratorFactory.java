package com.meterware.simplestub.generation.asm;

import com.meterware.simplestub.generation.StubGenerator;
import com.meterware.simplestub.generation.StubGeneratorFactory;
import com.meterware.simplestub.generation.StubKind;

/**
 * A factory to create a stub generator using the ASM library.
 */
public class AsmStubGeneratorFactory implements StubGeneratorFactory {
    @Override
    public StubGenerator createStubGenerator(Class<?> baseClass, StubKind kind) {
        if (baseClass.isInterface())
            return new AsmStubGenerator(Object.class, kind, baseClass);
        else
            return new AsmStubGenerator(baseClass, kind);
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
