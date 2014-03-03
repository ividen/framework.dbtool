package ru.kwanza.dbtool.orm.impl.lockoperation;

import junit.framework.Assert;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.SortedDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.api.LockResult;
import ru.kwanza.dbtool.orm.api.LockType;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingRegistry;
import ru.kwanza.txn.api.spi.ITransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Alexander Guzanov
 */


public class TestLockOperation extends AbstractJUnit4SpringContextTests {
    @Resource(name = "dbtool.IEntityManager")
    protected IEntityManager em;
    @Resource(name = "txn.ITransactionManager")
    protected ITransactionManager tm;
    @Resource(name = "dbtool.DBTool")
    protected DBTool dbTool;
    @Resource(name = "dbtool.IEntityMappingRegistry")
    protected EntityMappingRegistry registry;
    @Resource(name = "dataSource")
    protected DataSource dataSource;

    @Value("${jdbc.schema}")
    private String schema;


    @Before
    public void setUpDV() throws Exception {
        IDatabaseConnection connection = getConnection();
        DatabaseOperation.CLEAN_INSERT.execute(connection, getInitDataSet());
        connection.getConnection().commit();
    }

    private IDataSet getInitDataSet() throws IOException,
            DataSetException {

        return new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream("initdb.xml"));
    }

    public IDatabaseConnection getConnection() throws SQLException, DatabaseUnitException {
        DatabaseConnection connection = new DatabaseConnection(dataSource.getConnection(), schema);
        connection.getConfig().setProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);
        return connection;
    }

    @Before
    public void init() {
        registry.registerEntityClass(LockedEntity.class);
    }

    public IDataSet getActualDataSet() throws Exception {
        return new SortedDataSet(getConnection().createDataSet(new String[]{"locked_entity"}));
    }


    private final class LockingThread extends Thread {
        private Collection<Long> ids;
        private LockType type;
        private LockResult<LockedEntity> result;
        private volatile boolean notified = false;
        private volatile boolean finished = false;
        private volatile boolean worked = false;
        private ReentrantLock lock = new ReentrantLock();
        private ReentrantLock workedLock = new ReentrantLock();
        private Condition notifyCondition = lock.newCondition();
        private Condition workedCondition = workedLock.newCondition();
        private Condition finishedCondition = lock.newCondition();

        private LockingThread(LockType type, Collection<Long> ids) {
            this.ids = ids;
            this.type = type;
        }

        @Override
        public void run() {
            lock.lock();

            try {
                if (waitForNotify()) return;
                tm.begin();

                Collection<LockedEntity> entities = em.readByKeys(LockedEntity.class, ids);
                result = em.lock(type, LockedEntity.class, entities);


            } finally {
                lock.unlock();
            }

            workedLock.lock();
            try {
                worked = true;
                workedCondition.signal();

            } finally {
                workedLock.unlock();
            }

            lock.lock();
            try {
                if (waitForFinish()) return;
            } finally {
                tm.commit();
                lock.unlock();
            }
        }

        private boolean waitForFinish() {
            while (!finished) {
                try {
                    finishedCondition.await();
                } catch (InterruptedException e) {
                    return true;
                }
            }
            return false;
        }

        private boolean waitForNotify() {
            while (!notified) {
                try {
                    notifyCondition.await();
                } catch (InterruptedException e) {
                    return true;
                }
            }
            return false;
        }

        public boolean doWork(long millis) {
            lock.lock();
            try {
                notified = true;
                notifyCondition.signal();
            } finally {
                lock.unlock();
            }

            workedLock.lock();
            try {

                if (!worked) {
                    workedCondition.await(10000, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e) {
                return worked;
            } finally {
                workedLock.unlock();
            }

            return worked;
        }


        public void finish() {
            lock.lock();
            try {
                finished = true;
                finishedCondition.signal();
            } finally {
                lock.unlock();
            }

            while (isAlive()) {
                try {
                    join(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    /**
     * LockType.PESSIMISTIC_WAIT
     * <p/>
     * Взаимная блокировка двумя разными потоками по полностью совпадающему набору элементов
     */
    @Test
    public void testWaiteLock_1() throws InterruptedException {
        List<Long> ids = Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l);
        LockingThread first = new LockingThread(LockType.PESSIMISTIC_WAIT, ids);
        LockingThread second = new LockingThread(LockType.PESSIMISTIC_WAIT, ids);

        first.start();
        second.start();

        first.doWork(10000);
        Assert.assertNotNull("Result must be no null in first thread", first.result);
        Assert.assertEquals("Expected count of locked entities in first thread", 10, first.result.getLocked().size());
        Assert.assertEquals("Expected count of unlocked entities in first thread", 0, first.result.getUnlocked().size());
        second.doWork(10000);
        Assert.assertNull("Result must be null in second thread", second.result);
        first.finish();
        Thread.currentThread().sleep(1000);
        Assert.assertNotNull("Result must be no null in second thread", second.result);
        Assert.assertEquals("Expected count of locked entities in second thread", 10, second.result.getLocked().size());
        Assert.assertEquals("Expected count of unlocked entities in second thread", 0, second.result.getUnlocked().size());
        second.finish();
    }

    /**
     * LockType.PESSIMISTIC_WAIT
     * Взаимная блокировка двумя разными потоками с одним общим элементом
     */
    @Test
    public void testWaiteLock_2() throws InterruptedException {
        List<Long> ids1 = Arrays.asList(1l, 2l, 3l, 4l, 5l);
        List<Long> ids2 = Arrays.asList(5l, 6l, 7l, 8l, 9l);
        LockingThread first = new LockingThread(LockType.PESSIMISTIC_WAIT, ids1);
        LockingThread second = new LockingThread(LockType.PESSIMISTIC_WAIT, ids2);

        first.start();
        second.start();

        first.doWork(10000);
        Assert.assertNotNull("Result must be no null in first thread", first.result);
        Assert.assertEquals("Expected count of locked entities in first thread", 5, first.result.getLocked().size());
        Assert.assertEquals("Expected count of unlocked entities in first thread", 0, first.result.getUnlocked().size());
        second.doWork(10000);
        Assert.assertNull("Result must be null in second thread", second.result);
        first.finish();
        Thread.currentThread().sleep(1000);
        Assert.assertNotNull("Result must be no null in second thread", second.result);
        Assert.assertEquals("Expected count of locked entities in second thread", 5, second.result.getLocked().size());
        Assert.assertEquals("Expected count of unlocked entities in second thread", 0, second.result.getUnlocked().size());
        second.finish();

    }

    /**
     * LockType.PESSIMISTIC_WAIT
     * Отсутствие блокировки из-за отсутствия пересечения между локами
     */
    @Test
    public void testWaiteLock_3() {
        List<Long> ids1 = Arrays.asList(1l, 2l, 3l, 4l, 5l);
        List<Long> ids2 = Arrays.asList(6l, 7l, 8l, 9l, 10l);
        LockingThread first = new LockingThread(LockType.PESSIMISTIC_WAIT, ids1);
        LockingThread second = new LockingThread(LockType.PESSIMISTIC_WAIT, ids2);

        first.start();
        second.start();

        first.doWork(10000);
        Assert.assertNotNull("Result must be no null in first thread", first.result);
        Assert.assertEquals("Expected count of locked entities in first thread", 5, first.result.getLocked().size());
        Assert.assertEquals("Expected count of unlocked entities in first thread", 0, first.result.getUnlocked().size());
        second.doWork(10000);
        Assert.assertNotNull("Result must be no null in second thread", second.result);
        Assert.assertEquals("Expected count of locked entities in second thread", 5, second.result.getLocked().size());
        Assert.assertEquals("Expected count of unlocked entities in second thread", 0, second.result.getUnlocked().size());
        second.finish();
        first.finish();


    }

    /**
     * LockType.PESSIMISTIC_NOWAIT
     * Отказ в блокировки, если набор блокировки полностью общий
     */
    @Test
    public void testNoWaiteLock_1() throws InterruptedException {
        List<Long> ids = Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l);
        LockingThread first = new LockingThread(LockType.PESSIMISTIC_NOWAIT, ids);
        LockingThread second = new LockingThread(LockType.PESSIMISTIC_NOWAIT, ids);

        first.start();
        second.start();

        first.doWork(10000);
        Assert.assertNotNull("Result must be no null in first thread", first.result);
        Assert.assertEquals("Expected count of locked entities in first thread", 10, first.result.getLocked().size());
        Assert.assertEquals("Expected count of unlocked entities in first thread", 0, first.result.getUnlocked().size());
        second.doWork(10000);

        Assert.assertNotNull("Result must be no null in second thread", second.result);
        Assert.assertEquals("Expected count of locked entities in second thread", 0, second.result.getLocked().size());
        Assert.assertEquals("Expected count of unlocked entities in second thread", 10, second.result.getUnlocked().size());
        second.finish();
        first.finish();
    }

    /**
     * Отказ в блокировки, если в наборах есть одинаковый элемент
     */
    @Test
    public void testNoWaiteLock_2() {
        List<Long> ids1 = Arrays.asList(1l, 2l, 3l, 4l, 5l);
        List<Long> ids2 = Arrays.asList(5l, 6l, 7l, 8l, 9l);
        LockingThread first = new LockingThread(LockType.PESSIMISTIC_NOWAIT, ids1);
        LockingThread second = new LockingThread(LockType.PESSIMISTIC_NOWAIT, ids2);

        first.start();
        second.start();

        first.doWork(10000);
        Assert.assertNotNull("Result must be no null in first thread", first.result);
        Assert.assertEquals("Expected count of locked entities in first thread", 5, first.result.getLocked().size());
        Assert.assertEquals("Expected count of unlocked entities in first thread", 0, first.result.getUnlocked().size());
        second.doWork(10000);

        Assert.assertNotNull("Result must be no null in second thread", second.result);
        Assert.assertEquals("Expected count of locked entities in second thread", 0, second.result.getLocked().size());
        Assert.assertEquals("Expected count of unlocked entities in second thread", 5, second.result.getUnlocked().size());
        second.finish();
        first.finish();
    }

    /**
     * Получения блокировок двумя потоками по разным наборам элементов
     */
    @Test
    public void testNoWaiteLock_3() {
        List<Long> ids1 = Arrays.asList(1l, 2l, 3l, 4l, 5l);
        List<Long> ids2 = Arrays.asList(6l, 7l, 8l, 9l, 10l);
        LockingThread first = new LockingThread(LockType.PESSIMISTIC_NOWAIT, ids1);
        LockingThread second = new LockingThread(LockType.PESSIMISTIC_NOWAIT, ids2);

        first.start();
        second.start();

        first.doWork(10000);
        Assert.assertNotNull("Result must be no null in first thread", first.result);
        Assert.assertEquals("Expected count of locked entities in first thread", 5, first.result.getLocked().size());
        Assert.assertEquals("Expected count of unlocked entities in first thread", 0, first.result.getUnlocked().size());
        second.doWork(10000);
        Assert.assertNotNull("Result must be no null in second thread", second.result);
        Assert.assertEquals("Expected count of locked entities in second thread", 5, second.result.getLocked().size());
        Assert.assertEquals("Expected count of unlocked entities in second thread", 0, second.result.getUnlocked().size());
        second.finish();
        first.finish();
    }


    /**
     * Отказ в блокировки, если набор блокировки полностью общий
     */
    @Test
    public void testSkipLock_1() {
        List<Long> ids = Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l);
        LockingThread first = new LockingThread(LockType.PESSIMISTIC_SKIP_LOCKED, ids);
        LockingThread second = new LockingThread(LockType.PESSIMISTIC_SKIP_LOCKED, ids);

        first.start();
        second.start();

        first.doWork(10000);
        Assert.assertNotNull("Result must be no null in first thread", first.result);
        Assert.assertEquals("Expected count of locked entities in first thread", 10, first.result.getLocked().size());
        Assert.assertEquals("Expected count of unlocked entities in first thread", 0, first.result.getUnlocked().size());
        second.doWork(10000);

        Assert.assertNotNull("Result must be no null in second thread", second.result);
        Assert.assertEquals("Expected count of locked entities in second thread", 0, second.result.getLocked().size());
        Assert.assertEquals("Expected count of unlocked entities in second thread", 10, second.result.getUnlocked().size());
        second.finish();
        first.finish();
    }

    /**
     * Отказ в блокировки одного элемента, если он общий для двух наборов
     */
    @Test
    public void testSkipLock_2() {
        List<Long> ids1 = Arrays.asList(1l, 2l, 3l, 4l, 5l);
        List<Long> ids2 = Arrays.asList(5l, 6l, 7l, 8l, 9l);;
        LockingThread first = new LockingThread(LockType.PESSIMISTIC_SKIP_LOCKED, ids1);
        LockingThread second = new LockingThread(LockType.PESSIMISTIC_SKIP_LOCKED, ids2);

        first.start();
        second.start();

        first.doWork(10000);
        Assert.assertNotNull("Result must be no null in first thread", first.result);
        Assert.assertEquals("Expected count of locked entities in first thread", 5, first.result.getLocked().size());
        Assert.assertEquals("Expected count of unlocked entities in first thread", 0, first.result.getUnlocked().size());
        second.doWork(10000);
        Assert.assertNotNull("Result must be no null in second thread", second.result);
        Assert.assertEquals("Expected count of locked entities in second thread", 4, second.result.getLocked().size());
        Assert.assertEquals("Expected count of unlocked entities in second thread", 1, second.result.getUnlocked().size());
        second.finish();
        first.finish();
    }

    /**
     * Получение блокировок двумя потоками по разным наборам элементов
     */
    @Test
    public void testSkipLock_3() {
        List<Long> ids1 = Arrays.asList(1l, 2l, 3l, 4l, 5l);
        List<Long> ids2 = Arrays.asList(6l, 7l, 8l, 9l, 10l);
        LockingThread first = new LockingThread(LockType.PESSIMISTIC_SKIP_LOCKED, ids1);
        LockingThread second = new LockingThread(LockType.PESSIMISTIC_SKIP_LOCKED, ids2);

        first.start();
        second.start();

        first.doWork(10000);
        Assert.assertNotNull("Result must be no null in first thread", first.result);
        Assert.assertEquals("Expected count of locked entities in first thread", 5, first.result.getLocked().size());
        Assert.assertEquals("Expected count of unlocked entities in first thread", 0, first.result.getUnlocked().size());
        second.doWork(10000);
        Assert.assertNotNull("Result must be no null in second thread", second.result);
        Assert.assertEquals("Expected count of locked entities in second thread", 5, second.result.getLocked().size());
        Assert.assertEquals("Expected count of unlocked entities in second thread", 0, second.result.getUnlocked().size());
        second.finish();
        first.finish();
    }
}