package com.meterware.simplestub.generation.asm;

import com.meterware.simplestub.classes.AbstractImplementation;
import com.meterware.simplestub.classes.ClassWithConstructorParameters;
import com.meterware.simplestub.classes.ConcreteClass;
import com.meterware.simplestub.classes.Interface1;

/**
 * A test interface that has getters for abstract objects.
 */
public interface ClassWithObjectGetters {

    Object getObject();

    Interface1 getInterface1();

    ClassWithConstructorParameters getClassWithConstructorParameters();

    ConcreteClass getConcreteClass();

    AbstractImplementation getAbstractImplementation();

    AnInterface[] getAnInterfaceArray();

    AnInterface[][] getATwoDArray();

}
