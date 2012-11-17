package ru.kwanza.dbtool.orm.impl.operation;

/**
 * @author Kiryl Karatsetski
 */
public class MSSQLCreateOperationTest extends CreateOperationTest {

    @Override
    protected String getSpringConfigFile() {
        return "mssql-dbtool-orm-operation-test-config.xml";
    }
}
