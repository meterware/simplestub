package com.meterware.simplestub.generation.asm;

import com.meterware.simplestub.generation.ClassNameList;
import com.meterware.simplestub.generation.ClassReferenceFinder;
import org.objectweb.asm.*;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Given a class, returns all of the classes it references.
 */
public class AsmClassReferenceFinder implements ClassReferenceFinder {

    @Override
    public Collection<Class> getClassesReferencedBy(Class aClass) throws IOException {
        ClassReferences references = new ClassReferences();
        ClassReader reader = new ClassReader(aClass.getName());
        reader.accept(new ReferenceClassVisitor(references), 0);
        return references.getClasses();
    }

    static class ClassReferences {
        private Set<Class> classes = new HashSet<>();

        void addNamedClass(String internalName) {
            Class aClass = getApplicationClass(internalName);
            if (aClass != null) this.getClasses().add(aClass);
        }

        private Class getApplicationClass(String internalName) {
            try {
                Class aClass = Class.forName(internalName.replace('/', '.'));
                return isJdkClass(aClass) ? null : aClass;
            } catch (ClassNotFoundException ignore) {
                return null;
            }
        }

        private boolean isJdkClass(Class aClass) {
            return aClass.getClassLoader() == null;
        }

        Set<Class> getClasses() {
            return classes;
        }
    }

    static class ReferenceClassVisitor extends ClassVisitor {
        private final ClassReferences references;

        ReferenceClassVisitor(ClassReferences references) {
            super(Opcodes.ASM4);
            this.references = references;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaceNames) {
            references.addNamedClass(superName);
            for (String interfaceName : interfaceNames) references.addNamedClass(interfaceName);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            return new ReferenceMethodVisitor(references);
        }
    }

    static class ReferenceMethodVisitor extends MethodVisitor {
        private final ClassReferences references;

        ReferenceMethodVisitor(ClassReferences references) {
            super(Opcodes.ASM4);
            this.references = references;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            addClassReferences(desc);
        }

        private void addClassReferences(String desc) {
            for (String className : getClassNames(desc))
                references.addNamedClass(className);
        }

        private Iterable<String> getClassNames(String spec) {
            return new ClassNameList(spec);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            addClassReferences(desc);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            if (type.startsWith("["))
                addClassReferences(type);
            else
                references.addNamedClass(type);
        }

        @Override
        public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
            addClassReferences(desc);
            return null;
        }

        @Override
        public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
            if (type != null)
                references.addNamedClass(type);
        }

    }
}
