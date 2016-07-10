package com.meterware.simplestub;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * A class which simplifies the assignment of stubs to static variables.
 */
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
                setPrivateStaticField(containingClass, fieldName, originalValue);
            } catch (NoSuchFieldException e) {
                throw new SimpleStubException("Somehow managed to lose the field name", e);
            } catch (IllegalAccessException e) {
                throw new SimpleStubException("Somehow managed to lose access to the field", e);
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
            try {
                this.containingClass = containingClass;
                this.fieldName = fieldName;
                originalValue = getPrivateStaticField(containingClass, fieldName);
            } catch (IllegalAccessException e) {
                throw new SimpleStubException("Unable to gain access to field '" + fieldName + "'", e);
            }
        }

        private StaticMemento(Class<?> containingClass, String fieldName, Object stubValue) throws NoSuchFieldException {
            this(containingClass, fieldName);
            try {
                setPrivateStaticField(containingClass, fieldName, stubValue);
            } catch (IllegalAccessException e) {
                throw new SimpleStubException("Unable to gain access to field '" + fieldName + "'", e);
            }
        }

        private void setPrivateStaticField(Class aClass, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
            try {
                Field field = getAccessibleField(aClass, fieldName);
                field.set(null, value);
            } catch (NoSuchFieldException e) {
                if (aClass.getSuperclass() == null)
                    throw e;
                else
                    setPrivateStaticField(aClass.getSuperclass(), fieldName, value);
            }
        }

        /**
         * Returns the specified field, ensuring that the code can access it. Note that this will not work
         * with fields representing primitives or Strings, as the compiler may optimize them.
         */
        private Field getAccessibleField(Class aClass, String fieldName) throws NoSuchFieldException, IllegalAccessException {
            Field field = aClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            return field;
        }

        private Object getPrivateStaticField(Class aClass, String fieldName) throws NoSuchFieldException, IllegalAccessException {
            try {
                Field field = getAccessibleField(aClass, fieldName);
                return field.get(null);
            } catch (NoSuchFieldException e) {
                if (aClass.getSuperclass() == null)
                    throw e;
                else
                    return getPrivateStaticField(aClass.getSuperclass(), fieldName);
            }
        }
    }

}
