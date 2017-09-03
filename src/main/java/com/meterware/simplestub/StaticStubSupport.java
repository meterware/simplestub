package com.meterware.simplestub;

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
                FieldUtils.setPrivateStaticField(containingClass, fieldName, originalValue);
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
                originalValue = FieldUtils.getPrivateStaticField(containingClass, fieldName);
            } catch (IllegalAccessException e) {
                throw new SimpleStubException("Unable to gain access to field '" + fieldName + "'", e);
            }
        }

        private StaticMemento(Class<?> containingClass, String fieldName, Object stubValue) throws NoSuchFieldException {
            this(containingClass, fieldName);
            try {
                FieldUtils.setPrivateStaticField(containingClass, fieldName, stubValue);
            } catch (IllegalAccessException e) {
                throw new SimpleStubException("Unable to gain access to field '" + fieldName + "'", e);
            }
        }

    }

}
