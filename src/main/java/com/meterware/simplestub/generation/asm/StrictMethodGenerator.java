package com.meterware.simplestub.generation.asm;

import com.meterware.simplestub.UnexpectedMethodCallException;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

class StrictMethodGenerator implements MethodGenerator {
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
