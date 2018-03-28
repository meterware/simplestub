package com.meterware.simplestub.generation.javassist;
/*
 * Copyright (c) 2016 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * @author Russell Gold
 */
class StrictMethodGenerator implements MethodGenerator {
    @Override
    public String createBody(CtMethod method) throws NotFoundException {
        return "{ throw new com.meterware.simplestub.UnexpectedMethodCallException( \"" +
                        getUnexpectedCallMessage(method) + "\"); }";
    }

    private String getUnexpectedCallMessage(CtMethod method) throws NotFoundException {
        StringBuilder sb = new StringBuilder("Unexpected call to method ");
        sb.append(method.getDeclaringClass().getName());
        sb.append('.').append(method.getName()).append('(');

        int count = 0;
        for (CtClass parameterType : method.getParameterTypes()) {
            if (count++ != 0) sb.append(',');
            sb.append(parameterType.getName());
        }
        sb.append(')');
        return sb.toString();
    }
}
