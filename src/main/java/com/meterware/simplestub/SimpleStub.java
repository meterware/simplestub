package com.meterware.simplestub;

import java.lang.annotation.*;

/**
 * An annotation which can generate a test stub from an abstract class, generating implementations for any
 * abstract methods. By default, generated methods will be no-ops. Specifying "strict = true"
 * will cause generated methods to throw a runtime exception.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SimpleStub {
    boolean strict() default false;
}
