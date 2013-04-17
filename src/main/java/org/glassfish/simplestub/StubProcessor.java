package org.glassfish.simplestub;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes({"org.glassfish.simplestub.SimpleStub"})
@SupportedSourceVersion(value = SourceVersion.RELEASE_6)
public class StubProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnvironment) {
        System.out.println("REG-> debugging enabled");
        if (roundEnvironment.processingOver()) return false;
        if (typeElements.isEmpty()) return false;

        generateStubClasses(roundEnvironment.getElementsAnnotatedWith(SimpleStub.class));
        return true;
    }

    private void generateStubClasses(Set<? extends Element> classElements) {
        for (Element classElement : classElements)
            generateStubClass(classElement);
    }

    private void generateStubClass(Element classElement) {
        try {
            writeClassFile(new ClassGenerator(((TypeElement) classElement), processingEnv.getElementUtils()));
        } catch (IOException e) {
            throw new RuntimeException( "Unable to write stub for " + classElement, e );
        }
    }

/**/
/*/
    private void dump(Element classElement) {
        System.out.println("REG-> checking " + classElement );
        System.out.println("      package is " + processingEnv.getElementUtils().getPackageOf(classElement));
        System.out.println("      enclosing element is " + classElement.getEnclosingElement());
        if (classElement instanceof ExecutableElement) printGenericElements("       ", (ExecutableElement) classElement);
//        for (Element member : processingEnv.getElementUtils().getAllMembers((TypeElement) classElement)) {
//            System.out.println("    " + member);
//        }

        for (Element element : classElement.getEnclosedElements()) {
            System.out.println("      encloses: " + element + " (" + element.getSimpleName() + " with " + element.getModifiers() + ")");
            if (element instanceof ExecutableElement) {
                ExecutableElement ex = (ExecutableElement) element;
                printGenericElements("         ", ex);
                System.out.println("         kind: " + ex.getKind());
                System.out.println("            return type: " + ex.getReturnType() + " (" + ex.getReturnType().getKind() + ")");
                if (ex.getParameters() != null) {
                    for (VariableElement v : ex.getParameters()) {
                        System.out.println("            parameter name: " + v);
                        System.out.println("            type: " + v.asType());
                        System.out.println("            kind: " + v.getKind());
                    }
                }
            }
        }
    }

    private void printGenericElements(String padding, ExecutableElement ex) {
        List<? extends TypeParameterElement> typeParameters = ex.getTypeParameters();
        if (typeParameters == null || typeParameters.isEmpty()) return;

        System.out.println(padding + "IS GENERIC");
        for (TypeParameterElement typeParameter : typeParameters) {
            System.out.println(padding + "generic parameter " + typeParameter.asType());
            List<? extends TypeMirror> bounds = typeParameter.getBounds();
            if (bounds != null) {
                for (TypeMirror bound : bounds) {
                    System.out.println(padding + "  with bound " + bound);
                }
            }
        }
    }
/**/

    private void writeClassFile(ClassGenerator generator) throws IOException {
        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(generator.getStubClassName());
        generator.generateStub(sourceFile.openWriter());
    }

}
