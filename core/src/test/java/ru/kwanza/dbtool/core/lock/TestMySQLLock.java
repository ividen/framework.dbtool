package ru.kwanza.dbtool.core.lock;

import org.springframework.test.context.ContextConfiguration;

/**
 * @author Alexander Guzanov
 */
@ContextConfiguration(locations = "classpath:mysql-config.xml")
public class TestMySQLLock extends AbstractTestLock {

}
