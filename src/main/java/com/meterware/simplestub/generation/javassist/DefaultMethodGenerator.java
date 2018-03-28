package com.meterware.simplestub.generation.javassist;
/*
 * Copyright (c) 2016 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * @author Russell Gold
 */
class DefaultMethodGenerator implements MethodGenerator {
    @Override
    public String createBody(CtMethod method) throws NotFoundException {
        return null;
    }
}
