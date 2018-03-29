package com.meterware.simplestub.generation.javassist;
/*
 * Copyright (c) 2017-2018 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import com.meterware.simplestub.SimpleStubException;
import com.meterware.simplestub.generation.StubGenerator;
import com.meterware.simplestub.generation.StubKind;
import javassist.*;

import java.util.HashMap;
import java.util.Map;

/**
 * The Javassist implementation of a stub generator.
 *
 * @author Russell Gold
 */
public class JavassistStubGenerator extends StubGenerator {

    private static final Map<StubKind,MethodGenerator> methodGenerators = new HashMap<>();

    static {
        JavassistStubGenerator.methodGenerators.put(StubKind.DEFAULT, new DefaultMethodGenerator());
        JavassistStubGenerator.methodGenerators.put(StubKind.NICE, new NiceMethodGenerator());
        JavassistStubGenerator.methodGenerators.put(StubKind.STRICT, new StrictMethodGenerator());
    }

    private ClassPool pool = new ClassPool(ClassPool.getDefault());
    private Class<?> baseClass;
    private MethodGenerator methodGenerator;

    public JavassistStubGenerator(Class<?> baseClass, StubKind kind) {
        this.baseClass = baseClass;
        methodGenerator = methodGenerators.get(kind);
    }

    @Override
    public Class<?> generateStubClass(String stubClassName, Class<?> anchorClass) {
        try {
            return createStubClass(stubClassName, anchorClass);
        } catch (NotFoundException | CannotCompileException e) {
            throw new SimpleStubException("Unable to create stub class", e);
        }
    }

    private Class<?> createStubClass(String stubClassName, Class<?> anchorClass) throws NotFoundException, CannotCompileException {
        CtClass ctClass = createStubClassBase(stubClassName);
        for (CtMethod method : ctClass.getMethods()) {
            if (isAbstract(method))
                addStubMethod(ctClass, method);
        }
        return ctClass.toClass(anchorClass.getClassLoader(), null);
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

    private void addStubMethod(CtClass declaringClass, CtMethod abstractMethod) throws NotFoundException, CannotCompileException {
        declaringClass.addMethod(createCtMethod(declaringClass, abstractMethod));
    }

    private CtMethod createCtMethod(CtClass declaringClass, CtMethod method) throws CannotCompileException, NotFoundException {
        return CtNewMethod.make(method.getModifiers() & ~Modifier.ABSTRACT, method.getReturnType(),
                                 method.getName(), method.getParameterTypes(), method.getExceptionTypes(), methodGenerator.createBody(method), declaringClass);
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
