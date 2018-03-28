package com.meterware.simplestub.classes;
/*
 * Copyright (c) 2015 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

/**
 * An interface for a class which does its static initialization from system properties.
 *
 * @author Russell Gold
 */
public interface PropertyReader {

    /**
     * Returns the value of the specified property when the class was initialized.
     * @param propertyName the name of the specified property
     * @return the property value
     */
    String getPropertyValue(String propertyName);
}
