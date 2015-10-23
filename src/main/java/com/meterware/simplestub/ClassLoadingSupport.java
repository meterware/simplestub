package com.meterware.simplestub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Supports special classloading as needed by unit tests.
 */
public class ClassLoadingSupport {
    /**
     * Reloads the specified class from a new classloader, allowing its static initializations to run again.
     * Note that the new classloader will use the original class's classloader as a parent, so that the
     * resultant class will share the original's base class and interfaces.
     * @param aClass a class to reload
     * @return a new class, based on the original.
     * @throws IOException if there is a problem reading the original class definition
     * @throws ClassNotFoundException if there is a problem creating the new definition.
     */
    public static Class reloadClass(Class<?> aClass) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = new RedefiningClassLoader(aClass);
        return classLoader.loadClass(aClass.getName());
    }

    static private class RedefiningClassLoader extends ClassLoader {
        public RedefiningClassLoader(Class aClass) throws IOException {
            super(aClass.getClassLoader());
            byte[] bytes = createArrayFromStream(getClassResource(aClass));
            defineClass(aClass.getName(), bytes, 0, bytes.length);
        }

        private byte[] createArrayFromStream(InputStream inputStream) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[2056];
            int count = inputStream.read(buffer);
            while (count > 0) {
                outputStream.write(buffer, 0, count);
                count = inputStream.read(buffer);
            }
            return outputStream.toByteArray();
        }

        private InputStream getClassResource(Class aClass) {
            ClassLoader classLoader = aClass.getClassLoader();
            String name = aClass.getName().replace(".", "/") + ".class";
            return classLoader.getResourceAsStream(name);
        }
    }
}
