package org.glassfish.annotation.testing;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

import static java.lang.reflect.Modifier.*;
import static javax.lang.model.type.TypeKind.*;

/**
 * This class and its subclasses are implementations of the Element class and its subclasses defined
 * in the javax.lang.model package hierarchy. The classes are used internally by the compiler and
 * provided at annotation processing time.
 */
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

    FakeExecutableElement(String name, Element enclosingClass, int modifiers, Type[] parameterTypes) {
        super(name, enclosingClass);
        modifierSet = createModifierSet(modifiers);
        for (Type parameterType: parameterTypes) {
            parameters.add(new FakeVariableElement( "argument" + parameters.size(), this, parameterType));
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
    private List<FakeTypeParameterElement> genericTypes;

    MethodElement(Method method, Element enclosingClass) {
        super(method.getName(), enclosingClass, method.getModifiers(), method.getGenericParameterTypes());
        TypeVariable<Method>[] typeParameters = method.getTypeParameters();
        for (TypeVariable<Method> typeParameter : typeParameters) {
            addGenericParameter(typeParameter);
        }
        this.method = method;
        returnType = new FakeTypeMirror(method.getGenericReturnType());
    }

    public FakeTypeParameterElement addGenericParameter(TypeVariable<Method> typeVariable) {
        if (genericTypes == null)
            genericTypes = new ArrayList<FakeTypeParameterElement>();
        FakeTypeParameterElement parameterElement = new FakeTypeParameterElement(typeVariable, this);
        genericTypes.add(parameterElement);
        for (Type type : typeVariable.getBounds()) {
            parameterElement.addBound(type);
        }
        return parameterElement;
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
    public List<? extends TypeParameterElement> getTypeParameters() {
        return genericTypes;
    }

    @Override
    boolean overrides(Method method) {
        return !isAbstract(this.method.getModifiers()) &&
                this.method.getName().equals(method.getName()) &&
                Arrays.equals(this.method.getParameterTypes(), method.getParameterTypes());
    }
}


class FakeVariableElement extends FakeElement implements VariableElement {

    private TypeMirror typeMirror;

    FakeVariableElement(String name, Element enclosingElement, Type aType) {
        super(name, enclosingElement);
        typeMirror = new FakeTypeMirror(aType);
    }

    @Override
    public Object getConstantValue() {
        return null;
    }

    @Override
    public TypeMirror asType() {
        return typeMirror;
    }
}


class FakeTypeMirror implements TypeMirror {
    private Type aType;

    FakeTypeMirror(Type type) {
        this.aType = type;
    }

    @Override
    public TypeKind getKind() {
        if (aType instanceof TypeVariable<?>)
            return TYPEVAR;
        else
            return getKind((Class) aType);
    }

    private TypeKind getKind(Class aClass) {
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
        if (aType instanceof TypeVariable<?>)
            return ((TypeVariable) aType).getName();
        else if (aType instanceof Class)
            return toString((Class) aType);
        else
            return aType.toString();
    }

    private String toString(Class aClass) {
        if (!aClass.isArray())
            return aClass.getName();
        else {
            return new FakeTypeMirror(aClass.getComponentType()) + "[]";
        }
    }
}


class FakeTypeParameterElement extends FakeElement implements TypeParameterElement {
    private Element enclosingElement;
    private Type type;
    private List<FakeTypeMirror> bounds;

    FakeTypeParameterElement(TypeVariable<Method> type, Element enclosingElement) {
        super(type.getName(), enclosingElement);
        this.type = type;
        this.enclosingElement = enclosingElement;
    }

    void addBound(Type type) {
        if (bounds == null)
            bounds = new ArrayList<FakeTypeMirror>();
        bounds.add(new FakeTypeMirror(type));
    }

    @Override
    public Element getGenericElement() {
        return enclosingElement;
    }

    @Override
    public List<? extends TypeMirror> getBounds() {
        return bounds;
    }

    @Override
    public TypeMirror asType() {
        return new FakeTypeMirror(type);
    }
}

