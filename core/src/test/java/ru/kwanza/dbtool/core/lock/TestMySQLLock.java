package ru.kwanza.dbtool.core.lock;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Alexander Guzanov
 */
@ContextConfiguration(locations = "classpath:base-mysql-config.xml")
public class TestMySQLLock extends AbstractTestLock {

    @Override
    protected AppLock createLockForDeadLockTest(String name) throws SQLException {
        try {
            return new DefaultAppLock(dbTool, name, new ReentrantLock(), true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDeadLock_1() throws InterruptedException {
        TransactionStatus transaction = tm.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
        try {
            jdbcTemplate.execute("delete dbmutex;");
            jdbcTemplate.execute("insert into dbmutex('l1');");
            jdbcTemplate.execute("insert into dbmutex('l2');");
            tm.commit(transaction);
        } catch (Exception e) {
            tm.rollback(transaction);
        }

        super.testDeadLock_1();
    }

    @Test
    public void testDeadLock_2() throws InterruptedException {
        TransactionStatus transaction = tm.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
        try {
            jdbcTemplate.execute("delete from dbmutex;");
            tm.commit(transaction);
        } catch (Exception e) {
            tm.rollback(transaction);
        }

        super.testDeadLock_1();
    }
}
