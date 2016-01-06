package com.meterware.simplestub.generation.javassist;

import com.meterware.simplestub.SimpleStubException;
import com.meterware.simplestub.Stub;
import com.meterware.simplestub.generation.StubGenerator;
import javassist.*;

/**
 * The Javassist implementation of a stub generator.
 */
public class JavassistStubGenerator extends StubGenerator {

    private ClassPool pool = new ClassPool(ClassPool.getDefault());
    private Class<?> baseClass;
    private boolean strict;
    private boolean returnNulls;

    public JavassistStubGenerator(Class<?> baseClass, boolean strict, boolean returnNulls) {
        this.baseClass = baseClass;
        this.strict = strict;
        this.returnNulls = returnNulls;
    }

    @Override
    public Class<?> loadStubClass(String stubClassName, ClassLoader classLoader) {
        try {
            return createStubClass(stubClassName, classLoader);
        } catch (NotFoundException e) {
            throw new SimpleStubException("Unable to create stub class", e);
        } catch (CannotCompileException e) {
            throw new SimpleStubException("Unable to create stub class", e);
        }
    }

    private Class<?> createStubClass(String stubClassName, ClassLoader classLoader) throws NotFoundException, CannotCompileException {
        CtClass ctClass = createStubClassBase(stubClassName);
        for (CtMethod method : ctClass.getMethods()) {
            if (isAbstract(method))
                addStubMethod(ctClass, method);
        }
        return ctClass.toClass(classLoader, null);
    }

    private CtClass createStubClassBase(String stubClassName) throws NotFoundException {
        if (baseClass.isInterface()) {
            return createStubClassFromInterface(stubClassName);
        } else {
            return createStubClassFromAbstractClass(stubClassName);
        }
    }

    private boolean isAbstract(CtMethod method) {
        return Modifier.isAbstract(method.getModifiers());
    }

    private void addStubMethod(CtClass ctClass, CtMethod method) throws NotFoundException, CannotCompileException {
        CtMethod method1 = createMethod(ctClass, method);
        if (generateStrictStub())
            defineMethodToThrowException(method1);
        ctClass.addMethod(method1);
    }

    private CtMethod createMethod(CtClass ctClass, CtMethod method) throws NotFoundException, CannotCompileException {
        return CtNewMethod.make(method.getModifiers() & ~Modifier.ABSTRACT,
                method.getReturnType(), method.getName(), method.getParameterTypes(), method.getExceptionTypes(), createBody(method.getReturnType()), ctClass);
    }

    private String createBody(CtClass returnType) throws NotFoundException {
        if (returnNulls || returnType.isPrimitive())
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


    private boolean generateStrictStub() {
        return strict;
    }

    private void defineMethodToThrowException(CtMethod method) throws CannotCompileException, NotFoundException {

        method.setBody("{ throw new com.meterware.simplestub.UnexpectedMethodCallException( \"" +
                        getUnexpectedCallMessage(method) + "\"); }" );
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

    private CtClass createStubClassFromInterface(String stubClassName) throws NotFoundException {
        CtClass ctClass = pool.makeClass(stubClassName);
        ctClass.addInterface(pool.getCtClass(baseClass.getName()));
        return ctClass;
    }

    private CtClass createStubClassFromAbstractClass(String stubClassName) throws NotFoundException {
        return pool.makeClass(stubClassName, pool.get(baseClass.getName()));
    }
}
