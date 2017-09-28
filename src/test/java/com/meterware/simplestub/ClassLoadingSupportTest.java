package com.meterware.simplestub;

import com.meterware.simplestub.classes.*;
import org.junit.After;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Tests the class reloading functionality.
 */
public class ClassLoadingSupportTest {
    private List<Memento> mementos = new ArrayList<>();

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
    public void whenReloadClassCalled_classRunsStaticInitialization() throws Exception {
        mementos.add(SystemPropertySupport.install("test.property", "zork"));
        PropertyReader secondReader = (PropertyReader) ClassLoadingSupport.reloadClass(PropertyReaderImpl.class).getDeclaredConstructor().newInstance();
        assertThat(secondReader.getPropertyValue("test.property"), is("zork"));
    }

    @Test
    public void whenClassHasPrivateNestedClass_reloadedClassGetsOwnVersion() throws Exception {
        Object original = getListenerFromInstance(ClassWithPrivateNestedClass.class);
        Object reloadedValue = getListenerFromInstance(ClassLoadingSupport.reloadClass(ClassWithPrivateNestedClass.class));

        assertThat(original.getClass().getName(), equalTo(reloadedValue.getClass().getName()));
        assertThat(original, not(sameInstance(reloadedValue)));
    }

    @SuppressWarnings("unchecked")
    private Object getListenerFromInstance(Class aClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Method getListenerMethod = aClass.getMethod("getListener");
        return getListenerMethod.invoke(aClass.getDeclaredConstructor().newInstance());
    }

    @Test
    public void whenClassHasPackageReference_reloadIt() throws Exception {
        Class<?> aClass = ClassLoadingSupport.reloadClass(ClassWithPackagePrivateReference.class);
        aClass.getDeclaredConstructor().newInstance();
    }

    @Test
    public void whenClassIsEnum_realoadingWorks() throws Exception {
        Class aClass = ClassLoadingSupport.reloadClass(AnEnum.class);
        assertThat(aClass.getEnumConstants(), arrayWithSize(3));
    }
}
