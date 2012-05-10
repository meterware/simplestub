package org.glassfish.simplestub;

import java.lang.annotation.*;

/**
 * An annotation which can generate a test stub from an abstract class.
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface SimpleStub {
    boolean strict() default false;
}
