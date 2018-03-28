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
    public static void skipUnderJdk11() throws Exception {
        Assume.assumeTrue( isBeforeJdk11() );
    }

    private static boolean isBeforeJdk11() {
        return getJavaVersion() < 11;
    }

    private static int getJavaVersion() {
        String versionString = System.getProperty("java.version");
        if (versionString.startsWith("1."))
            return toVersionNum(versionString.substring(2));
        else
            return toVersionNum(versionString);
    }

    private static int toVersionNum(String versionString) {
        StringBuilder sb = new StringBuilder(  );
        for (char c : versionString.toCharArray())
            if (Character.isDigit( c ))
                sb.append( c );
            else
                break;

        return Integer.parseInt( sb.toString() );
    }


    @Override
    protected String getImplementationType() {
        return "JA";
    }
}
