package com.meterware.simplestub;
/*
 * Copyright (c) 2014-2015 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
/**
 * Thrown by a strict stub if one of its undefined methods is called.
 *
 * @author Russell Gold
 */
public class UnexpectedMethodCallException extends SimpleStubException {
    public UnexpectedMethodCallException(String message) {
        super(message);
    }
}
