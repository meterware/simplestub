package com.meterware.simplestub.generation.asm;
/*
 * Copyright (c) 2016-2018 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

/**
 * @author Russell Gold
 */
class NiceMethodGenerator extends DefaultMethodGenerator {

    @Override
    protected void pushObjectReturnType(GeneratorAdapter mg, Class<?> returnType) {
        if (returnType.isArray())
            pushGenerateEmptyArray(mg, returnType.getComponentType());
        else if (returnType.equals(String.class))
            mg.visitLdcInsn("");
        else if (isInterface(returnType))
            pushGenerateStub(mg, returnType);
        else
            super.pushObjectReturnType(mg, returnType);
    }

    private void pushGenerateEmptyArray(GeneratorAdapter mg, Class<?> componentType) {
        mg.visitInsn(Opcodes.ICONST_0);
        if (componentType.isPrimitive())
            mg.visitIntInsn(Opcodes.NEWARRAY, getNewArrayType(componentType));
        else
            mg.visitTypeInsn(Opcodes.ANEWARRAY, componentType.getName().replace('.', '/'));
    }

    private int getNewArrayType(Class<?> componentType) {
        if (componentType.equals(boolean.class))
            return Opcodes.T_BOOLEAN;
        else if (componentType.equals(char.class))
            return Opcodes.T_CHAR;
        else if (componentType.equals(float.class))
            return Opcodes.T_FLOAT;
        else if (componentType.equals(double.class))
            return Opcodes.T_DOUBLE;
        else if (componentType.equals(byte.class))
            return Opcodes.T_BYTE;
        else if (componentType.equals(short.class))
            return Opcodes.T_SHORT;
        else if (componentType.equals(int.class))
            return Opcodes.T_INT;
        else if (componentType.equals(long.class))
            return Opcodes.T_LONG;
        throw new IllegalArgumentException("Unknown array component type " + componentType);
    }

    private void pushGenerateStub(GeneratorAdapter mg, Class<?> returnType) {
        mg.visitLdcInsn(Type.getType(returnType));
        mg.visitInsn(Opcodes.ICONST_0);
        mg.newArray(Type.getType(Object.class));
        mg.visitMethodInsn(Opcodes.INVOKESTATIC, "com/meterware/simplestub/Stub", "createNiceStub", "(Ljava/lang/Class;[Ljava/lang/Object;)Ljava/lang/Object;", false);
        mg.checkCast(Type.getType(returnType));
    }

    private boolean isInterface(Class<?> aClass) {
        return aClass.isInterface();
    }

}
