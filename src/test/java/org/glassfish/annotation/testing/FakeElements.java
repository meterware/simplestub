package org.glassfish.annotation.testing;

import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeElements implements Elements {

    private Map<String,PackageElement> packages = new HashMap<String, PackageElement>();
    private Map<FakeTypeElement, ArrayList<FakeExecutableElement>> allMembers = new HashMap<FakeTypeElement, ArrayList<FakeExecutableElement>>();

    public FakeTypeElement createAnnotationElement(Class<? extends Annotation> annotationClass) {
        return new FakeTypeElement(lastComponent(annotationClass.getName()), createPackage(prefix(annotationClass.getName())));
    }

    public FakeTypeElement createClassElement( Class<?> aClass ) {
        if (aClass.getEnclosingClass() == null)
            return createClassElement(aClass, createPackage(prefix(aClass.getName())));
        else
            return createClassElement(aClass, nestedName(aClass), createClassElement(aClass.getEnclosingClass()));
    }

    private String nestedName(Class<?> aClass) {
        return aClass.getSimpleName();
    }

    public FakeTypeElement createNoPackageClassElement( Class<?> aClass ) {
        return createClassElement(aClass, createPackage(""));
    }

    private FakeTypeElement createClassElement(Class<?> aClass, Element enclosure) {
        return createClassElement(aClass, lastComponent(aClass.getName()), enclosure);
    }

    private FakeTypeElement createClassElement(Class<?> aClass, String className, Element enclosure) {
        FakeTypeElement classElement = new FakeTypeElement(className, enclosure);

        ArrayList<FakeExecutableElement> allMembers = new ArrayList<FakeExecutableElement>();
        for (Constructor constructor : aClass.getDeclaredConstructors()) {
            allMembers.add(new ConstructorElement(constructor, classElement));
        }
        addAllMethods(aClass, classElement, allMembers);
        this.allMembers.put(classElement,allMembers);
        return classElement;
    }

    private void addAllMethods(Class<?> aClass, FakeTypeElement classElement, ArrayList<FakeExecutableElement> allMembers) {
        addMethods(allMembers, aClass, classElement);
        for (Class<?> anInterface : aClass.getInterfaces())
            addMethods(allMembers, anInterface, null);
        if (aClass.getSuperclass() != null)
            addMethods(allMembers, aClass.getSuperclass(), null);
    }

    private void addMethods(List<FakeExecutableElement> allMembers, Class<?> aClass, FakeTypeElement classElement) {
        for (Method method : aClass.getDeclaredMethods()) {
            if (!concreteMethodDefined(allMembers, method))
                allMembers.add(new MethodElement(method, classElement));
        }
    }

    private boolean concreteMethodDefined(List<FakeExecutableElement> allMembers, Method method) {
        for (FakeExecutableElement member : allMembers) {
            if (member.overrides(method)) return true;
        }
        return false;
    }

    private PackageElement createPackage(String name) {
        if (packages.containsKey(name)) return packages.get(name);

        PackageElement newPackage = hasNoParent(name)
                                        ? new FakePackageElement(name)
                                        : new FakePackageElement(lastComponent(name), createPackage(prefix(name)));
        packages.put(name, newPackage);
        return newPackage;
    }

    private boolean hasNoParent(String name) {
        return !name.contains(".");
    }

    private String lastComponent(String name) {
        return name.substring(name.lastIndexOf('.')+1);
    }

    private String prefix(String name) {
        return name.substring(0, name.lastIndexOf('.'));
    }

    public PackageElement getPackageElement(CharSequence packageName) {
        return packages.get(packageName.toString());
    }

    public TypeElement getTypeElement(CharSequence charSequence) {
        return null;
    }

    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(AnnotationMirror annotationMirror) {
        return null;
    }

    public String getDocComment(Element element) {
        return null;
    }

    public boolean isDeprecated(Element element) {
        return false;
    }

    public Name getBinaryName(TypeElement typeElement) {
        return null;
    }

    public PackageElement getPackageOf(Element element) {
        Element anElement = element;
        while (anElement != null && !(anElement instanceof PackageElement)) anElement = anElement.getEnclosingElement();
        return (PackageElement) anElement;
    }

    public List<? extends Element> getAllMembers(TypeElement typeElement) {
        return new ArrayList<Element>(allMembers.get(typeElement));
    }

    public List<? extends AnnotationMirror> getAllAnnotationMirrors(Element element) {
        return null;
    }

    public boolean hides(Element element, Element element1) {
        return false;
    }

    public boolean overrides(ExecutableElement method1, ExecutableElement method2, TypeElement classElement) {
        return method1.getSimpleName().equals(method2.getSimpleName()) &&
                parametersMatch(method1.getParameters(), method2.getParameters());
    }

    private boolean parametersMatch(List<? extends VariableElement> parameters1, List<? extends VariableElement> parameters2) {
        if (parameters1.size() != parameters2.size()) return false;
        for (int i = 0; i < parameters1.size(); i++)
            if (!parametersMatch(parameters1.get(i), parameters2.get(i))) return false;
        return true;
    }

    private boolean parametersMatch(VariableElement parameter1, VariableElement parameter2) {
        return parameter1.asType().toString().equals(parameter2.asType().toString());
    }

    public String getConstantExpression(Object o) {
        return null;
    }

    public void printElements(Writer writer, Element... elements) {
    }

    public Name getName(CharSequence charSequence) {
        return null;
    }
}
