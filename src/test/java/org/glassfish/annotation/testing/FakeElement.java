package org.glassfish.annotation.testing;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

import static java.lang.reflect.Modifier.*;
import static javax.lang.model.type.TypeKind.*;

public class FakeElement implements Element {
    private Element enclosingElement;
    private Name simpleName;
    private List<Element> contents = new ArrayList<Element>();

    private List<Annotation> annotations = new ArrayList<Annotation>();

    public FakeElement(String component, Element enclosingElement) {
        simpleName = new FakeName(component);
        this.enclosingElement = enclosingElement;
        if (enclosingElement instanceof FakeElement)
            ((FakeElement) enclosingElement).contents.add(this);
    }

    public void addAnnotation(Annotation annotation) {
        annotations.add(annotation);
    }

    public TypeMirror asType() {
        return null;
    }

    public ElementKind getKind() {
        return null;
    }

    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return null;
    }

    public <A extends Annotation> A getAnnotation(Class<A> aClass) {
        for (Annotation annotation : annotations)
            if (aClass.isInstance(annotation)) return (A) annotation;
        return null;
    }

    public Set<Modifier> getModifiers() {
        return null;
    }

    public Name getSimpleName() {
        return simpleName;
    }

    public Element getEnclosingElement() {
        return enclosingElement;
    }

    public List<? extends Element> getEnclosedElements() {
        return new ArrayList<Element>(contents);
    }

    public <R, P> R accept(ElementVisitor<R, P> rpElementVisitor, P p) {
        return null;
    }

    public Name getQualifiedName() {
        return new FakeName(getQualifiedNameString());
    }

    @Override
    public String toString() {
        return getQualifiedNameString();
    }

    private String getQualifiedNameString() {
        return enclosingElement == null ? simpleName.toString()
                : enclosingElement + "." + simpleName;
    }

}

class FakeTypeElement extends FakeElement implements TypeElement {

    public FakeTypeElement(String component, Element enclosingElement) {
        super(component, enclosingElement);
    }

    public TypeMirror getSuperclass() {
        return null;
    }

    public List<? extends TypeMirror> getInterfaces() {
        return null;
    }

    public List<? extends TypeParameterElement> getTypeParameters() {
        return null;
    }

    public NestingKind getNestingKind() {
        return null;
    }

}


class FakePackageElement extends FakeElement implements PackageElement {

    private boolean unnamed;

    FakePackageElement(String component, Element enclosingElement) {
        super(component, enclosingElement);
    }

    public FakePackageElement(String name) {
        super(name, null);
        unnamed = name == null || name.length() == 0;
    }

    @Override
    public boolean isUnnamed() {
        return unnamed;
    }
}


abstract class FakeExecutableElement extends FakeElement implements ExecutableElement {

    private Set<Modifier> modifierSet;
    List<VariableElement> parameters = new ArrayList<VariableElement>();

    FakeExecutableElement(String name, Element enclosingClass, int modifiers, Class[] parameterTypes) {
        super(name, enclosingClass);
        modifierSet = createModifierSet(modifiers);
        for (Class parameterType: parameterTypes) {
            parameters.add(new FakeVariableElement( "arg" + parameters.size(), this, parameterType));
        }
    }

    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        return null;
    }

    @Override
    public List<? extends VariableElement> getParameters() {
        return new ArrayList<VariableElement>(parameters);
    }

    @Override
    public boolean isVarArgs() {
        return false;
    }

    @Override
    public List<? extends TypeMirror> getThrownTypes() {
        return null;
    }

    @Override
    public AnnotationValue getDefaultValue() {
        return null;
    }

    @Override
    public Set<Modifier> getModifiers() {
        return new HashSet<Modifier>(modifierSet);
    }

    private Set<Modifier> createModifierSet(int modifierBits) {
        Set<Modifier> modifierSet = new HashSet<Modifier>();
        if (isAbstract(modifierBits)) modifierSet.add(Modifier.ABSTRACT);
        if (isPublic(modifierBits)) modifierSet.add(Modifier.PUBLIC);
        if (isPrivate(modifierBits)) modifierSet.add(Modifier.PRIVATE);
        if (isProtected(modifierBits)) modifierSet.add(Modifier.PROTECTED);
        if (isStatic(modifierBits)) modifierSet.add(Modifier.STATIC);
        return modifierSet;
    }

    abstract boolean overrides( Method method );
}

class ConstructorElement extends FakeExecutableElement implements ExecutableElement {

    private static final FakeTypeMirror VOID = new FakeTypeMirror(void.class);

    ConstructorElement(Constructor constructor, Element enclosingClass) {
        super(enclosingClass.getSimpleName().toString(), enclosingClass, constructor.getModifiers(), constructor.getParameterTypes());
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.CONSTRUCTOR;
    }

    @Override
    public TypeMirror getReturnType() {
        return VOID;
    }

    @Override
    boolean overrides(Method method) {
        return false;
    }
}


class MethodElement extends FakeExecutableElement implements ExecutableElement {

    private TypeMirror returnType;
    private Method method;

    MethodElement(Method method, Element enclosingClass) {
        super(method.getName(), enclosingClass, method.getModifiers(), method.getParameterTypes());
        this.method = method;
        returnType = new FakeTypeMirror(method.getReturnType());
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.METHOD;
    }

    @Override
    public TypeMirror getReturnType() {
        return returnType;
    }

    @Override
    boolean overrides(Method method) {
        return !isAbstract(this.method.getModifiers()) &&
                this.method.getName().equals(method.getName()) &&
                Arrays.equals(this.method.getParameterTypes(), method.getParameterTypes());
    }
}


class FakeVariableElement extends FakeElement implements VariableElement {

    private TypeMirror type;

    FakeVariableElement(String name, Element enclosingElement, Class aClass) {
        super(name, enclosingElement);
        type = new FakeTypeMirror(aClass);
    }

    @Override
    public Object getConstantValue() {
        return null;
    }

    @Override
    public TypeMirror asType() {
        return type;
    }
}


class FakeTypeMirror implements TypeMirror {
    private Class aClass;

    FakeTypeMirror(Class aClass) {
        this.aClass = aClass;
    }

    @Override
    public TypeKind getKind() {
        if (aClass.equals(void.class))
            return VOID;
        else if (aClass.equals(char.class))
            return CHAR;
        else if (aClass.equals(byte.class))
            return BYTE;
        else if (aClass.equals(int.class))
            return INT;
        else if (aClass.equals(long.class))
            return LONG;
        else if (aClass.equals(float.class))
            return FLOAT;
        else if (aClass.equals(boolean.class))
            return BOOLEAN;
        else if (aClass.equals(double.class))
            return DOUBLE;
        else if (aClass.equals(short.class))
            return SHORT;
        else if (aClass.isArray())
            return ARRAY;
        else
            return DECLARED;
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> rpTypeVisitor, P p) {
        return null;
    }

    @Override
    public String toString() {
        if (!aClass.isArray())
            return aClass.getName();
        else {
            return new FakeTypeMirror(aClass.getComponentType()) + "[]";
        }
    }
}

