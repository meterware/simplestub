package com.meterware.simplestub.generation.asm;

import org.objectweb.asm.ClassWriter;

/**
 * Classes to generate appropriate methods, based on stub type.
 */
interface MethodGenerator {

    void addMethod(ClassWriter cw, java.lang.reflect.Method method);
}

