package com.meterware.simplestub.generation.asm;
/*
 * Copyright (c) 2015 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import org.objectweb.asm.ClassWriter;

/**
 * Classes to generate appropriate methods, based on stub type.
 *
 * @author Russell Gold
 */
interface MethodGenerator {

    void addMethod(ClassWriter cw, java.lang.reflect.Method method);
}

