package ru.kwanza.dbtool.core.lock;

/*
 * #%L
 * dbtool-core
 * %%
 * Copyright (C) 2015 Kwanza
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import junit.framework.Assert;
import org.dbunit.IDatabaseTester;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.kwanza.dbtool.core.DBTool;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Ivan Baluk
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractTestLock extends AbstractTransactionalJUnit4SpringContextTests {

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


            dbToolLock.lock();

            workedLock.lock();
            try {
                worked = true;
                workedCondition.signalAll();

            } finally {
                workedLock.unlock();
            }

            wasLocked = true;
            lock.lock();
            try {
                if (waitForFinish()) return;
            } finally {
                lock.unlock();
                dbToolLock.close();
                tm.commit(transaction);
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
            lock1.close();
        }
    }

    @Test
    public void testWaiteLock_1() throws InterruptedException {
        LockingThread first = new LockingThread("lock1", true);
        LockingThread second = new LockingThread("lock1", true);

        first.start();
        second.start();

        first.doWork(10000);
        Assert.assertEquals(first.worked, true);
        Assert.assertEquals(first.finished, false);
        Assert.assertEquals(second.worked, false);
        Assert.assertEquals(second.finished, false);
        first.finish();
        second.doWork(10000);
        Assert.assertEquals(first.worked, true);
        Assert.assertEquals(first.finished, true);
        Assert.assertEquals(second.worked, true);
        Assert.assertEquals(second.finished, false);
        Thread.currentThread().sleep(1000);
        second.finish();
        Assert.assertEquals(first.worked, true);
        Assert.assertEquals(first.finished, true);
        Assert.assertEquals(second.worked, true);
        Assert.assertEquals(second.finished, true);
    }

    @Test
    public void testWaiteLock_2() throws InterruptedException {
        LockingThread first = new LockingThread("lock1", true);
        LockingThread second = new LockingThread("lock2", true);

        first.start();
        second.start();

        first.doWork(100);
        second.doWork(100);
        Assert.assertEquals(first.worked, true);
        Assert.assertEquals(first.finished, false);
        Assert.assertEquals(second.worked, true);
        Assert.assertEquals(second.finished, false);
        first.finish();
        second.finish();
        Assert.assertEquals(first.worked, true);
        Assert.assertEquals(first.finished, true);
        Assert.assertEquals(second.worked, true);
        Assert.assertEquals(second.finished, true);
    }


    public final class DeadLockThead extends Thread {
        private String lock1;
        private String lock2;
        private volatile Throwable result;
        private ReentrantLock lock = new ReentrantLock();
        private Condition waiteL1Condition = lock.newCondition();
        private Condition waiteL2Condition = lock.newCondition();
        private Condition waiteL2ReleaseCondition = lock.newCondition();
        private Condition doL1Condition = lock.newCondition();
        private Condition doL2Condition = lock.newCondition();
        private volatile boolean waitingL1 = false;
        private volatile boolean waitingL2 = false;
        private volatile boolean waitingL2Release = false;
        private volatile boolean dolock1 = false;
        private volatile boolean dolock2 = false;
        private volatile TransactionStatus transaction;


        public DeadLockThead(String lock1, String lock2) {
            this.lock1 = lock1;
            this.lock2 = lock2;
        }

        @Override
        public void run() {
            transaction = tm.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
            try {
                AppLock l1 = createLockForDeadLockTest(lock1);
                AppLock l2 = createLockForDeadLockTest(lock2);
                try {

                    waiteForDoL1();
                    l1.lock();
                    waitingL1 = true;
                    lock.lock();
                    try {
                        waiteL1Condition.signalAll();
                    } finally {
                        lock.unlock();
                    }

                    System.out.println(System.currentTimeMillis() + " : " + this.getName() + ":" + lock1);

                    try {
                        waitForDoL2();
                        l2.lock();
                        lock.lock();
                        try {
                            waitingL2 = true;
                            waiteL2Condition.signalAll();
                        } finally {
                            lock.unlock();
                        }
                        System.out.println(System.currentTimeMillis() + " : " + this.getName() + ":" + lock2);

                    } catch (Throwable e) {
                        e.printStackTrace();
                        this.result = e;
                    } finally {
                        l2.close();
                        releaseL2();
                        System.out.println(System.currentTimeMillis() + " : " + this.getName() + ":-" + lock2);
                    }

                } catch (Exception e) {
                    result = e;
                    e.printStackTrace();

                } finally {
                    waitingL2Release();
                    l1.close();
                    System.out.println(System.currentTimeMillis() + " : " + this.getName() + ":-" + lock1);
                }
            } catch (Exception e) {
                result = e;
                e.printStackTrace();

            } finally {
                tm.commit(transaction);
            }
        }

        private void waitingL2Release() throws InterruptedException {
            lock.lock();
            try {
                while (!waitingL2Release) {
                    waiteL2ReleaseCondition.await();
                }
            } finally {
                lock.unlock();
            }
        }

        private void waitForDoL2() throws InterruptedException {
            lock.lock();
            try {
                while (!dolock2) {
                    doL2Condition.await();
                }
            } finally {
                lock.unlock();
            }
        }

        private void waiteForDoL1() throws InterruptedException {
            lock.lock();
            try {
                while (!dolock1) {
                    doL1Condition.await();
                }
            } finally {
                lock.unlock();
            }
        }

        private void waitingL1() throws InterruptedException {
            lock.lock();
            try {
                while (!waitingL1) {
                    waiteL1Condition.await();
                }
            } finally {
                lock.unlock();
            }
        }


        private void doL1() throws InterruptedException {
            lock.lock();
            try {
                dolock1 = true;
                doL1Condition.signalAll();
            } finally {
                lock.unlock();
            }
        }

        private void doL2() throws InterruptedException {
            lock.lock();
            try {
                dolock2 = true;
                doL2Condition.signalAll();
            } finally {
                lock.unlock();
            }
        }

        private void waitingL2() throws InterruptedException {
            lock.lock();
            try {
                while (!waitingL2) {
                    waiteL2Condition.await();
                }
            } finally {
                lock.unlock();
            }
        }


        private void releaseL2() throws InterruptedException {
            lock.lock();
            try {
                waitingL2Release = true;
                waiteL2ReleaseCondition.signalAll();
            } finally {
                lock.unlock();
            }
        }


    }

    @Test
    public void testDeadLock_1() throws InterruptedException {
        DeadLockThead t1 = new DeadLockThead("l2", "l1");
        DeadLockThead t2 = new DeadLockThead("l1", "l2");

        t1.start();
        t2.start();

        t1.doL1();
        t1.waitingL1();

        t2.doL1();
        t2.waitingL1();

        t1.doL2();
        t2.doL2();

        t1.waitingL2Release();
        t2.waitingL2Release();
        Throwable result = t1.result == null ? t2.result : t1.result;
        result.printStackTrace();
        assertDeadlockException(result);

        t1.join();
        t2.join();
    }

    protected void assertDeadlockException(Throwable result) {
        Assert.assertTrue(result.getMessage().toUpperCase().contains("DEADLOCK"));
    }


    protected abstract AppLock createLockForDeadLockTest(String name) throws SQLException;

}
