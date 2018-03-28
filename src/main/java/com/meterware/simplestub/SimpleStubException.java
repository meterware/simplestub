package com.meterware.simplestub;
/*
 * Copyright (c) 2014-2015 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

/**
 * An exception thrown when the simplestub library detects a problem in a stub definition.
 *
 * @author Russell Gold
 */
public class SimpleStubException extends RuntimeException {
    public SimpleStubException(String message) {
        super(message);
    }

    public SimpleStubException(String message, Throwable cause) {
        super(message, cause);
    }
}
