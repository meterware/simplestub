package com.meterware.simplestub.classes;

/**
 * A class which references a class in the same package
 */
public class ClassWithPackagePrivateReference {

    static PackagedClass aClass = new PackagedClass();
}
