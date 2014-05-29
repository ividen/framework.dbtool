package ru.kwanza.dbtool.core.lock;

import junit.framework.Assert;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author Alexander Guzanov
 */
@ContextConfiguration(locations = "classpath:base-h2-config.xml")
public class TestH2Lock extends TestMySQLLock {
    @Override
    protected void assertDeadlockException(Throwable result) {
        Assert.assertTrue(result.getMessage().contains("Timeout trying to lock table"));
    }
}


