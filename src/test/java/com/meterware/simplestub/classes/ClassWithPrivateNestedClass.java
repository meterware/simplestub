package com.meterware.simplestub.classes;

import java.util.EventListener;

/**
 * A class which creates a private inner class during static initialization.
 */
public class ClassWithPrivateNestedClass {

    private class ListenerImpl implements EventListener {}

    private ListenerImpl listener = new ListenerImpl();

    public EventListener getListener() {
        return listener;
    }
}
