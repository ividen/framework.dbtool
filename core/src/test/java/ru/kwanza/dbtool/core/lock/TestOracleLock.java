package ru.kwanza.dbtool.core.lock;

import org.springframework.test.context.ContextConfiguration;

/**
 * @author Ivan Baluk
 */
@ContextConfiguration(locations = "classpath:oracle-config.xml")
public class TestOracleLock extends AbstractTestLock {

    @Override
    protected AppLock createLockForDeadLockTest(String name) {
        return null;
    }
}
