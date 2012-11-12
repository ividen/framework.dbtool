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

        try {
            UpdateException updateException = new UpdateException();
            updateException.setConstrainted(list);
            updateException.setOptimistic(list2);

            throw updateException;
        } catch (UpdateException e) {
            assertEquals("Wrong constrained ", list, e.<TestEntity>getConstrainted());
            assertEquals("Wrong optimistic ", list2, e.<TestEntity>getOptimistic());
            assertNull(e.getMessage());
            assertNull(e.getCause());
        }

        try {
            UpdateException updateException = new UpdateException("Test Message");
            updateException.setConstrainted(list2);
            updateException.setOptimistic(list);

            throw updateException;
        } catch (UpdateException e) {
            assertEquals("Wrong constrained ", list2, e.<TestEntity>getConstrainted());
            assertEquals("Wrong optimistic ", list, e.<TestEntity>getOptimistic());
            assertEquals("Test Message", e.getMessage());
            assertNull(e.getCause());
        }

        try {
            UpdateException updateException = new UpdateException("Test Message", new RuntimeException());

            throw updateException;
        } catch (UpdateException e) {
            assertNull("Wrong constrained ", e.<TestEntity>getConstrainted());
            assertNull("Wrong optimistic ", e.<TestEntity>getOptimistic());
            assertEquals("Test Message", e.getMessage());
            assertEquals(RuntimeException.class, e.getCause().getClass());
        }

        try {
            UpdateException updateException = new UpdateException(new RuntimeException());

            throw updateException;
        } catch (UpdateException e) {
            assertNull("Wrong constrained ", e.<TestEntity>getConstrainted());
            assertNull("Wrong optimistic ", e.<TestEntity>getOptimistic());
            assertEquals(RuntimeException.class.getName(), e.getMessage());
            assertEquals(RuntimeException.class, e.getCause().getClass());
        }
    }
}
