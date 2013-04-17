package org.glassfish.simplestub.classes;

import java.io.Serializable;

abstract public class GenericClass1 {

    abstract <T> T generic1(int x);
    abstract <T> void generic2(T t, int x);
    abstract <T extends Number> void generic3(String x, T t, int i);
    abstract <T,K> void generic4(Object o, T t, K k);
    abstract <T extends Serializable & Cloneable> void generic5(T t);
}
