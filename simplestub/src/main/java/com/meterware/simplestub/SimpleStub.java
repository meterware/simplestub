package com.meterware.simplestub;

import java.lang.annotation.*;

/**
 * An annotation which can generate a test stub from an abstract class, generating implementations for any
 * abstract methods. By default, generated methods will be no-ops. Specifying "strict = true"
 * will cause generated methods to throw a runtime exception.
 *
 * @deprecated use {@link Stub#createStrictStub(Class, Object...)} to generate a strict stub.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SimpleStub {
    boolean strict() default false;
}
