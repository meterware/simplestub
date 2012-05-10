package org.glassfish.simplestub;

import org.glassfish.annotation.testing.AnnotationTestBase;
import org.glassfish.annotation.testing.FakeElement;
import org.glassfish.annotation.testing.AnnotationTestBase;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

public class SimpleStubTestBase extends AnnotationTestBase {

    protected TypeElement createAnnotatedClass(Class<?> aClass) {
        return createAnnotatedClass(aClass, false);
    }

    protected TypeElement createAnnotatedClass(Class<?> aClass, boolean strict) {
        TypeElement classElement = createClassElement(aClass);
        annotateElement(classElement, new TestSimpleStub(strict), SimpleStub.class);
        return classElement;
    }

    protected TypeElement createAnnotatedNoPackageClass(Class<?> aClass, boolean strict) {
        TypeElement classElement = createNoPackageClassElement(aClass);
        annotateElement(classElement, new TestSimpleStub(strict), SimpleStub.class);
        return classElement;
    }

    private void annotateElement(Element classElement, Annotation annotation, Class<? extends Annotation> annotationClass) {
        ((FakeElement) classElement).addAnnotation(annotation);
        getAnnotatedElements(annotationClass).add(classElement);
    }

    static class TestSimpleStub implements SimpleStub {
        private boolean strict;

        public TestSimpleStub(boolean strict) {
            this.strict = strict;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return SimpleStub.class;
        }

        @Override
        public boolean strict() {
            return strict;
        }
    }
}
