package com.meterware.simplestub.classes;
/*
 * Copyright (c) 2015 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import java.util.Properties;

/**
 * A class which loads system properties when initialized.
 *
 * @author Russell Gold
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
