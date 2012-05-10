package org.glassfish.simplestub.classes;

import java.math.BigInteger;
import java.net.CookiePolicy;
import java.util.List;

abstract
public class SimpleAbstractTestClass implements Interface1 {

    public void doNothing() {}

    protected abstract int doSomething(int value);

    abstract String doSomething2(int value1, List value2);

    abstract boolean doSomething3(List<BigInteger> list);

    abstract CookiePolicy getPolicy();
}
