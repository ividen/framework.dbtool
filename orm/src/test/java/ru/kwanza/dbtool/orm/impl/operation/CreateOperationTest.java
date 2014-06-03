package ru.kwanza.dbtool.orm.impl.operation;

import org.dbunit.Assertion;
import org.junit.Test;
import org.springframework.dao.DuplicateKeyException;
import ru.kwanza.dbtool.core.UpdateException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * @author Kiryl Karatsetski
 */
public abstract class CreateOperationTest extends AbstractOperationTest {

    @Test
    public void testInsertSuccess() throws Throwable {
        List<TestEntity> testEntities = new ArrayList<TestEntity>(10);
        for (int i = 0; i < 10; i++) {
            testEntities.add(new TestEntity(11L + i, "Test_" + (11L + i), 0));
        }

        try {
            em.create(TestEntity.class, testEntities);
        } catch (UpdateException e) {
            fail("Must never throw Exception!");
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertSuccess.xml"));
    }

    @Test
    public void testInsertConstrained_1() throws Throwable {
        List<TestEntity> testEntities = new ArrayList<TestEntity>();
        for (int i = 0; i < 10; i++) {
            testEntities.add(new TestEntity(11L + i, "Test_" + (11 + i), 0));
        }

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(0).setName(new String(buff));

        try {
            em.create(TestEntity.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 11, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_1.xml"));
    }

    @Test
    public void testInsertConstrained_2() throws Throwable {
        List<TestEntity> testEntities = new ArrayList<TestEntity>();
        for (int i = 0; i < 10; i++) {
            testEntities.add(new TestEntity(11L + i, "Test_" + (11 + i), 0));
        }

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(testEntities.size() - 1).setName(new String(buff));

        try {
            em.create(TestEntity.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 20, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_2.xml"));
    }

    @Test
    public void testInsertConstrained_3() throws Throwable {
        List<TestEntity> testEntities = new ArrayList<TestEntity>();
        for (int i = 0; i < 10; i++) {
            testEntities.add(new TestEntity(11L + i, "Test_" + (11 + i), 0));
        }

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(4).setName(new String(buff));

        try {
            em.create(TestEntity.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 15, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_3.xml"));
    }

    @Test
    public void testInsertConstrained_5() throws Throwable {
        List<TestEntity> testEntities = new ArrayList<TestEntity>();
        for (int i = 0; i < 10; i++) {
            testEntities.add(new TestEntity(11L + i, "Test_" + (11 + i), 0));
        }

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(testEntities.size() - 1).setName(new String(buff));
        testEntities.get(0).setName(new String(buff));

        try {
            em.create(TestEntity.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 2, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 11, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
            assertEquals("Wrong key ", 20, e.<TestEntity>getConstrainted().get(1).getKey().longValue());
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_5.xml"));
    }

    @Test
    public void testInsertConstrained_6() throws Throwable {
        List<TestEntity> testEntities = new ArrayList<TestEntity>();
        for (int i = 0; i < 10; i++) {
            testEntities.add(new TestEntity(11L + i, "Test_" + (11 + i), 0));
        }

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(testEntities.size() - 1).setName(new String(buff));
        testEntities.get(4).setName(new String(buff));
        testEntities.get(0).setName(new String(buff));

        try {
            em.create(TestEntity.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 3, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 11, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
            assertEquals("Wrong key ", 15, e.<TestEntity>getConstrainted().get(1).getKey().longValue());
            assertEquals("Wrong key ", 20, e.<TestEntity>getConstrainted().get(2).getKey().longValue());
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_6.xml"));
    }

    @Test
    public void testInsertConstrained_ByKey() throws Throwable {
        List<TestEntity> testEntities = new ArrayList<TestEntity>();
        for (int i = 0; i < 10; i++) {
            testEntities.add(new TestEntity(21L + i, "Test_" + (11 + i), 0));
        }

        testEntities.add(new TestEntity(0L, "Test_" + (11), 0));
        testEntities.add(new TestEntity(8L, "Test_" + (11), 0));
        testEntities.add(new TestEntity(6L, "Test_" + (11), 0));

        for (int i = 0; i < 10; i++) {
            testEntities.add(new TestEntity(51L + i, "Test_" + (11 + i), 0));
        }

        try {
            em.create(TestEntity.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("No optimistic!",0,e.getOptimistic().size());
            assertEquals("Three constrained!",3,e.getConstrainted().size());
            assertEquals("Check constrained",0L,e.<TestEntity>getConstrainted().get(0).getKey().longValue());
            assertEquals("Check constrained",8L,e.<TestEntity>getConstrainted().get(1).getKey().longValue());
            assertEquals("Check constrained",6L,e.<TestEntity>getConstrainted().get(2).getKey().longValue());
        }
    }

    @Test
    public void testInsertConstrained_ByNullable() throws Throwable {
        List<TestEntity> testEntities = new ArrayList<TestEntity>();
        for (int i = 0; i < 10; i++) {
            final TestEntity testEntity = new TestEntity(11L + i, "Test_" + (11 + i), 0);
            if (testEntity.getKey() == 11) {
                testEntity.setVersion(null);
            }
            testEntities.add(testEntity);
        }

        try {
            em.create(TestEntity.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 11, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_ByNullable.xml"));
    }

    @Test
    public void testUpdateConstrained_ByNulableConstrained() throws Throwable {
        final List<TestEntity> testEntities =
                em.queryBuilder(TestEntity.class).orderBy("key").create().prepare().selectList();
        for (TestEntity e : testEntities) {
            e.incrementVersion();
            if (e.getKey() == 0) {
                e.setVersion(null);
            }
        }

        try {
            em.update(TestEntity.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            List<TestEntity> list = e.<TestEntity>getConstrainted();
            assertEquals("Wrong size ", 1, list.size());
            assertEquals("Wrong size ", 0, list.get(0).getKey().longValue());
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_ByNullableConstrained.xml"));
    }
}
