package ru.kwanza.dbtool.orm.impl.operation;

/**
 * @author Kiryl Karatsetski
 */
public class MySQLUpdateOperationTest extends UpdateOperationTest {

    @Override
    protected String getSpringConfigFile() {
        return "mssql-dbtool-orm-operation-test-config.xml";
    }
}
