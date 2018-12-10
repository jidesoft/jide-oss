package com.jidesoft.converter;

import junit.framework.TestCase;

public class ObjectConverterManagerTest extends TestCase {
    public void testInit() throws InterruptedException {
        assertTrue(ObjectConverterManager.getConverter(Integer.class).getClass().getName().indexOf("IntegerConverter") != -1);
        ObjectConverterManager.unregisterAllConverters();
        ObjectConverterManager.resetInit();
        ObjectConverterManager.registerConverter(Integer.class, new DefaultObjectConverter());
        assertTrue(ObjectConverterManager.getConverter(Integer.class).getClass().getName().indexOf("DefaultObjectConverter") != -1);
    }
}
