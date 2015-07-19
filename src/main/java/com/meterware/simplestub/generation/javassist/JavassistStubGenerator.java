package com.meterware.simplestub.generation.javassist;

import com.meterware.simplestub.SimpleStubException;
import com.meterware.simplestub.generation.StubGenerator;
import javassist.*;

/**
 * The Javassist implementation of a stub generator.
 */
public class JavassistStubGenerator extends StubGenerator {

    private ClassPool pool = new ClassPool(ClassPool.getDefault());
    private Class<?> baseClass;
    private boolean strict;

    public JavassistStubGenerator(Class<?> baseClass, boolean strict) {
        this.baseClass = baseClass;
        this.strict = strict;
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
                method.getReturnType(), method.getName(), method.getParameterTypes(), method.getExceptionTypes(), null, ctClass);
    }

    private boolean generateStrictStub() {
        return strict;
    }

    private void defineMethodToThrowException(CtMethod method) throws CannotCompileException {
        method.setBody("{ throw new com.meterware.simplestub.UnexpectedMethodCallException( \"unexpected call to method " +
                        getNameFilter().toDisplayName(method.getLongName()) + "\"); }" );
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
