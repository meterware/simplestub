package com.meterware.simplestub;

import com.meterware.simplestub.classes.PropertyReader;
import com.meterware.simplestub.classes.PropertyReaderImpl;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests the class reloading functionality.
 */
public class ClassLoadingSupportTest {
    private List<Memento> mementos = new ArrayList<Memento>();

    @After
    public void tearDown() throws Exception {
        for (Memento memento : mementos)
            memento.revert();
    }

    @Test
    public void byDefaultStaticClassIgnoresPropertyChange() throws Exception {
        PropertyReader defaultReader = new PropertyReaderImpl();
        mementos.add(SystemPropertySupport.install("test.property", "zork"));
        assertThat(defaultReader.getPropertyValue("test.property"), nullValue());
    }

    @Test
    @SuppressWarnings("all")
    public void testClassReloading() throws Exception {
        mementos.add(SystemPropertySupport.install("test.property", "zork"));
        PropertyReader secondReader = (PropertyReader) ClassLoadingSupport.reloadClass(PropertyReaderImpl.class).newInstance();
        assertThat(secondReader.getPropertyValue("test.property"), is("zork"));
    }
}
