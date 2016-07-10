package com.meterware.simplestub.generation.javassist;

import com.meterware.simplestub.Stub;
import com.meterware.simplestub.generation.StubKind;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.util.HashMap;
import java.util.Map;

abstract class MethodGenerator {

    private static final Map<StubKind,MethodGenerator> methodGenerators = new HashMap<StubKind, MethodGenerator>();

    static {
        methodGenerators.put(StubKind.DEFAULT, new DefaultMethodGenerator());
        methodGenerators.put(StubKind.NICE, new NiceMethodGenerator());
        methodGenerators.put(StubKind.STRICT, new StrictMethodGenerator());
    }

    static MethodGenerator getMethodGenerator(StubKind kind) {
        return methodGenerators.get(kind);
    }

    abstract String createBody(CtMethod method) throws NotFoundException;

    private static class StrictMethodGenerator extends MethodGenerator {
        @Override
        String createBody(CtMethod method) throws NotFoundException {
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

    static class DefaultMethodGenerator extends MethodGenerator {
        @Override
        String createBody(CtMethod method) throws NotFoundException {
            return null;
        }
    }

    private static class NiceMethodGenerator extends DefaultMethodGenerator {
        @Override
        String createBody(CtMethod method) throws NotFoundException {
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
            return "return (" + name + ") " + Stub.class.getName() + ".createStub(" + name + ".class, new java.lang.Object[0]);";
        }
    }
}
