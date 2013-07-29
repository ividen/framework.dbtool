package ru.kwanza.dbtool.orm.impl.operation;

/**
 * @author Michael Yeskov
 */
public class PostgreSQLUpdateOperationWithOptimisticTest extends UpdateOperationWithOptimisticTest {

    @Override
    protected String getSpringConfigFile() {
        return "postgresql-dbtool-orm-operation-test-config.xml";
    }
}
