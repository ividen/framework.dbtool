package ru.kwanza.dbtool.core.lock;

import junit.framework.Assert;
import org.dbunit.IDatabaseTester;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.kwanza.dbtool.core.DBTool;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Ivan Baluk
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractTestLock extends AbstractTransactionalJUnit4SpringContextTests{

    @Resource(name = "dbtool.DBTool")
    protected DBTool dbTool;
    @Resource(name = "dbTester")
    protected IDatabaseTester dbTester;
    @Resource(name = "transactionManager")
    protected PlatformTransactionManager tm;

    private final class LockingThread extends Thread {
        private String lockName;
        private boolean reentrant;
        private volatile boolean notified = false;
        private volatile boolean finished = false;
        private volatile boolean worked = false;
        private volatile boolean wasLocked = false;
        private ReentrantLock lock = new ReentrantLock();
        private ReentrantLock workedLock = new ReentrantLock();
        private Condition notifyCondition = lock.newCondition();
        private Condition workedCondition = workedLock.newCondition();
        private Condition finishedCondition = lock.newCondition();

        private LockingThread(String lockName, boolean reentrant) {
            this.lockName = lockName;
            this.reentrant = reentrant;
        }

        @Override
        public void run() {
            lock.lock();
            TransactionStatus transaction;
            AppLock dbToolLock;
            try {
                if (waitForNotify()) return;

                transaction = tm.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));

                dbToolLock = dbTool.getLock(lockName, reentrant);

            } finally {
                lock.unlock();
            }


            lock.lock();

            workedLock.lock();
            try {
                worked = true;
                workedCondition.signalAll();

            } finally {
                workedLock.unlock();
            }


            wasLocked = true;
            try {
                if (waitForFinish()) return;
            } finally {
                dbToolLock.close();
                tm.commit(transaction);
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
                notifyCondition.signalAll();
            } finally {
                lock.unlock();
            }

            workedLock.lock();
            try {

                if (!worked) {
                    workedCondition.await(millis, TimeUnit.MILLISECONDS);
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
                finishedCondition.signalAll();
            } finally {
                lock.unlock();
            }

            while (isAlive()) {
                try {
                    join(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }


    @Test
    public void testLock_1() throws Exception {
        AppLock lock1 = dbTool.getLock("lock1");
        AppLock lock2 = dbTool.getLock("lock2");

        lock1.lock();
        try {
            lock2.lock();
            lock2.close();
        } finally {
            lock1.close();
        }
    }


    @Test
    public void testLock_2() throws Exception {
        AppLock lock1 = dbTool.getLock("lock1");
        for (int i = 0; i < 100; i++) {
            lock1.lock();
        }

        for (int i = 0; i < 100; i++) {
            lock1.unlock();
        }
    }

    private AtomicBoolean isMainLocked = new AtomicBoolean(false);
    private AtomicBoolean isSecondLocked = new AtomicBoolean(false);

    @Test
    public void testWaiteLock_1() throws InterruptedException {
        LockingThread first = new LockingThread("lock1", true);
        LockingThread second = new LockingThread("lock1", true);

        first.start();
        second.start();

        first.doWork(100);
        Assert.assertEquals(first.worked, true);
        Assert.assertEquals(first.finished, false);
        Assert.assertEquals(second.worked, false);
        Assert.assertEquals(second.finished, false);
        first.finish();
        first.doWork(100);
        Assert.assertEquals(first.worked, true);
        Assert.assertEquals(first.finished, true);
        Assert.assertEquals(second.worked, true);
        Assert.assertEquals(second.finished, false);
        Thread.currentThread().sleep(100);
        second.finish();
        Assert.assertEquals(first.worked, true);
        Assert.assertEquals(first.finished, true);
        Assert.assertEquals(second.worked, true);
        Assert.assertEquals(second.finished, true);
    }


}
