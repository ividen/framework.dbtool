package ru.kwanza.dbtool.orm.impl.operation;

import org.springframework.test.context.ContextConfiguration;

/**
 * @author Michael Yeskov
 */
@ContextConfiguration(locations = "postgresql-config.xml")
public class PostgreSQLUpdateOperationWithOptimisticTest extends UpdateOperationWithOptimisticTest {
}
