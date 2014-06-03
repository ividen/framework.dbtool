package ru.kwanza.dbtool.orm.impl.operation;

import org.dbunit.Assertion;
import org.junit.Test;
import ru.kwanza.dbtool.core.UpdateException;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * @author Kiryl Karatsetski
 */
public abstract class UpdateOperationTest extends AbstractOperationTest {

    @Test
    public void testSuccessUpdate() throws Throwable {
        final List<TestEntity> testEntities = em.queryBuilder(TestEntity.class).create().prepare().selectList();
        for (TestEntity testEntity : testEntities) {
            testEntity.incrementVersion();
        }
        try {
            em.update(TestEntity.class, testEntities);
        } catch (UpdateException e) {
            fail("Must never throw!");
            e.printStackTrace();
        }

        Assertion.assertEquals(getResourceSet("./data/testSuccessUpdate.xml"), getActualDataSet());
    }

    @Test
    public void testUpdateConstrained_1() throws Throwable {
        final List<TestEntity> testEntities =
                em.queryBuilder(TestEntity.class).orderBy("key").create().prepare().selectList();
        for (TestEntity testEntity : testEntities) {
            testEntity.incrementVersion();
        }

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(0).setName(new String(buff));

        try {
            em.update(TestEntity.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 0, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_1.xml"));
    }

    @Test
    public void testUpdateConstrained_2() throws Throwable {
        final List<TestEntity> testEntities =
                em.queryBuilder(TestEntity.class).orderBy("key").create().prepare().selectList();
        for (TestEntity e : testEntities) {
            e.incrementVersion();
        }

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(testEntities.size() - 1).setName(new String(buff));

        try {
            em.update(TestEntity.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 10, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_2.xml"));
    }

    @Test
    public void testUpdateConstrained_3() throws Throwable {
        final List<TestEntity> testEntities =
                em.queryBuilder(TestEntity.class).orderBy("key").create().prepare().selectList();
        for (TestEntity e : testEntities) {
            e.incrementVersion();
        }

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(testEntities.size() - 1).setName(new String(buff));
        testEntities.get(0).setName(new String(buff));

        try {
            em.update(TestEntity.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 2, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 0, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
            assertEquals("Wrong key ", 10, e.<TestEntity>getConstrainted().get(1).getKey().longValue());
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_3.xml"));
    }

    @Test
    public void testUpdateConstrained_4() throws Throwable {
        final List<TestEntity> testEntities =
                em.queryBuilder(TestEntity.class).orderBy("key").create().prepare().selectList();
        for (TestEntity e : testEntities) {
            e.incrementVersion();
        }

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(5).setName(new String(buff));

        try {
            em.update(TestEntity.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 5, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_4.xml"));
    }

    @Test
    public void testUpdateConstrained_5() throws Throwable {
        final List<TestEntity> testEntities =
                em.queryBuilder(TestEntity.class).orderBy("key").create().prepare().selectList();
        for (TestEntity e : testEntities) {
            e.incrementVersion();
        }

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(testEntities.size() - 1).setName(new String(buff));
        testEntities.get(0).setName(new String(buff));
        testEntities.get(5).setName(new String(buff));

        try {
            em.update(TestEntity.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 3, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 0, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
            assertEquals("Wrong key ", 5, e.<TestEntity>getConstrainted().get(1).getKey().longValue());
            assertEquals("Wrong key ", 10, e.<TestEntity>getConstrainted().get(2).getKey().longValue());
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_5.xml"));
    }

    @Test
    public void testUpdateConstrained_6() throws Throwable {
        final List<TestEntity> testEntities =
                em.queryBuilder(TestEntity.class).orderBy("key").create().prepare().selectList();
        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        for (TestEntity e : testEntities) {
            e.incrementVersion();
            e.setName(new String(buff));
        }

        try {
            em.update(TestEntity.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            List<TestEntity> list = e.<TestEntity>getConstrainted();
            assertEquals("Wrong size ", 11, list.size());
            for (int i = 0; i < list.size(); i++) {
                assertEquals(i, list.get(i).getKey().longValue());
            }
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_6.xml"));
    }
}
