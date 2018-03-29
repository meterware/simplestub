package com.meterware.simplestub;
/*
 * Copyright (c) 2014-2018 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

/**
 * An exception thrown when the simplestub library detects a problem in a stub definition.
 *
 * @author Russell Gold
 */
public class SimpleStubException extends RuntimeException {
    public SimpleStubException(String message, Object... parameters) {
        super(String.format(message, parameters));
    }

    public SimpleStubException(String message, Throwable cause, Object... parameters) {
        super(String.format(message, parameters), cause);
    }
}
