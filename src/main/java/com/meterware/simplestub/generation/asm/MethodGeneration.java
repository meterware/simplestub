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

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

/**
 * Utilities for method generation.
 */
public class MethodGeneration {

    private static final List<? extends Class<?>> INTEGER_TYPES = createIntegerTypesList();

    private static final MethodGenerator NICE_METHOD_GENERATOR = new NiceMethodGenerator();
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
            mg.visitInsn(getDefaultReturnValueConstant(method.getReturnType()));
            mg.returnValue();
            mg.endMethod();
        }

        private int getDefaultReturnValueConstant(Class<?> returnType) {
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
