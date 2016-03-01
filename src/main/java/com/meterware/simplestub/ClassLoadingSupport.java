package com.meterware.simplestub;

import com.meterware.simplestub.generation.StubGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Supports special classloading as needed by unit tests.
 */
public class ClassLoadingSupport {
    /**
     * EXPERIMENTAL!
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
        private Set<String> definedClassNames = new HashSet<String>();

        public RedefiningClassLoader(Class aClass) throws IOException {
            super(aClass.getClassLoader());
            defineClass(aClass);
        }

        private void defineClass(Class aClass) throws IOException {
            if (definedClassNames.contains(aClass.getName())) return;
            definedClassNames.add(aClass.getName());

            for (Class declaredClass : aClass.getDeclaredClasses())
                defineClass(declaredClass);
            for (Class referencedClass : getNonPublicReferencedClasses(aClass))
                defineClass(referencedClass);

            byte[] bytes = createArrayFromStream(getClassResource(aClass));
            if (!aClass.isEnum()) defineClass(aClass.getName(), bytes, 0, bytes.length);  // defining enum values defines the class itself
        }

        private Class[] getNonPublicReferencedClasses(Class aClass) throws IOException {
            Set<Class> result = new HashSet<Class>();
            for (Class reference : getClassesReferencedBy(aClass))
                if (isNewNonPublicClass(reference))
                    result.add(reference);

            return result.toArray(new Class[result.size()]);
        }

        private Collection<Class> getClassesReferencedBy(Class aClass) throws IOException {
            return StubGenerator.getStubGeneratorFactory().getClassReferenceFinder().getClassesReferencedBy(aClass);
        }

        private boolean isNewNonPublicClass(Class reference) {
            return !definedClassNames.contains(reference.getName()) && !Modifier.isPublic(reference.getModifiers());
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
