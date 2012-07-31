package org.glassfish.simplestub;

import org.glassfish.simplestub.classes.AbstractClass2;
import org.glassfish.simplestub.classes.ExtendingClass;
import org.glassfish.simplestub.classes.Interface1;
import org.glassfish.simplestub.classes.SimpleAbstractTestClass;
import org.junit.Before;
import org.junit.Test;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.CookiePolicy;
import java.util.*;

import static org.junit.Assert.*;

public class ClassGeneratorTest extends SimpleStubTestBase {

    private ClassGenerator generator;
    private Method method1;
    private Method doSomething;
    private Method doSomething2;
    private Method doSomething3;
    private Method getSwitches;
    private Constructor<SimpleAbstractTestClass> abstractTestClassConstructor;
    private Constructor<ExtendingClass> twoArgConstructor;
    private Method firstLetter;

    @Before
    public void setUp() throws Exception {
        method1 = Interface1.class.getDeclaredMethod("method1");
        doSomething = SimpleAbstractTestClass.class.getDeclaredMethod("doSomething", int.class);
        doSomething2 = SimpleAbstractTestClass.class.getDeclaredMethod("doSomething2", int.class, List.class);
        doSomething3 = SimpleAbstractTestClass.class.getDeclaredMethod("doSomething3", List.class);
        getSwitches = ExtendingClass.class.getDeclaredMethod("getSwitches");
        twoArgConstructor = ExtendingClass.class.getDeclaredConstructor(BigInteger.class, List.class);
        abstractTestClassConstructor = SimpleAbstractTestClass.class.getConstructor();

        firstLetter = AbstractClass2.class.getDeclaredMethod("firstLetter");
    }


    @Test
    public void givenClassElement_extractFullName() throws ClassNotFoundException {
        parseClass(SimpleAbstractTestClass.class);
        assertEquals(SimpleAbstractTestClass.class.getName(), generator.getClassName());
    }

    @Test
    public void givenClassElement_getPackage() throws ClassNotFoundException {
        parseClass(SimpleAbstractTestClass.class);
        assertEquals("org.glassfish.simplestub.classes", generator.getPackageName());
    }

    @Test
    public void whenClassIsStaticInnerClass_includeParentClassInType() throws ClassNotFoundException {
        parseClass(InnerClass1.class);
        assertEquals("ClassGeneratorTest.InnerClass1", generator.getSimpleClassName());
        assertEquals("ClassGeneratorTest__InnerClass1__org_glassfish_SimpleStub", generator.getSimpleStubClassName());
        assertEquals("org.glassfish.simplestub.ClassGeneratorTest__InnerClass1__org_glassfish_SimpleStub", generator.getStubClassName());
    }

    @Test
    public void givenClassElement_findDeclaredAbstractMethodElement() throws NoSuchMethodException, ClassNotFoundException {
        parseClass(SimpleAbstractTestClass.class);
        ExecutableElement[] abstractMethods = generator.getAbstractMethodElements();
        assertFoundMethod(abstractMethods, "getPolicy", CookiePolicy.class);
    }

    @Test
    public void givenClassElement_findProtectedMethodElement() throws ClassNotFoundException, NoSuchMethodException {
        parseClass(SimpleAbstractTestClass.class);
        ExecutableElement[] abstractMethods = generator.getAbstractMethodElements();
        assertFoundMethod(abstractMethods, "doSomething", int.class, int.class);
    }

    @Test
    public void givenClassElement_findInheritedAbstractProtectedMethodElement() throws ClassNotFoundException, NoSuchMethodException {
        parseClass(ExtendingClass.class);
        ExecutableElement[] abstractMethods = generator.getAbstractMethodElements();
        assertNotFoundMethod(abstractMethods, "method1", void.class);
        assertNotFoundMethod(abstractMethods, "doSomething", int.class, int.class);
        assertFoundMethod(abstractMethods, "doSomething2", String.class, int.class, List.class);
    }

    private void assertFoundMethod(ExecutableElement[] abstractMethods, String methodName, Class<?> returnValue, Class<?>... parameters) {
        for (ExecutableElement abstractMethod : abstractMethods) {
            if (matchesMethod( abstractMethod, methodName, returnValue, parameters))
                return;
        }
        fail("Did not find matching method with name " + methodName);
    }

    private void assertNotFoundMethod(ExecutableElement[] abstractMethods, String methodName, Class<?> returnValue, Class<?>... parameters) {
        for (ExecutableElement abstractMethod : abstractMethods) {
            if (matchesMethod( abstractMethod, methodName, returnValue, parameters))
                fail( "Unexpectedly found matching method with name " + methodName);
        }
    }

    private boolean matchesMethod(ExecutableElement abstractMethod, String methodName, Class<?> returnValue, Class<?>... parameters) {
        return abstractMethod.getSimpleName().toString().equals(methodName)
                && matchesType(returnValue, abstractMethod.getReturnType())
                && matchesTypes(parameters, abstractMethod.getParameters());
    }

    private boolean matchesTypes(Class<?>[] parameters, List<? extends VariableElement> parameterElements) {
        if (parameters.length != parameterElements.size()) return false;
        for (int i = 0; i < parameters.length; i++) {
            if (!matchesType(parameters[i], parameterElements.get(i).asType())) return false;
        }
        return true;
    }

    private boolean matchesType(Class<?> typeClass, TypeMirror typeMirror) {
        return typeClass.getName().equals(typeMirror.toString());
    }

    @Test
    public void findDefaultConstructorElement() throws ClassNotFoundException, NoSuchMethodException {
        parseClass(SimpleAbstractTestClass.class);
        checkConstructors(new Class[]{});
    }

    private void checkConstructors(Class[]... parameters) {
        ExecutableElement[] constructors = generator.getConstructorElements();
        assertEquals(parameters.length, constructors.length);
        for (int i = 0; i < parameters.length; i++)
            checkConstructor(constructors[i], parameters[i]);
    }

    private void checkConstructor(ExecutableElement constructor, Class... parameters) {
        assertEquals(ElementKind.CONSTRUCTOR, constructor.getKind());
        assertEquals(parameters.length, constructor.getParameters().size());
        for (int i = 0; i < parameters.length; i++) {
            VariableElement variableElement = constructor.getParameters().get(i);
            assertEquals(parameters[i].getName(), variableElement.asType().toString());
        }
    }

    @Test
    public void findAccessibleConstructorElements() throws ClassNotFoundException, NoSuchMethodException {
        parseClass(ExtendingClass.class);
        checkConstructors(new Class[]{BigInteger.class, List.class}, new Class[]{int.class, List.class}, new Class[]{});
    }

    @Test
    public void givenClassElement_createStubClassName() throws ClassNotFoundException {
        parseClass(ExtendingClass.class);
        assertEquals("ExtendingClass__org_glassfish_SimpleStub", generator.getSimpleStubClassName());
    }

    @Test
    public void givenClassElement_generateNoArgStubMethod() throws ClassNotFoundException, NoSuchMethodException {
        shouldGenerateMethod(ExtendingClass.class, method1, false,
                "public void method1() {}");
    }

    private void shouldGenerateMethod(Class<?> aClass, Method method, boolean strict, String expected) throws ClassNotFoundException {
        parseClass(aClass, strict);
        assertEquals(expected, generator.generateMethod(createMethodElement(method)));
    }

    @Test
    public void givenClassElement_generateStrictNoArgStubMethod() throws ClassNotFoundException, NoSuchMethodException {
        shouldGenerateMethod(ExtendingClass.class, method1, true,
                "public void method1() { throw new org.glassfish.simplestub.SimpleStubException( \"Unexpected call to method1()\" ); }");
    }

    @Test
    public void givenClassElement_generateOneArgProtectedStubMethod() throws ClassNotFoundException, NoSuchMethodException {
        shouldGenerateMethod(ExtendingClass.class, doSomething, false,
                "protected int doSomething(int argument0) { return 0; }");
    }

    @Test
    public void givenClassElement_generateStrictOneArgProtectedStubMethod() throws ClassNotFoundException, NoSuchMethodException {
        shouldGenerateMethod(ExtendingClass.class, doSomething, true,
                "protected int doSomething(int argument0) { throw new org.glassfish.simplestub.SimpleStubException( \"Unexpected call to doSomething(int)\" ); }");
    }

    @Test
    public void givenClassElement_generateTwoArgPackageStubMethod() throws ClassNotFoundException, NoSuchMethodException {
        shouldGenerateMethod(ExtendingClass.class, doSomething2, false,
                "java.lang.String doSomething2(int argument0, java.util.List argument1) { return null; }");
    }

    @Test
    public void givenClassElement_generateStrictTwoArgPackageStubMethod() throws ClassNotFoundException, NoSuchMethodException {
        shouldGenerateMethod(ExtendingClass.class, doSomething2, true,
                "java.lang.String doSomething2(int argument0, java.util.List argument1) { throw new org.glassfish.simplestub.SimpleStubException( \"Unexpected call to doSomething2(int,java.util.List)\" ); }");
    }

    @Test
    public void givenClassElement_generateBooleanStubMethodWithParameterizedArgument() throws ClassNotFoundException, NoSuchMethodException {
        shouldGenerateMethod(ExtendingClass.class, doSomething3, false,
                "boolean doSomething3(java.util.List argument0) { return false; }");
    }

    @Test
    public void givenClassElement_generateArrayStubMethod() throws ClassNotFoundException, NoSuchMethodException {
        shouldGenerateMethod(ExtendingClass.class, getSwitches, false,
                "public java.lang.Boolean[] getSwitches() { return null; }");
    }

    @Test
    public void givenClassElement_generateMethodWithCharReturn() throws ClassNotFoundException, NoSuchMethodException {
        shouldGenerateMethod(AbstractClass2.class, firstLetter, false,
                "public char firstLetter() { return 0; }");
    }

    @Test
    public void createDefaultConstructor() throws ClassNotFoundException {
        shouldGenerateConstructor(SimpleAbstractTestClass.class, abstractTestClassConstructor,
                "public SimpleAbstractTestClass__org_glassfish_SimpleStub() { super(); }");
    }

    private void shouldGenerateConstructor(Class<?> aClass, Constructor<?> constructor, String expected) throws ClassNotFoundException {
        parseClass(aClass);
        String actual = generator.generateConstructor(createConstructorElement(constructor));
        assertEquals(expected, actual);
    }

    @Test
    public void createTwoArgConstructor() throws ClassNotFoundException {
        shouldGenerateConstructor(ExtendingClass.class, twoArgConstructor,
                "public ExtendingClass__org_glassfish_SimpleStub(java.math.BigInteger argument0, java.util.List argument1) { super(argument0, argument1); }");
    }

    @Test
    public void generateExtendingStubClass() throws ClassNotFoundException, IOException {
        StringWriter writer = new StringWriter();
        parseClass(ExtendingClass.class);
        generator.generateStub(writer);
        writer.close();

        assertText(writer.getBuffer().toString(),
                "package org.glassfish.simplestub.classes;",
                "",
                "public class ExtendingClass__org_glassfish_SimpleStub extends ExtendingClass {",
                "",
                "    public ExtendingClass__org_glassfish_SimpleStub() { super(); }",
                "    public ExtendingClass__org_glassfish_SimpleStub(java.math.BigInteger argument0, java.util.List argument1) { super(argument0, argument1); }",
                "    public ExtendingClass__org_glassfish_SimpleStub(int argument0, java.util.List argument1) { super(argument0, argument1); }",
                "",
                "    java.lang.String doSomething2(int argument0, java.util.List argument1) { return null; }",
                "    boolean doSomething3(java.util.List argument0) { return false; }",
                "    public java.lang.Boolean[] getSwitches() { return null; }",
                "    java.net.CookiePolicy getPolicy() { return null; }",
                "",
                "}");
    }

    @Test
    public void generateNoPackageStubClass() throws ClassNotFoundException, IOException {
        StringWriter writer = new StringWriter();
        parseNoPackageClass(SimpleAbstractTestClass.class);
        generator.generateStub(writer);
        writer.close();

        assertText(writer.getBuffer().toString(),
                "public class SimpleAbstractTestClass__org_glassfish_SimpleStub extends SimpleAbstractTestClass {",
                "",
                "    public SimpleAbstractTestClass__org_glassfish_SimpleStub() { super(); }",
                "",
                "    public void method1() {}",
                "    protected int doSomething(int argument0) { return 0; }",
                "    java.lang.String doSomething2(int argument0, java.util.List argument1) { return null; }",
                "    boolean doSomething3(java.util.List argument0) { return false; }",
                "    java.net.CookiePolicy getPolicy() { return null; }",
                "",
                "}");
    }

    /**
     * This method parses a string into a series of expected lines. Empty lines in the 'expected' parameter have
     * a special meaning: the order of lines between pairs of empty lines is ignored.
     * @param actualString the generated data
     * @param expectedLines the expected data
     */
    private void assertText(String actualString, String... expectedLines) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(actualString));

        List<String> stringsToMatch = new ArrayList<String>();
        for (String expectedLine : expectedLines) {
            if (expectedLine.length() != 0) {
                stringsToMatch.add( expectedLine );
            } else {
                verifyLines( reader, stringsToMatch );
                stringsToMatch.clear();
            }
        }
        verifyLines( reader, stringsToMatch );

        String line = readNextNonEmptyLine(reader);

        if (line != null) fail( "Unexpected output: " + line);
    }

    private String readNextNonEmptyLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        while (line != null && line.length() == 0)
            line = reader.readLine();
        return line;
    }

    private void verifyLines(BufferedReader reader, List<String> stringsToMatch) throws IOException {
        while (!stringsToMatch.isEmpty()) {
            String line = readNextNonEmptyLine(reader);
            if (line == null)
                fail( "failed to generate required lines " + stringsToMatch );
            else if (!stringsToMatch.contains(line))
                fail( "unexpected line [" + line + "]\n  while looking for " + stringsToMatch );
            else
                stringsToMatch.remove(line);
        }
    }

    // TODO non-static inner classes, generic classes, generic methods, overloaded methods, non-int return types
    // TODO non abstract class, private class, private constructor, multiply nested inner class

    private void parseClass(Class<?> aClass) throws ClassNotFoundException {
        parseClass(aClass,false);
    }

    private void parseNoPackageClass(Class<?> aClass) throws ClassNotFoundException {
        generator = new ClassGenerator(createAnnotatedNoPackageClass(aClass, false), getElements());
    }

    private void parseClass(Class<?> aClass, boolean strict) throws ClassNotFoundException {
        generator = new ClassGenerator(createAnnotatedClass(aClass, strict), getElements());
    }

    abstract static class InnerClass1 implements Interface1 {}

    abstract class InnerClass2 implements Interface1 {}


}
