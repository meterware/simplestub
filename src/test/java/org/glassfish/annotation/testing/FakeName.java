package org.glassfish.annotation.testing;

import javax.lang.model.element.Name;

public class FakeName implements Name {

    private CharSequence value;

    FakeName(CharSequence value) {
        this.value = value;
    }

    @Override
    public boolean contentEquals(CharSequence charSequence) {
        return value.equals(charSequence);
    }

    @Override
    public int length() {
        return value.length();
    }

    @Override
    public char charAt(int i) {
        return value.charAt(i);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return value.subSequence(start, end);
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
