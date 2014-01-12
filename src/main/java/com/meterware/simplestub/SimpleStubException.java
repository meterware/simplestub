package com.meterware.simplestub;

import javassist.NotFoundException;

/**
 * An exception thrown when the simplestub library detects a problem in a stub definition.
 */
public class SimpleStubException extends RuntimeException {
    public SimpleStubException(String message) {
        super(message);
    }

    public SimpleStubException(String message, Throwable cause) {
        super(message, cause);
    }
}
