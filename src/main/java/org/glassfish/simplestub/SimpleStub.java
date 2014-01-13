package org.glassfish.simplestub;

import java.lang.annotation.*;

/**
 * An annotation which can generate a test stub from an abstract class.
 * @deprecated use {com.meterware.simplestub.SimpleStub}
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface SimpleStub {
    boolean strict() default true;
}
