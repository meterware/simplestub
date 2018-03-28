package com.meterware.simplestub.generation.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

class DefaultMethodGenerator implements MethodGenerator {
    private static final List<? extends Class<?>> INTEGER_TYPES = createIntegerTypesList();

    @SuppressWarnings("unchecked")
    private static List<? extends Class<? extends Serializable>> createIntegerTypesList() {
        return Arrays.asList(boolean.class, byte.class, char.class, short.class, int.class);
    }

    @Override
    public void addMethod(ClassWriter cw, java.lang.reflect.Method method) {
        Method m = Method.getMethod(method);
        GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, m, null, null, cw);
        pushReturnValue(mg, method.getReturnType());
        mg.returnValue();
        mg.endMethod();
    }

    private void pushReturnValue(GeneratorAdapter mg, Class<?> returnType) {
        if (returnType.isPrimitive())
            mg.visitInsn(getPrimitiveReturnValueConstant(returnType));
        else
            pushObjectReturnType(mg, returnType);
    }

    private int getPrimitiveReturnValueConstant(Class<?> returnType) {
        if (returnType.equals(long.class))
            return Opcodes.LCONST_0;
        else if (returnType.equals(float.class))
            return Opcodes.FCONST_0;
        else if (returnType.equals(double.class))
            return Opcodes.DCONST_0;
        else if (INTEGER_TYPES.contains(returnType))
            return Opcodes.ICONST_0;
        else
            return Opcodes.ACONST_NULL;
    }

    protected void pushObjectReturnType(GeneratorAdapter mg, Class<?> returnType) {
        mg.visitInsn(Opcodes.ACONST_NULL);
    }

}
