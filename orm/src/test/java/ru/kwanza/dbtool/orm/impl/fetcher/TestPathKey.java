package ru.kwanza.dbtool.orm.impl.fetcher;

import junit.framework.Assert;
import org.junit.Test;

import java.io.Serializable;

/**
 * @author Alexander Guzanov
 */
public class TestPathKey implements Serializable {

    @Test
    public void testPathKey() {
        PathKey key1 = new PathKey(TestEntity.class, "entityA,entityB");

        PathKey key2 = new PathKey(TestEntity.class, "entityA,entityB");
        Assert.assertTrue(key1.getPath().equals(key2.getPath()));
        Assert.assertTrue(key1.getEntityClass() == key2.getEntityClass());
        PathKey key3 = new PathKey(TestEntity.class, "entityA,entityB,entityC");
        PathKey key4 = new PathKey(TestEntityA.class, "entityA,entityB");

        Assert.assertTrue(key1.equals(key2));
        Assert.assertTrue(key2.equals(key1));
        Assert.assertFalse(key1.equals(key3));
        Assert.assertFalse(key1.equals(key4));
        Assert.assertFalse(key3.equals(key4));
    }

}
