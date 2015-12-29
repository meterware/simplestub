package com.meterware.simplestub.classes;

/**
 * An interface for a class which does its static initialization from system properties.
 */
public interface PropertyReader {

    /**
     * Returns the value of the specified property when the class was initialized.
     * @param propertyName
     */
    String getPropertyValue(String propertyName);
}
