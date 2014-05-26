package ru.kwanza.dbtool.core.lock;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Ivan Baluk
 */
@ContextConfiguration(locations = "classpath:base-oracle-config.xml")
public class TestOracleLock extends AbstractTestLock {

    @Override
    protected AppLock createLockForDeadLockTest(String name) throws SQLException {
        return new OracleAppLock(dbTool,name,new ReentrantLock(),true);
    }


    @Test
    public void testLock(){
        AppLock l1 = dbTool.getLock("l1");
        l1.lock();
        try{
            System.out.println(System.currentTimeMillis());
            DeadLockThead.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            l1.close();
        }
    }
}
