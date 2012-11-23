package ru.kwanza.dbtool.orm.impl.operation;

import org.dbunit.Assertion;
import org.springframework.transaction.TransactionStatus;
import ru.kwanza.dbtool.core.UpdateException;
import ru.kwanza.dbtool.orm.api.OrderBy;

import java.util.Arrays;
import java.util.List;

/**
 * @author Kiryl Karatsetski
 */
public abstract class UpdateOperationTest extends AbstractOperationTest {

    public void testSuccessUpdate() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            final List<TestEntity> testEntities = getEntityManager().queryBuilder(TestEntity.class).create().selectList();
            for (TestEntity testEntity : testEntities) {
                testEntity.incrementVersion();
            }
            try {
                getEntityManager().update(TestEntity.class, testEntities);
            } catch (UpdateException e) {
                fail("Must never throw!");
                e.printStackTrace();
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getResourceSet("./data/testSuccessUpdate.xml"), getActualDataSet());
    }

    public void testUpdateConstrained_1() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            final List<TestEntity> testEntities =
                    getEntityManager().queryBuilder(TestEntity.class).orderBy(OrderBy.ASC("key")).create().selectList();
            for (TestEntity testEntity : testEntities) {
                testEntity.incrementVersion();
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(0).setName(new String(buff));

            try {
                getEntityManager().update(TestEntity.class, testEntities);
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 0, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_1.xml"));
    }

    public void testUpdateConstrained_2() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            final List<TestEntity> testEntities =
                    getEntityManager().queryBuilder(TestEntity.class).orderBy(OrderBy.ASC("key")).create().selectList();
            for (TestEntity e : testEntities) {
                e.incrementVersion();
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(testEntities.size() - 1).setName(new String(buff));

            try {
                getEntityManager().update(TestEntity.class, testEntities);
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 10, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_2.xml"));
    }

    public void testUpdateConstrained_3() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            final List<TestEntity> testEntities =
                    getEntityManager().queryBuilder(TestEntity.class).orderBy(OrderBy.ASC("key")).create().selectList();
            for (TestEntity e : testEntities) {
                e.incrementVersion();
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(testEntities.size() - 1).setName(new String(buff));
            testEntities.get(0).setName(new String(buff));

            try {
                getEntityManager().update(TestEntity.class, testEntities);
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 2, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 0, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
                assertEquals("Wrong key ", 10, e.<TestEntity>getConstrainted().get(1).getKey().longValue());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_3.xml"));
    }

    public void testUpdateConstrained_4() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            final List<TestEntity> testEntities =
                    getEntityManager().queryBuilder(TestEntity.class).orderBy(OrderBy.ASC("key")).create().selectList();
            for (TestEntity e : testEntities) {
                e.incrementVersion();
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(5).setName(new String(buff));

            try {
                getEntityManager().update(TestEntity.class, testEntities);
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 5, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_4.xml"));
    }

    public void testUpdateConstrained_5() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            final List<TestEntity> testEntities =
                    getEntityManager().queryBuilder(TestEntity.class).orderBy(OrderBy.ASC("key")).create().selectList();
            for (TestEntity e : testEntities) {
                e.incrementVersion();
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(testEntities.size() - 1).setName(new String(buff));
            testEntities.get(0).setName(new String(buff));
            testEntities.get(5).setName(new String(buff));

            try {
                getEntityManager().update(TestEntity.class, testEntities);
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 3, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 0, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
                assertEquals("Wrong key ", 5, e.<TestEntity>getConstrainted().get(1).getKey().longValue());
                assertEquals("Wrong key ", 10, e.<TestEntity>getConstrainted().get(2).getKey().longValue());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_5.xml"));
    }

    public void testUpdateConstrained_6() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            final List<TestEntity> testEntities =
                    getEntityManager().queryBuilder(TestEntity.class).orderBy(OrderBy.ASC("key")).create().selectList();
            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            for (TestEntity e : testEntities) {
                e.incrementVersion();
                e.setName(new String(buff));
            }

            try {
                getEntityManager().update(TestEntity.class, testEntities);
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                List<TestEntity> list = e.<TestEntity>getConstrainted();
                assertEquals("Wrong size ", 11, list.size());
                for (int i = 0; i < list.size(); i++) {
                    assertEquals(i, list.get(i).getKey().longValue());
                }
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_6.xml"));
    }
}