package ru.kwanza.dbtool.core.lock;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Ivan Baluk
 */
@ContextConfiguration(locations = "classpath:base-oracle-config.xml")
public class TestOracleLock extends TestMySQLLock {

    @Override
    protected AppLock createLockForDeadLockTest(String name) throws SQLException {
        return new DefaultAppLock(dbTool,name,new ReentrantLock(),true);
    }




}
