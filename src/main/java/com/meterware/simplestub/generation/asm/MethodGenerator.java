package com.meterware.simplestub.generation.asm;

import com.meterware.simplestub.UnexpectedMethodCallException;
import com.meterware.simplestub.generation.StubKind;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classes to generate appropriate methods, based on stub type.
 */
abstract class MethodGenerator {

    static final Map<StubKind,MethodGenerator> methodGenerators = new HashMap<StubKind, MethodGenerator>();

    static {
        methodGenerators.put(StubKind.NICE, new NiceMethodGenerator());
        methodGenerators.put(StubKind.NON_NULL, new NonNullMethodGenerator());
        methodGenerators.put(StubKind.STRICT, new StrictMethodGenerator());
    }

    static MethodGenerator getMethodGenerator(StubKind kind) {
        return methodGenerators.get(kind);
    }

    abstract void addMethod(ClassWriter cw, java.lang.reflect.Method method);

    static class NiceMethodGenerator extends MethodGenerator {
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

    static class StrictMethodGenerator extends MethodGenerator {
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
