package com.meterware.simplestub;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * A Java 9 version of the FieldUtils class, which bypasses Java module restrictions on access to fields.
 */
class FieldUtils {

    /** Gain access to define class method. */
    private final static Unsafe unsafe = AccessController.doPrivileged(
            new PrivilegedAction<Unsafe>() {
                @Override
                public Unsafe run() {
                    //noinspection Duplicates
                    try {
                        Field field = Unsafe.class.getDeclaredField("theUnsafe");
                        field.setAccessible(true);
                        return (Unsafe) field.get(null);
                    } catch (NoSuchFieldException | IllegalAccessException exc) {
                        throw new Error("Could not access Unsafe", exc);
                    }
                }
            }
    );

    /**
     * Returns the value in the specified static field, even if it is private.
     * @param aClass the class containing the field
     * @param fieldName the name of the field.
     * @return the value of the field.
     * @throws NoSuchFieldException if the specified class does not contain a static field with the specified name
     * @throws IllegalAccessException if the attempt to make it accessible fails
     */
    static Object getPrivateStaticField(Class aClass, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        try {
            return getFieldValue(aClass, fieldName);
        } catch (NoSuchFieldException e) {
            if (aClass.getSuperclass() == null)
                throw e;
            else
                return getPrivateStaticField(aClass.getSuperclass(), fieldName);
        }
    }

    private static Object getFieldValue(Class aClass, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        unsafe.ensureClassInitialized(aClass);
        Field field = aClass.getDeclaredField(fieldName);
        if (!field.getType().isPrimitive())
            return unsafe.getObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
        else if (field.getType().equals(boolean.class))
            return unsafe.getBoolean(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
        else if (field.getType().equals(char.class))
            return unsafe.getChar(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
        else if (field.getType().equals(byte.class))
            return unsafe.getByte(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
        else if (field.getType().equals(short.class))
            return unsafe.getShort(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
        else if (field.getType().equals(int.class))
            return unsafe.getInt(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
        else if (field.getType().equals(long.class))
            return unsafe.getLong(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
        else if (field.getType().equals(float.class))
            return unsafe.getFloat(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
        else if (field.getType().equals(double.class))
            return unsafe.getDouble(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
        else
            throw new IllegalArgumentException(String.format("Can not get value for static %s field %s.%s", field.getType(), aClass.getName(), fieldName));
    }


    /**
     * Sets the value of the specified static field, even if it is private and final.
     * @param aClass the class containing the field
     * @param fieldName the name of the field.
     * @param value the new value to apply.
     * @throws NoSuchFieldException if the specified class does not contain a static field with the specified name
     * @throws IllegalAccessException if the attempt to make it accessible fails
     */
    static void setPrivateStaticField(Class aClass, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        try {
            setFieldValue( aClass, fieldName, value );
        } catch (NoSuchFieldException e) {
            if (aClass.getSuperclass() == null)
                throw e;
            else
                setPrivateStaticField(aClass.getSuperclass(), fieldName, value);
        }
    }

    private static void setFieldValue(Class aClass, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        unsafe.ensureClassInitialized(aClass);
        Field field = aClass.getDeclaredField(fieldName);
        if (!field.getType().isPrimitive())
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
            throw new IllegalArgumentException(String.format("Can not set final static %s field %s.%s to %s", field.getType(), aClass.getName(), fieldName, value.getClass().getName()));
    }

}

