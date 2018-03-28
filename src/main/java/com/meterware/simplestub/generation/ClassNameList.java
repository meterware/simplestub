package com.meterware.simplestub.generation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An iterable list of internal class names, created from a type description.
 */
public class ClassNameList implements Iterable<String> {
    private List<String> classNames = new ArrayList<>();

    public ClassNameList(String spec) {
        parseTypeSpec(spec);
    }

    private void parseTypeSpec(String spec) {
        int i = 0;
        while (i < spec.length()) {
            int start = spec.indexOf("L", i);
            if (start < 0) break;
            int end = spec.indexOf(";", start);
            classNames.add(spec.substring(start+1, end));
            i = end + 1;
        }
    }

    @Override
    public Iterator<String> iterator() {
        return classNames.iterator();
    }
}
