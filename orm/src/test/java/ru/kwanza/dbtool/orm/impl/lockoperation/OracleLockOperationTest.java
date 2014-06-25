package ru.kwanza.dbtool.orm.impl.lockoperation;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author Alexander Guzanov
 */
@ContextConfiguration(locations = "oracle-config.xml" )
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class OracleLockOperationTest extends LockOperationTest {
}
