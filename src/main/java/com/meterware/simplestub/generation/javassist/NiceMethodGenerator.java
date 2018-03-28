package com.meterware.simplestub.generation.javassist;
/*
 * Copyright (c) 2016-2018 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import com.meterware.simplestub.Stub;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * @author Russell Gold
 */
class NiceMethodGenerator extends DefaultMethodGenerator {
    @Override
    public String createBody(CtMethod method) throws NotFoundException {
        CtClass returnType = method.getReturnType();
        if (returnType.isPrimitive())
            return null;
        else if (returnType.isArray())
            return "return new " + createEmptyArrayInstantiator(returnType) + ";";
        else if (returnType.getName().equals("java.lang.String"))
            return "return \"\";";
        else if (returnType.isInterface())
            return createStubCreationBody(returnType.getName());
        else
            return null;
    }

    private String createEmptyArrayInstantiator(CtClass returnType) throws NotFoundException {
        int numDimensions = 0;
        while (returnType.isArray()) {
            numDimensions++;
            returnType = returnType.getComponentType();
        }
        StringBuilder sb = new StringBuilder(returnType.getName());
        sb.append("[0]");
        for (int i = 1; i < numDimensions; i++) sb.append("[]");
        return sb.toString();
    }

    private String createStubCreationBody(String name) {
        return "return (" + name + ") " + Stub.class.getName() + ".createNiceStub(" + name + ".class, new java.lang.Object[0]);";
    }
}
