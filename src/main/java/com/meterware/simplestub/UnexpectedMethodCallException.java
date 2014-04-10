package com.meterware.simplestub;

/**
 * Thrown by a strict stub if one of its undefined methods is called.
 */
public class UnexpectedMethodCallException extends SimpleStubException {
    public UnexpectedMethodCallException(String message) {
        super(message);
    }
}
