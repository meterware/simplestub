package com.meterware.simplestub.classes;
/*
 * Copyright (c) 2016 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */
import java.util.EventListener;

/**
 * A class which creates a private inner class during static initialization.
 *
 * @author Russell Gold
 */
public class ClassWithPrivateNestedClass {

    private class ListenerImpl implements EventListener {}

    private ListenerImpl listener = new ListenerImpl();

    public EventListener getListener() {
        return listener;
    }
}
