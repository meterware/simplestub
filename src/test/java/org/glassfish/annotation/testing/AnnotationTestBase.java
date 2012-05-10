package org.glassfish.annotation.testing;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AnnotationTestBase {
    private Map<Class<? extends Annotation>, Set<Element>> annotations = new HashMap<Class<? extends Annotation>, Set<Element>>();
    private FakeElements elements = new FakeElements();

    protected TypeElement createAnnotationElement(Class<? extends Annotation> annotationClass) {
        return elements.createAnnotationElement(annotationClass);
    }

    protected TypeElement createClassElement(Class<?> aClass) {
        return elements.createClassElement(aClass);
    }

    protected TypeElement createNoPackageClassElement(Class<?> aClass) {
        return elements.createNoPackageClassElement(aClass);
    }

    protected ExecutableElement createConstructorElement(Constructor constructor) {
        return new ConstructorElement(constructor, new FakeElement(constructor.getDeclaringClass().getSimpleName(), null));
    }

    protected ExecutableElement createMethodElement(Method method) {
        return new MethodElement(method, new FakeElement(method.getDeclaringClass().getSimpleName(), null));
    }

    protected Set<Element> getAnnotatedElements(Class<? extends Annotation> annotationClass) {
        if (annotations.containsKey(annotationClass))
            return annotations.get(annotationClass);
        else
            return createAnnotatedElementsSet(annotationClass);
    }

    private Set<Element> createAnnotatedElementsSet(Class<? extends Annotation> annotationClass) {
        Set<Element> set = new HashSet<Element>();
        annotations.put(annotationClass, set);
        return set;
    }

    protected Elements getElements() {
        return elements;
    }
}
