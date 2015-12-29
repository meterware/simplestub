package com.meterware.simplestub.classes;

import java.util.Properties;

/**
 * A class which loads system properties when initialized.
 */
public class PropertyReaderImpl implements PropertyReader {

    private static final Properties initialProperties;

    static {
        initialProperties = (Properties) System.getProperties().clone();
    }

    @Override
    public String getPropertyValue(String propertyName) {
        return initialProperties.getProperty(propertyName);
    }
}
