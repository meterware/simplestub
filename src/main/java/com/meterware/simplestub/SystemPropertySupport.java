package com.meterware.simplestub;
/*
 * Copyright (c) 2015 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
/**
 * A class which simplifies setting system properties in unit tests.
 *
 * @author Russell Gold
 */
@SuppressWarnings("WeakerAccess")
public class SystemPropertySupport {

    /**
     * Sets a property to the specified value, returning a memento to restore it.
     * @param propertyName the name of the property to set
     * @param newValue the new value for the property
     * @return an object which holds the information needed to revert the system property.
     */
    public static Memento install(String propertyName, String newValue) {
        PropertyMemento memento = new PropertyMemento(propertyName);
        System.setProperty(propertyName, newValue);
        return memento;
    }

    /**
     * Returns a memento to restore the specified property to its current value.
     * @param propertyName the name of the property to preserve
     * @return an object which holds the information needed to revert the system property.
     */
    @SuppressWarnings("SameParameterValue")
    public static Memento preserve(String propertyName) {
        return new PropertyMemento(propertyName);
    }

    private static class PropertyMemento implements Memento {
        private final String originalValue;
        private String propertyName;

        PropertyMemento(String propertyName) {
            this.propertyName = propertyName;
            this.originalValue = System.getProperty(propertyName);
        }

        @Override
        public void revert() {
            if (originalValue == null)
                System.clearProperty(propertyName);
            else
                System.setProperty(propertyName, originalValue);
        }

        @Override
        @SuppressWarnings("unchecked")
        public String getOriginalValue() {
            return originalValue;
        }
    }
}
