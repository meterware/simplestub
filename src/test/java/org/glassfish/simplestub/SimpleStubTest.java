package org.glassfish.simplestub;

import org.glassfish.simplestub.classes.SimpleAbstractTestClass;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;

import static org.junit.Assert.*;

public class SimpleStubTest extends SimpleStubTestBase {

    private Map<URI, StringWriter> writers = new HashMap<URI, StringWriter>();
    private Set<TypeElement> annotationsSupported = new HashSet<TypeElement>();
    private final RoundEnvironment roundEnvironment = new TestRoundEnvironment();
    private final TestProcessingEnvironment processingEnvironment = new TestProcessingEnvironment();

    @Before
    public void setUp() throws Exception {
        annotationsSupported.add(createAnnotationElement(SimpleStub.class));
    }

    @Test
    public void annotation_isDocumented() {
        assertNotNull(SimpleStub.class.getAnnotation(Documented.class));
    }

    @Test
    public void annotation_hasSourceRetention() {
        assertEquals(RetentionPolicy.SOURCE, SimpleStub.class.getAnnotation(Retention.class).value());
    }

    @Test
    public void annotation_targetsTypes() {
        assertAnnotationTargetsElement(SimpleStub.class, ElementType.TYPE);
    }

    private void assertAnnotationTargetsElement(Class<?> aClass, ElementType elementType) {
        Target annotation = aClass.getAnnotation(Target.class);
        assertTrue(Arrays.asList(annotation.value()).contains(elementType));
    }

    @Test
    public void annotation_hasBooleanStrictParameter() throws NoSuchMethodException {
        Method strict = SimpleStub.class.getMethod("strict");
        assertNotNull(strict);
        assertEquals(boolean.class, strict.getReturnType());
    }

    @Test
    public void annotationStringParameter_defaultsToFalse() throws NoSuchMethodException {
        Method strict = SimpleStub.class.getMethod("strict");
        assertEquals(Boolean.FALSE, strict.getDefaultValue());
    }

    @Test
    public void processor_supportsExceptionWrapperAnnotation() {
        assertProcessorSupportsAnnotation(StubProcessor.class, SimpleStub.class);
    }

    private void assertProcessorSupportsAnnotation(Class<?> aClass, Class<?> anAnnotationClass) {
        SupportedAnnotationTypes annotation = aClass.getAnnotation(SupportedAnnotationTypes.class);
        assertTrue(Arrays.asList(annotation.value()).contains(anAnnotationClass.getName()));
    }

    @Test
    public void processor_extendsAbstractProcessor() {
        Class superclass = StubProcessor.class.getSuperclass();
        assertEquals(AbstractProcessor.class, superclass);
    }

    @Test
    public void processer_isRegistered() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("META-INF/services/javax.annotation.processing.Processor");
        assertNotNull("Resource not found in classpath",inputStream);
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(isr);
        assertEquals(StubProcessor.class.getName(), reader.readLine());
    }

    @Test
    public void processorGeneratesFiles() throws Exception {
        createAnnotatedClass(SimpleAbstractTestClass.class);
        processAnnotations();

        assertFalse(writers.isEmpty());
        Map.Entry<URI, StringWriter> entry = writers.entrySet().iterator().next();
        assertEquals(System.getProperty("user.dir") + "/SOURCE_OUTPUT/org.glassfish.simplestub.classes.SimpleAbstractTestClass__org_glassfish_SimpleStub", entry.getKey().getRawPath());
        assertTrue(entry.getValue().getBuffer().toString().contains("public class SimpleAbstractTestClass__org_glassfish_SimpleStub extends"));
    }

    private void processAnnotations() {
        StubProcessor processor = new StubProcessor();
        processor.init(processingEnvironment);
        processor.process(annotationsSupported, roundEnvironment);
    }


    class TestRoundEnvironment implements RoundEnvironment {
        boolean processingOver;

        public boolean processingOver() {
            return processingOver;
        }

        public boolean errorRaised() { return false; }
        public Set<? extends Element> getRootElements() { return null; }

        @Override
        public Set<? extends Element> getElementsAnnotatedWith(TypeElement typeElement) {
            return null;
        }

        @Override
        public Set<? extends Element> getElementsAnnotatedWith(Class<? extends Annotation> aClass) {
            return getAnnotatedElements(aClass);
        }
    }


    class TestProcessingEnvironment implements ProcessingEnvironment {

        private TestFiler filer;

        @Override
        public Map<String, String> getOptions() {
            return null;
        }

        @Override
        public Messager getMessager() {
            return null;
        }

        @Override
        public Filer getFiler() {
            if (filer == null) filer = new TestFiler();
            return filer;
        }

        @Override
        public Elements getElementUtils() {
            return getElements();
        }

        @Override
        public Types getTypeUtils() {
            return null;
        }

        @Override
        public SourceVersion getSourceVersion() {
            return null;
        }

        @Override
        public Locale getLocale() {
            return null;
        }


    }

    class TestFiler implements Filer {

        @Override
        public JavaFileObject createSourceFile(CharSequence name, Element... elements) throws IOException {
            File file = new File(StandardLocation.SOURCE_OUTPUT.getName());
            file = new File(file, name.toString());
            return createFileObject(file);
        }

        @Override
        public JavaFileObject createClassFile(CharSequence charSequence, Element... elements) throws IOException {
            return null;
        }

        @Override
        public FileObject createResource(JavaFileManager.Location location, CharSequence pkg, CharSequence trailing, Element... elements) throws IOException {
            File file = new File(location.getName());
            file = new File(file, pkg.toString().replace('.','/'));
            file = new File(file, trailing.toString());
            return createFileObject(file);
        }

        private JavaFileObject createFileObject(File file) {
            return new TestFileObject(file.toURI());
        }

        @Override
        public FileObject getResource(JavaFileManager.Location location, CharSequence charSequence, CharSequence charSequence1) throws IOException {
            return null;
        }

    }

    class TestFileObject implements JavaFileObject {

        private URI uri;


        TestFileObject(URI uri) {
            this.uri = uri;
        }

        @Override
        public URI toUri() {
            return uri;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public InputStream openInputStream() throws IOException {
            return null;
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return null;
        }

        @Override
        public Reader openReader(boolean b) throws IOException {
            return null;
        }

        @Override
        public CharSequence getCharContent(boolean b) throws IOException {
            return null;
        }

        @Override
        public Writer openWriter() throws IOException {
            StringWriter writer = new StringWriter();
            writers.put(uri,writer);
            return writer;
        }

        @Override
        public long getLastModified() {
            return 0;
        }

        @Override
        public boolean delete() {
            return false;
        }

        @Override
        public Kind getKind() {
            return null;
        }

        @Override
        public boolean isNameCompatible(String s, Kind kind) {
            return false;
        }

        @Override
        public NestingKind getNestingKind() {
            return null;
        }

        @Override
        public Modifier getAccessLevel() {
            return null;
        }
    }

}
