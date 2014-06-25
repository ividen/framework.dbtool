package ru.kwanza.dbtool.orm.impl.lockoperation;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author Alexander Guzanov
 */
@ContextConfiguration(locations = "mssql-config.xml" )
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class MSSQLLockOperationTest extends LockOperationTest {
}
