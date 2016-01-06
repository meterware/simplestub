package com.meterware.simplestub.generation.asm;

import com.meterware.simplestub.UnexpectedMethodCallException;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

/**
 * Utilities for method generation.
 */
public class MethodGeneration {

    private static final List<? extends Class<?>> INTEGER_TYPES = createIntegerTypesList();

    private static final MethodGenerator NICE_METHOD_GENERATOR = new NiceMethodGenerator();
    private static final MethodGenerator NONNULL_METHOD_GENERATOR = new NonNullMethodGenerator();
    private static final MethodGenerator STRICT_METHOD_GENERATOR = new StrictMethodGenerator();

    @SuppressWarnings("unchecked")
    private static List<? extends Class<? extends Serializable>> createIntegerTypesList() {
        return Arrays.asList(boolean.class, byte.class, char.class, short.class, int.class);
    }

    static void addConstructor(ClassWriter cw, Constructor constructor) {
        Method m = Method.getMethod(constructor);
        GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, m, null, null, cw);
        mg.loadThis();
        mg.loadArgs();
        mg.invokeConstructor(Type.getType(constructor.getDeclaringClass()), m);
        mg.returnValue();
        mg.endMethod();
    }

    static MethodGenerator getNiceMethodGenerator() {
        return NICE_METHOD_GENERATOR;
    }

    static MethodGenerator getNonnullMethodGenerator() {
        return NONNULL_METHOD_GENERATOR;
    }

    static MethodGenerator getStrictMethodGenerator() {
        return STRICT_METHOD_GENERATOR;
    }

    interface MethodGenerator {
        void addMethod(ClassWriter cw, java.lang.reflect.Method method);
    }

    static class NiceMethodGenerator implements MethodGenerator {
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

    static class NonNullMethodGenerator extends NiceMethodGenerator {

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
            mg.visitTypeInsn(Opcodes.ANEWARRAY, componentType.getName().replace('.','/'));
        }

        private void pushGenerateStub(GeneratorAdapter mg, Class<?> returnType) {
            mg.visitLdcInsn(Type.getType(returnType));
            mg.visitInsn(Opcodes.ICONST_0);
            mg.newArray(Type.getType(Object.class));
            mg.visitMethodInsn(Opcodes.INVOKESTATIC, "com/meterware/simplestub/Stub", "createStub", "(Ljava/lang/Class;[Ljava/lang/Object;)Ljava/lang/Object;", false);
            mg.checkCast(Type.getType(returnType));
        }

        private boolean isInterface(Class<?> aClass) {
            return aClass.isInterface();
        }

    }

    static class StrictMethodGenerator implements MethodGenerator {
        @Override
        public void addMethod(ClassWriter cw, java.lang.reflect.Method method) {

            Method m = Method.getMethod(method);
            GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, m, null, null, cw);
            mg.throwException(Type.getType(UnexpectedMethodCallException.class), getUnexpectedCallMessage(method));
            mg.endMethod();
        }

        private String getUnexpectedCallMessage(java.lang.reflect.Method method) {
            StringBuilder sb = new StringBuilder("Unexpected call to method ");
            sb.append(method.getDeclaringClass().getName());
            sb.append('.').append(method.getName()).append('(');

            int count = 0;
            for (Class<?> parameterType : method.getParameterTypes()) {
                if (count++ != 0) sb.append(',');
                sb.append(parameterType.getName());
            }
            sb.append(')');
            return sb.toString();
        }
    }
}
