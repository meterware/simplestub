package com.meterware.simplestub.generation.asm;
/*
 * Copyright (c) 2016 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import com.meterware.simplestub.classes.AbstractImplementation;
import com.meterware.simplestub.classes.ClassWithPrivateNestedClass;
import com.meterware.simplestub.classes.ConcreteClass;
import com.meterware.simplestub.classes.Interface1;
import com.meterware.simplestub.generation.ClassReferenceFinder;
import org.junit.Test;
import org.objectweb.asm.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.objectweb.asm.Opcodes.ASM4;

/**
 * @author Russell Gold
 */
public class AsmClassReferenceFinderTest {

    ClassReferenceFinder finder = new AsmClassReferenceFinder();

    @Test
    public void whenClassReferencesNoClass_returnEmptyCollection() throws Exception {
        assertThat(finder.getClassesReferencedBy(ConcreteClass.class), empty());
    }

    @Test
    public void implementation_referencesItsInterface() throws Exception {
        assertThat(finder.getClassesReferencedBy(AbstractImplementation.class), contains((Class) Interface1.class));
    }

    @Test
    public void whenClassHasInnerClass_returnManufacturedAccessClass() throws Exception {
        assertThat(finder.getClassesReferencedBy(ClassWithPrivateNestedClass.class), hasItem((Class) Class.forName(ClassWithPrivateNestedClass.class.getName() + "$1")));
    }

    @Test
    public void whenClassInstantiatesNewClass_returnInstantiatedClass() throws Exception {
        assertThat(finder.getClassesReferencedBy(ClassWithPrivateNestedClass.class), hasItem((Class) Class.forName(ClassWithPrivateNestedClass.class.getName() + "$ListenerImpl")));
    }


    //---------

    public static void main(String[] args) throws Exception {
        ClassReader cr = new ClassReader(ClassWithPrivateNestedClass.class.getName());
        cr.accept(new MyClassVisitor(), 0);
    }

    static class MyClassVisitor extends ClassVisitor {
        public MyClassVisitor() {
            super(ASM4);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            return new MyMethodVisitor();
        }
    }


    static class MyMethodVisitor extends MethodVisitor {
        public MyMethodVisitor() {
            super(ASM4);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            System.out.println("typeInsn: " + type);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            System.out.println("fieldInsn:" + desc);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            System.out.println("methodInsn:" + desc);
        }

        @Override
        public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
            System.out.println("tryCatchAnnotation:" + desc);
            return null;
        }

        @Override
        public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
            System.out.println("tryCatchBlock:" + type);
        }
    }
}
