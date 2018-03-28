package com.meterware.simplestub.generation;
/*
 * Copyright (c) 2015-2016 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
/**
 * An interface which returns various objects for SimpleStub to stub or instantiate.
 *
 * @author Russell Gold
 */
public interface ClassWithObjectGetters {

    Object getObject();

    AnInterface getAnInterface();

    ABaseClass getABaseClass();

    AConcreteClass getAConcreteClass();

    AClassWithNoDefaultConstructor getAClassWithNoDefaultConstructor();

    AnInterface[] getAnInterfaceArray();

    AnInterface[][] getATwoDArray();

}

