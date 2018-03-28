package com.meterware.simplestub.classes;
/*
 * Copyright (c) 2016 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

/**
 * A class which references a class in the same package
 *
 * @author Russell Gold
 */
public class ClassWithPackagePrivateReference {

    static PackagedClass aClass = new PackagedClass();
}
