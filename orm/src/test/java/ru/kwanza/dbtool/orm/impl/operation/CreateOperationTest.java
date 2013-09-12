package ru.kwanza.dbtool.orm.impl.operation;

import org.dbunit.Assertion;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.TransactionStatus;
import ru.kwanza.dbtool.core.UpdateException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kiryl Karatsetski
 */
public abstract class CreateOperationTest extends AbstractOperationTest {

    public void testInsertSuccess() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>(10);
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11L + i, "Test_" + (11L + i), 0));
            }

            try {
                getEntityManager().create(TestEntity.class, testEntities);
            } catch (UpdateException e) {
                fail("Must never throw Exception!");
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertSuccess.xml"));
    }

    public void testInsertConstrained_1() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>();
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11L + i, "Test_" + (11 + i), 0));
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(0).setName(new String(buff));

            try {
                getEntityManager().create(TestEntity.class, testEntities);
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 11, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_1.xml"));
    }

    public void testInsertConstrained_2() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>();
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11L + i, "Test_" + (11 + i), 0));
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(testEntities.size() - 1).setName(new String(buff));

            try {
                getEntityManager().create(TestEntity.class, testEntities);
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 20, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_2.xml"));
    }

    public void testInsertConstrained_3() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>();
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11L + i, "Test_" + (11 + i), 0));
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(4).setName(new String(buff));

            try {
                getEntityManager().create(TestEntity.class, testEntities);
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 15, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_3.xml"));
    }

    public void testInsertConstrained_5() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>();
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11L + i, "Test_" + (11 + i), 0));
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(testEntities.size() - 1).setName(new String(buff));
            testEntities.get(0).setName(new String(buff));

            try {
                getEntityManager().create(TestEntity.class, testEntities);
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 2, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 11, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
                assertEquals("Wrong key ", 20, e.<TestEntity>getConstrainted().get(1).getKey().longValue());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_5.xml"));
    }

    public void testInsertConstrained_6() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
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
                getEntityManager().create(TestEntity.class, testEntities);
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 3, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 11, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
                assertEquals("Wrong key ", 15, e.<TestEntity>getConstrainted().get(1).getKey().longValue());
                assertEquals("Wrong key ", 20, e.<TestEntity>getConstrainted().get(2).getKey().longValue());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_6.xml"));
    }

    public void testInsertConstrained_ByKey() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
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
                getEntityManager().create(TestEntity.class, testEntities);
                fail("Must throw Exception!");
            } catch (DuplicateKeyException e) {
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }
    }

    public void testInsertConstrained_ByNullable() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>();
            for (int i = 0; i < 10; i++) {
                final TestEntity testEntity = new TestEntity(11L + i, "Test_" + (11 + i), 0);
                if (testEntity.getKey() == 11) {
                    testEntity.setVersion(null);
                }
                testEntities.add(testEntity);
            }

            try {
                getEntityManager().create(TestEntity.class, testEntities);
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 11, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_ByNullable.xml"));
    }

    public void testUpdateConstrained_ByNulableConstrained() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            final List<TestEntity> testEntities =
                    getEntityManager().queryBuilder(TestEntity.class).orderBy("key").create().prepare().selectList();
            for (TestEntity e : testEntities) {
                e.incrementVersion();
                if (e.getKey() == 0) {
                    e.setVersion(null);
                }
            }

            try {
                getEntityManager().update(TestEntity.class, testEntities);
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                List<TestEntity> list = e.<TestEntity>getConstrainted();
                assertEquals("Wrong size ", 1, list.size());
                assertEquals("Wrong size ", 0, list.get(0).getKey().longValue());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_ByNullableConstrained.xml"));
    }
}
