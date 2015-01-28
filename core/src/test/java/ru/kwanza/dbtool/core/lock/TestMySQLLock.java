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
