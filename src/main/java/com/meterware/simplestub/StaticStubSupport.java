package com.meterware.simplestub;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * A class which simplifies the assignment of stubs to static variables.
 */
abstract public class StaticStubSupport {

    /** Gain access to define class method. */
    private final static Unsafe unsafe = AccessController.doPrivileged(
                    new PrivilegedAction<Unsafe>() {
                        public Unsafe run() {
                            //noinspection Duplicates
                            try {
                                Field field = Unsafe.class.getDeclaredField("theUnsafe");
                                field.setAccessible(true);
                                return (Unsafe) field.get(null);
                            } catch (NoSuchFieldException exc) {
                                throw new Error("Could not access Unsafe", exc);
                            } catch (IllegalAccessException exc) {
                                throw new Error("Could not access Unsafe", exc);
                            }
                        }
                    }
            );

    /**
     * This method assigns the specified value to the named field in the specified class. It returns a
     * {@link com.meterware.simplestub.Memento} object which can be used to revert that field to
     * its original value.
     *
     * Note: this may not work with fields representing primitives or Strings, as the compiler may optimize them.
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
    @SuppressWarnings("WeakerAccess")
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
                setAccessibleField( aClass, fieldName, value );
            } catch (NoSuchFieldException e) {
                if (aClass.getSuperclass() == null)
                    throw e;
                else
                    setPrivateStaticField(aClass.getSuperclass(), fieldName, value);
            }
        }


        private void setAccessibleField(Class aClass, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
            Field field = getAccessibleField(aClass, fieldName);
            if (!isFinal(field))
                setFieldValue(value, field);
            else if (!field.getType().isPrimitive())
                unsafe.putObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), value);
            else if (field.getType().equals(boolean.class) && value instanceof Boolean)
                unsafe.putBoolean(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), (Boolean) value);
            else if (field.getType().equals(char.class) && value instanceof Character)
                unsafe.putChar(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), ((Character) value));
            else if (field.getType().equals(byte.class) && value instanceof Number)
                unsafe.putByte(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), ((Number) value).byteValue());
            else if (field.getType().equals(short.class) && value instanceof Number)
                unsafe.putShort(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), ((Number) value).shortValue());
            else if (field.getType().equals(int.class) && value instanceof Number)
                unsafe.putInt(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), ((Number) value).intValue());
            else if (field.getType().equals(long.class) && value instanceof Number)
                unsafe.putLong(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), ((Number) value).longValue());
            else if (field.getType().equals(float.class) && value instanceof Number)
                unsafe.putFloat(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), ((Number) value).floatValue());
            else if (field.getType().equals(double.class) && value instanceof Number)
                unsafe.putDouble(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), ((Number) value).doubleValue());
            else
                throw new IllegalArgumentException(String.format("Unable to field '%s' of type %s to value \"%s\" of type %s", fieldName, field.getType(), value, value.getClass().getName()));
        }


        private void setFieldValue(Object value, Field field) throws IllegalAccessException {
            field.set(null, value);
        }


        /**
         * Returns the specified field, ensuring that the code can access it. Note that this will not work
         * with fields representing primitives or Strings, as the compiler may optimize them.
         */
        private Field getAccessibleField(Class aClass, String fieldName) throws NoSuchFieldException, IllegalAccessException {
            Field field = aClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        }

        private boolean isFinal(Field field) {
            return (field.getModifiers() & Modifier.FINAL) == Modifier.FINAL;
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
