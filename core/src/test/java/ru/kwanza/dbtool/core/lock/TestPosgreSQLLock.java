package ru.kwanza.dbtool.core.lock;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.xml.soap.Detail;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Alexander Guzanov
 */
@ContextConfiguration(locations = "classpath:base-postgresql-config.xml")
public class TestPosgreSQLLock extends TestMySQLLock {
    @Override
    protected void assertDeadlockException(Throwable result) {
        Assert.assertTrue(result.getMessage().contains("ShareLock"));
    }
}


