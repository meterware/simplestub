package com.meterware.simplestub;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Tricks for bypassing normal access restrictions on static fields
 */
class FieldUtils {

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
            Field field = getAccessibleField(aClass, fieldName);
            return field.get(null);
        } catch (NoSuchFieldException e) {
            if (aClass.getSuperclass() == null)
                throw e;
            else
                return getPrivateStaticField(aClass.getSuperclass(), fieldName);
        }
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
            Field field = getAccessibleField(aClass, fieldName);
            field.set(null, value);
        } catch (NoSuchFieldException e) {
            if (aClass.getSuperclass() == null)
                throw e;
            else
                setPrivateStaticField(aClass.getSuperclass(), fieldName, value);
        }
    }

    // Returns the specified field, ensuring that the code can access it. Note that this will not work
    // with fields representing primitives or Strings, as the compiler may optimize them.
    private static Field getAccessibleField(Class aClass, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = aClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        if (isFinal(field)) removeFinalModifier(field);
        return field;
    }

    private static boolean isFinal(Field field) {
        return (field.getModifiers() & Modifier.FINAL) == Modifier.FINAL;
    }

    private static void removeFinalModifier(Field field) throws NoSuchFieldException, IllegalAccessException {
        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    }

}
