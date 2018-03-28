package com.meterware.simplestub;
/*
 * Copyright (c) 2014-2017 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

/**
 * A class which simplifies the assignment of stubs to static variables.
 *
 * @author Russell Gold
 */
@SuppressWarnings("WeakerAccess")
abstract public class StaticStubSupport {

    /**
     * This method assigns the specified value to the named field in the specified class. It returns a
     * {@link com.meterware.simplestub.Memento} object which can be used to revert that field to
     * its original value.
     *
     * @param containingClass the class on which the static field is defined.
     * @param fieldName       the name of the static field.
     * @param newValue        the value to place into the static field.
     * @return an object which holds the information needed to revert the static field.
     * @throws NoSuchFieldException if the named field does not exist.
     */
    public static Memento install(Class<?> containingClass, String fieldName, Object newValue) throws NoSuchFieldException {
        return new StaticMemento(containingClass, fieldName, newValue);
    }

    /**
     * This method returns a {@link com.meterware.simplestub.Memento} object
     * which can be used to revert the specified field to its current value.
     *
     * @param containingClass the class on which the static field is defined.
     * @param fieldName       the name of the static field.
     * @return an object which holds the information needed to revert the static field.
     * @throws NoSuchFieldException if the named field does not exist.
     */
    public static Memento preserve(Class<?> containingClass, String fieldName) throws NoSuchFieldException {
        return new StaticMemento(containingClass, fieldName);
    }

    private StaticStubSupport() {
    }

    /**
     * Returns the nested class specified by an outer class and a sequences of class names. If more than one class name
     * is specified, they will be appended to one another.
     *
     * For example, the nested class Foo$Bar$Baz can be retrieved by nestedClass(Foo.class, "Bar", "Baz").
     * @param aClass the outer class containing the desired nested class
     * @param nestedClassNames one or more nested class names
     * @return the desired inner class
     * @throws ClassNotFoundException if no such nested class exists.
     */
    public static Class<?> nestedClass(Class<?> aClass, String... nestedClassNames) throws ClassNotFoundException {
        Class<?> result = aClass;
        for (String nestedClassName : nestedClassNames)
            result = Class.forName(result.getName() + "$" + nestedClassName);
        return result;
    }


    private static class StaticMemento implements Memento {

        private Class<?> containingClass;
        private String fieldName;
        private Object originalValue;

        /**
         * Reverts the field.
         */
        @Override
        public void revert() {
            try {
                FieldUtils.setPrivateStaticField(containingClass, fieldName, originalValue);
            } catch (NoSuchFieldException e) {
                throw new SimpleStubException("Somehow managed to lose the field name", e);
            }
        }

        /**
         * Returns the original value of the field.
         */
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getOriginalValue() {
            return (T) originalValue;
        }

        private StaticMemento(Class<?> containingClass, String fieldName) throws NoSuchFieldException {
            this.containingClass = containingClass;
            this.fieldName = fieldName;
            originalValue = FieldUtils.getPrivateStaticField(containingClass, fieldName);
        }

        private StaticMemento(Class<?> containingClass, String fieldName, Object stubValue) throws NoSuchFieldException {
            this(containingClass, fieldName);
            FieldUtils.setPrivateStaticField(containingClass, fieldName, stubValue);
        }

    }

}
