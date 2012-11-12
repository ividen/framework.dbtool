package ru.kwanza.dbtool.core;

import junit.framework.TestCase;

/**
 * @author Guzanov Alexander
 */
public class TestKeyValue extends TestCase {

    public void testKeyValue() {
        KeyValue<Integer, String> test = new KeyValue<Integer, String>(1, "Test");
        assertEquals("Wrong value", Integer.valueOf(1), test.getKey());
        assertEquals("Wrong value", "Test", test.getValue());

        test.setValue("New Test");
        assertEquals("Wrong value", Integer.valueOf(1), test.getKey());
        assertEquals("Wrong value", "New Test", test.getValue());
    }

}