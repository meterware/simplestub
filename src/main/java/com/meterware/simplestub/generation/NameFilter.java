package com.meterware.simplestub.generation;

/**
 * A filter which converts the actual method name to a displayable form. This hides the generated class name.
 */
public interface NameFilter {

    /** Converts the actual full method name to one suitable for display in an error message. */
    String toDisplayName(String fullMethodName);
}
