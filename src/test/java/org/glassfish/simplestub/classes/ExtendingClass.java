package org.glassfish.simplestub.classes;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

abstract
public class ExtendingClass extends SimpleAbstractTestClass {

    private final BigInteger num;
    private final List list;

    ExtendingClass() {
        this( new ArrayList<Integer>() );
    }

    ExtendingClass( BigInteger num, List list ) {
        this.num = num;
        this.list = list;
    }

    ExtendingClass(int num, List list) {
        this(new BigInteger(Integer.toString(num)), list);
    }

    private ExtendingClass( List list ) {
        this( BigInteger.ZERO, list );
    }

    @Override
    public int doSomething(int value) {
        return value + num.intValue();
    }

    @Override
    public void method1() {
    }

    String doSomething2(int value1) {
        return null;
    }

    abstract public Boolean[] getSwitches();
}
