package com.meterware.simplestub;

import java.lang.reflect.Field;

/**
 * A class which simplifies the assignment of stubs to static variables.
 */
abstract public class StaticStubSupport {

    /**
     * This method assigns the specified value to the named field in the specified class. It returns a
     * {@link com.meterware.simplestub.StaticStubSupport.Momento} object which can be used to revert that field to
     * its original value.
     *
     * @param containingClass the class on which the static field is defined.
     * @param fieldName       the name of the static field.
     * @param newValue        the value to place into the static field.
     * @return an object which holds the information needed to revert the static field.
     * @throws NoSuchFieldException if the named field does not exist.
     */
    public static Momento install(Class<?> containingClass, String fieldName, Object newValue) throws NoSuchFieldException {
        return new Momento(containingClass, fieldName, newValue);
    }

    /**
     * This method returns a {@link com.meterware.simplestub.StaticStubSupport.Momento} object
     * which can be used to revert the specified field to its current value.
     *
     * @param containingClass the class on which the static field is defined.
     * @param fieldName       the name of the static field.
     * @return an object which holds the information needed to revert the static field.
     * @throws NoSuchFieldException if the named field does not exist.
     */
    public static Momento preserve(Class<?> containingClass, String fieldName) throws NoSuchFieldException {
        return new Momento(containingClass, fieldName);
    }

    private StaticStubSupport() {
    }


    /**
     * An object which contains all the information needed to revert the static field to its previous value.
     */
    public static class Momento {

        /**
         * A null object instance. Initializing a Momento to this value ensures that it does nothing.
         */
        public static final Momento NULL = new NullMomento();

        private Class<?> containingClass;
        private String fieldName;
        private Object preservedValue;

        /**
         * Reverts the field.
         */
        public void revert() {
            try {
                setPrivateStaticField(containingClass, fieldName, preservedValue);
            } catch (NoSuchFieldException e) {
                throw new SimpleStubException("Somehow managed to lose the field name", e);
            } catch (IllegalAccessException e) {
                throw new SimpleStubException("Somehow managed to lose access to the field", e);
            }
        }

        private Momento() {
        }

        private Momento(Class<?> containingClass, String fieldName) throws NoSuchFieldException {
            try {
                this.containingClass = containingClass;
                this.fieldName = fieldName;
                preservedValue = getPrivateStaticField(containingClass, fieldName);
            } catch (IllegalAccessException e) {
                throw new SimpleStubException("Unable to gain access to field '" + fieldName + "'", e);
            }
        }

        private Momento(Class<?> containingClass, String fieldName, Object stubValue) throws NoSuchFieldException {
            this(containingClass, fieldName);
            try {
                setPrivateStaticField(containingClass, fieldName, stubValue);
            } catch (IllegalAccessException e) {
                throw new SimpleStubException("Unable to gain access to field '" + fieldName + "'", e);
            }
        }

        private void setPrivateStaticField(Class aClass, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
            Field field = aClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, value);
        }

        private Object getPrivateStaticField(Class aClass, String fieldName) throws NoSuchFieldException, IllegalAccessException {
            Field field = aClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(null);
        }
    }

    private static class NullMomento extends Momento {
        @Override
        public void revert() {
        }
    }

}
