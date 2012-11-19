package ru.kwanza.dbtool.orm.impl.operation;

/**
 * @author Kiryl Karatsetski
 */
public class MSSQLUpdateOperationWithOptimisticTest extends UpdateOperationWithOptimisticTest {

    @Override
    protected String getSpringConfigFile() {
        return "mssql-dbtool-orm-operation-test-config.xml";
    }
}
