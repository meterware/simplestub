package org.glassfish.simplestub;

public class SimpleStubException extends RuntimeException {
    public SimpleStubException(String message) {
        super(message);
    }

    public SimpleStubException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
