package com.meterware.simplestub.generation;
/*
 * Copyright (c) 2016 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import java.io.IOException;
import java.util.Collection;

/**
 * An interface for a class which can find class references.
 *
 * @author Russell Gold
 */
public interface ClassReferenceFinder {

    /**
     * Returns collection of all classes which are referenced by the specified class.
     * @param aClass the class whose internal references are to be found
     * @return a collection of classes
     * @throws IOException if there is an error parsing the class
     */
    Collection<Class> getClassesReferencedBy(Class aClass) throws IOException;
}
