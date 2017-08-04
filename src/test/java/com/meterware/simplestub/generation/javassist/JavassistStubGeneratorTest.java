package com.meterware.simplestub.generation.javassist;

import com.meterware.simplestub.generation.StubGeneratorTestBase;
import org.junit.Assume;
import org.junit.BeforeClass;

/**
 * Tests for creating stubs using Javassist.
 */
public class JavassistStubGeneratorTest extends StubGeneratorTestBase {
    private static final JavassistStubGeneratorFactory STUB_GENERATOR_FACTORY = new JavassistStubGeneratorFactory();

    public JavassistStubGeneratorTest() {
        super(STUB_GENERATOR_FACTORY);
    }


    @BeforeClass
    public static void skipUnderJdk9() throws Exception {
        Assume.assumeTrue( isBeforeJdk9() );
    }


    private static boolean isBeforeJdk9() {
        return System.getProperty( "java.version" ).startsWith( "1." );
    }


    @Override
    protected String getImplementationType() {
        return "JA";
    }
}
