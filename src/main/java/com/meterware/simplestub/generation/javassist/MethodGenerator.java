package com.meterware.simplestub.generation.javassist;

import javassist.CtMethod;
import javassist.NotFoundException;

interface MethodGenerator {

    String createBody(CtMethod method) throws NotFoundException;

}
