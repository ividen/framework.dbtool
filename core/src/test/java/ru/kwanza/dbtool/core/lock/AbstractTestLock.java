package ru.kwanza.dbtool.core.lock;

import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.lock.AppLock;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Ivan Baluk
 */
public abstract class AbstractTestLock extends TestCase {
    protected Connection conn = null;
    protected static DBTool dbTool = null;
    private ApplicationContext ctx = null;

    @Override
    public void setUp() throws Exception {
        ctx = new ClassPathXmlApplicationContext(getContextFileName(), this.getClass());
        dbTool = ctx.getBean(DBTool.class);
        conn = dbTool.getDataSource().getConnection();
        conn.setAutoCommit(false);
    }


    public void testWaitUnlock() throws Exception {
        lockTest("lock1");
    }

    public void testLockException() throws Exception {
        AppLock lock = dbTool.getLock("lock");
        boolean throwCheck = false;
        try {
            lock.close();
        } catch (RuntimeException e) {
            throwCheck = true;
        }
        assertTrue(throwCheck);
    }

    private void lockTest(String lockName) throws InterruptedException {
        AppLock appLock = dbTool.getLock(lockName);
        appLock.lock();
        TestRun lock2 = new TestRun(lockName);
        assertFalse(lock2.FLAG);
        Thread t = new Thread(lock2);
        t.start();
        appLock.close();
        Thread.sleep(1000l);
        assertTrue(lock2.FLAG);
    }

    protected abstract String getContextFileName();

    protected abstract void lock(String name) throws SQLException;

    private class TestRun implements Runnable {
        public boolean FLAG = false;
        private String lockName;

        private TestRun(String lockName) {
            this.lockName = lockName;
        }

        public void run() {
            try {
                lock(lockName);
                FLAG = true;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
