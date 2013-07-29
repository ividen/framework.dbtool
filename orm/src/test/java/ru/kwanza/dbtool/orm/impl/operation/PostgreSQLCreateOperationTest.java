package ru.kwanza.dbtool.orm.impl.operation;

/**
 * @author Michael Yeskov
 */
public class PostgreSQLCreateOperationTest extends CreateOperationTest {

    @Override
    protected String getSpringConfigFile() {
        return "postgresql-dbtool-orm-operation-test-config.xml";
    }
}
