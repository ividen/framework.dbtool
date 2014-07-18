package ru.kwanza.dbtool.core;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

/**
 * @author Guzanov Alexander
 */
public class TestUpdateException extends TestCase {

    public void testUpdateException() {
        List<TestEntity> list = Arrays.asList(new TestEntity(1, "Value 1"), new TestEntity(2, "Value 2"), new TestEntity(3, "Value 3"));
        List<TestEntity> list2 = Arrays.asList(new TestEntity(4, "Value 4"), new TestEntity(5, "Value 5"));
        List<TestEntity> list3 = Arrays.asList(new TestEntity(6, "Value 6"));

        try {
            throw new UpdateException("Error!",null,null,null);
        } catch (UpdateException e) {
            assertEquals(e.getMessage(),"Error!");
            assertEquals(e.getConstrainted().size(),0);
            assertEquals(e.getOptimistic().size(),0);
            assertEquals(e.getUpdated().size(),0);
        }

        try {
            throw new UpdateException("Error!",list,list2,list3);
        } catch (UpdateException e) {
            assertEquals(e.getMessage(),"Error!");
            assertEquals(e.getConstrainted(),list);
            assertEquals(e.getOptimistic(),list2);
            assertEquals(e.getUpdated(),list3);
        }
    }
}
