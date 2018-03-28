package com.meterware.simplestub.generation.asm;
/*
 * Copyright (c) 2015-2016 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import com.meterware.simplestub.generation.ClassReferenceFinder;
import com.meterware.simplestub.generation.StubGenerator;
import com.meterware.simplestub.generation.StubGeneratorFactory;
import com.meterware.simplestub.generation.StubKind;

/**
 * A factory to create a stub generator using the ASM library.
 *
 * @author Russell Gold
 */
public class AsmStubGeneratorFactory implements StubGeneratorFactory {

    private ClassReferenceFinder classReferenceFinder;

    @Override
    public StubGenerator createStubGenerator(Class<?> baseClass, StubKind kind) {
        return new AsmStubGenerator(baseClass, kind);
    }

    @Override
    public ClassReferenceFinder getClassReferenceFinder() {
        if (classReferenceFinder == null)
            classReferenceFinder = new AsmClassReferenceFinder();
        return classReferenceFinder;
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
