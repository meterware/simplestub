package com.meterware.simplestub.generation.javassist;

import javassist.CtMethod;
import javassist.NotFoundException;

class DefaultMethodGenerator implements MethodGenerator {
    @Override
    public String createBody(CtMethod method) throws NotFoundException {
        return null;
    }
}
