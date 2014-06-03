package ru.kwanza.dbtool.core.lock;

import junit.framework.Assert;
import org.springframework.test.context.ContextConfiguration;

import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Alexander Guzanov
 */
@ContextConfiguration(locations = "classpath:base-mssql-config.xml")
public class TestMSSQLLock extends AbstractTestLock {

    @Override
    protected AppLock createLockForDeadLockTest(String name) throws SQLException {
        return new MSSQLAppLock(dbTool,name,new ReentrantLock(),true);
    }

    protected void assertDeadlockException(Throwable result) {
        Assert.assertTrue(result.getMessage().contains("sp_getapplock returns -3"));
    }
}
