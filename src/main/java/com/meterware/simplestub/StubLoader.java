package com.meterware.simplestub;
/*
 * Copyright (c) 2015-2018 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import com.meterware.simplestub.generation.StubGenerator;
import com.meterware.simplestub.generation.StubKind;
import com.meterware.simplestub.stubs.StubAnchor;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to load generated simple stubs.
 *
 * @author Russell Gold
 */
class StubLoader {

    private final static Map<Class<?>, Class<?>> PRIMITIVE_TYPES;

    static {
        PRIMITIVE_TYPES = new HashMap<>();
        PRIMITIVE_TYPES.put(Void.TYPE,      Void.class);
        PRIMITIVE_TYPES.put(Character.TYPE, Character.class);
        PRIMITIVE_TYPES.put(Byte.TYPE,      Byte.class);
        PRIMITIVE_TYPES.put(Short.TYPE,     Short.class);
        PRIMITIVE_TYPES.put(Integer.TYPE,   Integer.class);
        PRIMITIVE_TYPES.put(Long.TYPE,      Long.class);
        PRIMITIVE_TYPES.put(Boolean.TYPE,   Boolean.class);
        PRIMITIVE_TYPES.put(Float.TYPE,     Float.class);
        PRIMITIVE_TYPES.put(Double.TYPE,    Double.class);
    }

    private StubGenerator generator;
    private final Class<?> baseClass;
    private StubKind kind;
    private Type type;

    StubLoader(Class<?> baseClass, StubKind kind) {
        this.baseClass = baseClass;
        this.kind = kind;
        this.generator = StubGenerator.create(baseClass, kind);
        this.type = baseClass.getClassLoader() == null ? Type.jdkClass : Type.userClass;
    }

    enum Type {
        jdkClass {
            @Override
            Class<?> getAnchorClass(Class<?> baseClass) {
                return StubAnchor.class;
            }

        },
        userClass {
            @Override
            Class<?> getAnchorClass(Class<?> baseClass) {
                return baseClass;
            }

        };

        private String createStubClassName(Class<?> baseClass, String className) {
            return getPackagePrefix(getAnchorClass(baseClass)) + getSimpleName(className);
        }

        private String getPackagePrefix(Class<?> baseClass) {
            String name = baseClass.getName();
            return name.substring(0, name.lastIndexOf('.'));
        }

        private String getSimpleName(String className) {
            return !className.contains(".") ? '.' + className : className.substring(className.lastIndexOf('.'));
        }

        abstract Class<?> getAnchorClass(Class<?> baseClass);

    }

    /**
     * Instantiates a stub from an abstract class. The class must have been marked with the @SimpleStub annotation.
     *
     * @param parameters any parameters needed for the constructor. If the class is an inner class, the first parameter
     *                   must be the outer class instance.
     * @return a newly instantiated stub
     */
    @SuppressWarnings("unchecked")
    <T> T create(Object... parameters) {
        try {
            Class<?> stubClass = getStubClass();
            Constructor<?> constructor = getConstructor(stubClass, parameters);
            constructor.setAccessible(true);
            if (isVarArgs(constructor))
                return (T) constructor.newInstance(toVarArgList(constructor, parameters));
            else
                return (T) constructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new SimpleStubException("Unable to instantiate stub for " + baseClass.getName(), e);
        }
    }

    private Object[] toVarArgList(Constructor<?> constructor, Object[] parameters) {
        Object[] result = new Object[numConstructorArgs(constructor)];
        System.arraycopy(parameters, 0, result, 0, result.length-1);
        result[result.length - 1] = isNonVarArgsInvocation(constructor, parameters)
                                        ? lastElement(parameters)
                                        : remainingArgsAsArray(parameters, constructor);
        return result;
    }

    private Object remainingArgsAsArray(Object[] parameters, Constructor<?> constructor) {
        return slice(lastConstructorArg(constructor).getComponentType(), parameters, numConstructorArgs(constructor)-1);
    }

    private boolean isNonVarArgsInvocation(Constructor<?> constructor, Object[] parameters) {
        return parameters.length == numConstructorArgs(constructor)
                && isAssignableFrom(lastConstructorArg(constructor), lastElement(parameters));
    }

    private int numConstructorArgs(Constructor<?> constructor) {
        return constructor.getParameterTypes().length;
    }

    private Class<?> lastConstructorArg(Constructor<?> constructor) {
        return lastElement(constructor.getParameterTypes());
    }

    private <T> T lastElement(T[] array) {
        return array[array.length-1];
    }

    private Object slice(Class<?> componentType, Object[] source, int first) {
        Object result = Array.newInstance(componentType, source.length - first);
        for (int i = 0; i < source.length - first; i++)
            Array.set(result, i, source[first + i]);
        return result;
    }

    private Constructor<?> getConstructor(Class<?> stubClass, Object... parameters) throws NoSuchMethodException {
        Constructor<?> constructor = findConstructor(stubClass, parameters);
        if (constructor != null) return constructor;

        StringBuilder sb = new StringBuilder("Unable to instantiate stub for ");
        sb.append(stubClass.getSuperclass().getName()).append(" because no constructor matches ").append(Arrays.toString(parameters));
        Class<?> enclosingClass = baseClass.getEnclosingClass();
        if (!isStatic(baseClass) && enclosingClass != null && !matchesFirstParameter(enclosingClass, parameters))
            sb.append(". This appears to be a non-static inner class, but the first parameter is not the enclosing class.");
            
        throw new SimpleStubException(sb.toString());
    }

    private boolean isStatic(Class<?> aClass) {
        return Modifier.isStatic(aClass.getModifiers());
    }

    private boolean matchesFirstParameter(Class<?> enclosingClass, Object[] parameters) {
        return parameters.length > 0 && enclosingClass.isAssignableFrom(parameters[0].getClass());
    }

    private Constructor<?> findConstructor(Class<?> stubClass, Object... parameters) throws NoSuchMethodException {
        for (Constructor constructor : stubClass.getDeclaredConstructors())
            if (isCompatible(constructor, parameters)) return constructor;
        return null;
    }

    private boolean isCompatible(Constructor constructor, Object[] parameters) throws NoSuchMethodException {
        return isCompatible(constructor.getParameterTypes(), parameters, isVarArgs(constructor));
    }

    @SuppressWarnings("unchecked")
    private boolean isVarArgs(Constructor constructor) throws NoSuchMethodException {
        return constructor.getDeclaringClass().getSuperclass().getDeclaredConstructor(constructor.getParameterTypes()).isVarArgs();
    }

    private boolean isCompatible(Class[] formalArguments, Object[] actualParameters, boolean isVarArgs) {
        return areCompatibleFixedArgs(formalArguments, actualParameters)
                || (isVarArgs && areCompatibleVarArgs(formalArguments, actualParameters));
    }

    private boolean areCompatibleVarArgs(Class[] formalArguments, Object[] actualParameters) {
        return areFirstNElementsCompatible(formalArguments, actualParameters, formalArguments.length - 1) &&
               variableArgumentsAreCompatible(formalArguments, actualParameters, formalArguments.length - 1);
    }

    private boolean variableArgumentsAreCompatible(Class[] formalArguments, Object[] actualParameters, int numFixedArguments) {
        Class<?> variableArgumentType = formalArguments[numFixedArguments].getComponentType();
        for (int i = numFixedArguments; i < actualParameters.length; i++)
            if (!isAssignableFrom(variableArgumentType, actualParameters[i])) return false;
        return true;
    }

    private boolean areCompatibleFixedArgs(Class[] formalArguments, Object[] actualParameters) {
        return formalArguments.length == actualParameters.length &&
               areFirstNElementsCompatible(formalArguments, actualParameters, actualParameters.length);
    }

    private boolean areFirstNElementsCompatible(Class[] formalArguments, Object[] actualParameters, int numElements) {
        for (int i = 0; i < numElements; i++)
            if (!isAssignableFrom(formalArguments[i], actualParameters[i])) return false;
        return true;
    }

    private static boolean isAssignableFrom(Class<?> constructorParameterType, Object actualParameter) {
        if (actualParameter == null)
            return !constructorParameterType.isPrimitive();
        else
            return constructorParameterType.isAssignableFrom(actualParameter.getClass()) ||
                constructorParameterType.isPrimitive() && PRIMITIVE_TYPES.get(constructorParameterType).isAssignableFrom(actualParameter.getClass());
    }

    private Class<?> getStubClass() {
        if (!isAbstractClass())
            throw new SimpleStubException("Class " + baseClass.getName() + " is not abstract");

        return getStubClass(createStubClassName(baseClass.getName(), baseClass), type.getAnchorClass(baseClass));
    }

    private boolean isAbstractClass() {
        return Modifier.isAbstract(baseClass.getModifiers());
    }

    private Class<?> getStubClass(String stubClassName, Class<?> anchorClass) {
        try {
            return anchorClass.getClassLoader().loadClass(stubClassName);
        } catch (ClassNotFoundException e) { // class has not already been created; create it now
            return loadStubClass(stubClassName, anchorClass);
        }
    }

    private Class<?> loadStubClass(String stubClassName, Class<?> anchorClass) {
        return generator.generateStubClass(stubClassName, anchorClass);
    }

    private String createStubClassName(String className, Class<?> baseClass) {
        return type.createStubClassName(baseClass, className) + getStubClassSuffix();
    }

    private String getStubClassSuffix() {
        return kind.getStubClassSuffix();
    }


    Class<?> getStubClassForThread(String proposedClassName) {
        verifyDefaultConstructor(baseClass);
        verifyClassLoadable(type.getAnchorClass(baseClass));

        return getStubClass(createStubClassNameForThread(proposedClassName), type.getAnchorClass(baseClass));
    }

    private String createStubClassNameForThread(String proposedClassName) {
        return type.createStubClassName(baseClass, proposedClassName);
    }

    private static void verifyDefaultConstructor(Class<?> baseClass) {
        try {
            if (!baseClass.isInterface())
                baseClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new SimpleStubException("Base class %s lacks a public no-arg constructor", baseClass.getName());
        }
    }

    private static void verifyClassLoadable(Class<?> baseClass) {
        try {
            Thread.currentThread().getContextClassLoader().loadClass(baseClass.getName());
        } catch (ClassNotFoundException e) {
            throw new SimpleStubException("Stub class for %s is not loadable from the thread context classloader",
                                           baseClass.getName());
        }
    }
}
