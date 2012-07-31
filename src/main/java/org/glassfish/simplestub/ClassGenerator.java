package org.glassfish.simplestub;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static javax.lang.model.type.TypeKind.*;

class ClassGenerator {

    static final String CLASS_NAME_SUFFIX = "__org_glassfish_SimpleStub";

    private final List<ExecutableElement> abstractMethodEs = new ArrayList<ExecutableElement>();
    private final SimpleStub simpleStub;

    private TypeElement classElement;
    private Elements elementUtils;
    private static final List<TypeKind> NUMERIC_KINDS = Arrays.asList(INT, CHAR, SHORT, LONG, FLOAT, DOUBLE);

    ClassGenerator(TypeElement annotatedClass, Elements elementUtils) throws ClassNotFoundException {
        classElement = annotatedClass;
        this.elementUtils = elementUtils;
        simpleStub = annotatedClass.getAnnotation(SimpleStub.class);
        computeAbstractMethodElements();
    }

    String getClassName() {
        return classElement.getQualifiedName().toString();
    }

    String getStubClassName() {
        return getPackagePrefix() + getSimpleStubClassName();

    }

    private String getPackagePrefix() {
        PackageElement thePackage = elementUtils.getPackageOf(classElement);
        return thePackage.isUnnamed() ? "" : thePackage.getQualifiedName() + ".";
    }

    String getSimpleClassName() {
        if (isOuterClass())
            return classElement.getSimpleName().toString();
        else
            return classElement.getEnclosingElement().getSimpleName().toString() + '.' + classElement.getSimpleName();
    }

    private boolean isOuterClass() {
        return classElement.getEnclosingElement() instanceof PackageElement;
    }

    String getSimpleStubClassName() {
        if (isOuterClass())
            return getSimpleClassName() + CLASS_NAME_SUFFIX;
        else
            return classElement.getEnclosingElement().getSimpleName().toString() + "__" + classElement.getSimpleName() + CLASS_NAME_SUFFIX;
    }

    String getPackageName() {
        PackageElement packageElement = elementUtils.getPackageOf(classElement);
        return packageElement.isUnnamed() ? "" : packageElement.getQualifiedName().toString();
    }

    void generateStub(Writer writer) throws IOException, ClassNotFoundException {
        BufferedWriter br = new BufferedWriter(writer);
        writePackageHeader(br);
        writeClassHeader(br);
        writeConstructors(br);
        writeMethods(br);
        writeClassFooter(br);
        br.close();
    }

    private void writePackageHeader(BufferedWriter br) throws IOException {
        if (getPackageName().length() > 0) writeLine(br, "package " + getPackageName() + ';');
    }

    private void writeClassHeader(BufferedWriter br) throws IOException {
        writeLine(br, "public class " + getSimpleStubClassName() + " extends " + getSimpleClassName() + " {");
    }

    private void writeConstructors(BufferedWriter br) throws IOException {
        for (ExecutableElement constructor : getConstructorElements())
            writeLine(br, "    " + generateConstructor(constructor) );
    }

    private void writeMethods(BufferedWriter br) throws ClassNotFoundException, IOException {
        for (ExecutableElement abstractMethod : getAbstractMethodElements())
            writeLine(br, "    " + generateMethod(abstractMethod));
    }

    private void writeClassFooter(BufferedWriter br) throws IOException {
        writeLine(br, "}");
    }

    private void writeLine(BufferedWriter br, String line) throws IOException {
        br.write(line);
        br.newLine();
    }

    ExecutableElement[] getAbstractMethodElements() {
        return abstractMethodEs.toArray(new ExecutableElement[abstractMethodEs.size()]);
    }

    private void computeAbstractMethodElements() {
        for (Element element : ElementFilter.methodsIn(elementUtils.getAllMembers(classElement)))
            if (accessibleAbstractMethod((ExecutableElement) element))
                abstractMethodEs.add((ExecutableElement) element);
    }

    private boolean accessibleAbstractMethod(ExecutableElement element) {
        return isAbstract(element) && !isPrivate(element) && !isStatic(element);
    }

    private boolean isAbstract(Element method) {
        return method.getModifiers().contains(javax.lang.model.element.Modifier.ABSTRACT);
    }

    private boolean isPrivate(Element method) {
        return method.getModifiers().contains(javax.lang.model.element.Modifier.PRIVATE);
    }

    private boolean isStatic(Element method) {
        return method.getModifiers().contains(javax.lang.model.element.Modifier.STATIC);
    }

    public ExecutableElement[] getConstructorElements() {
        List<ExecutableElement> constructors = new ArrayList<ExecutableElement>();
        for (Element element : classElement.getEnclosedElements()) {
            if (element.getKind() == ElementKind.CONSTRUCTOR && !isPrivate(element)) constructors.add((ExecutableElement) element);
        }
        return constructors.toArray(new ExecutableElement[constructors.size()]);
    }

    public String generateConstructor(ExecutableElement constructor) {
        return "public " + getSimpleStubClassName() +  createParameterString(constructor.getParameters()) + createConstructorBody( constructor );
    }

    private String createConstructorBody(ExecutableElement constructor) {
        StringBuilder sb = new StringBuilder();
        boolean isFirstArg = true;
        for (VariableElement element : constructor.getParameters()) {
            if (!isFirstArg) sb.append(", ");
            sb.append(element.getSimpleName());
            isFirstArg = false;
        }
        return " { super(" + sb.toString() + "); }";
    }

    String generateMethod(ExecutableElement method) {
        return createMethodHeader(method) + createParameterString(method.getParameters()) + createMethodBody(method);
    }

    private String createMethodHeader(ExecutableElement method) {
        return getVisibility(method) + getType(method.getReturnType()) + ' ' + method.getSimpleName().toString();
    }

    private String createParameterString(List<? extends VariableElement> parameters) {
        StringBuilder sb = new StringBuilder();
        int argNum = 0;
        for (VariableElement parameter : parameters) {
            if (argNum++ != 0) sb.append(", ");
            sb.append(getType(parameter.asType())).append(' ').append(parameter.getSimpleName());
        }
        return "(" + sb.toString() + ")";
    }

    private String getType(TypeMirror typeMirror) {
        return typeMirror.toString();
    }

    private String createMethodBody(ExecutableElement method) {
        if (simpleStub.strict())
            return createStringMethodBody(method);
        else
            return createStubMethodBody(method);
    }

    private String createStubMethodBody(ExecutableElement method) {
        if (method.getReturnType().toString().equals("void"))
            return " {}";
        else if (method.getReturnType().toString().equals("boolean"))
            return " { return false; }";
        else if (NUMERIC_KINDS.contains(method.getReturnType().getKind()))
            return " { return 0; }";
        else
            return " { return null; }";
    }

    private String createStringMethodBody(ExecutableElement method) {
        StringBuilder sb = new StringBuilder();
        boolean firstParameter = true;
        for (VariableElement parameterType : method.getParameters()) {
            if (!firstParameter)
                sb.append(',');
            firstParameter = false;
            sb.append(getType(parameterType.asType()));
        }
        return " { throw new org.glassfish.simplestub.SimpleStubException( \"Unexpected call to " + method.getSimpleName().toString() + '(' + sb + ")\" ); }";
    }


    private String getVisibility(ExecutableElement method) {
        if (method.getModifiers().contains(javax.lang.model.element.Modifier.PUBLIC))
            return "public ";
        else if (method.getModifiers().contains(javax.lang.model.element.Modifier.PROTECTED))
            return "protected ";
        else if (method.getModifiers().contains(javax.lang.model.element.Modifier.PRIVATE))
            return "private ";
        else
            return "";
    }
}
