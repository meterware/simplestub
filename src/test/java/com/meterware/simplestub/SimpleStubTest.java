package com.meterware.simplestub;

import org.junit.Test;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the specification of the @SimpleStub annotation.
 */
public class SimpleStubTest {

    @Test
    public void annotation_isDocumented() {
        assertNotNull(SimpleStub.class.getAnnotation(Documented.class));
    }

    @Test
    public void annotation_hasRuntimeRetention() {
        assertEquals(RetentionPolicy.RUNTIME, SimpleStub.class.getAnnotation(Retention.class).value());
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
    public void annotation_hasStrictBooleanParameter() throws NoSuchMethodException {
        assertEquals(boolean.class, SimpleStub.class.getMethod("strict").getReturnType());
    }

    @Test
    public void annotationStringParameter_defaultsToFalse() throws NoSuchMethodException {
        Method strict = SimpleStub.class.getMethod("strict");
        assertEquals(Boolean.FALSE, strict.getDefaultValue());
    }
}
