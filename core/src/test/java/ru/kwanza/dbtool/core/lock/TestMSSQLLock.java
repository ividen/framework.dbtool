package ru.kwanza.dbtool.core.lock;

import org.springframework.test.context.ContextConfiguration;

/**
 * @author Ivan Baluk
 */
@ContextConfiguration(locations = "classpath:mssql-config.xml")
public class TestMSSQLLock extends AbstractTestLock {
}
