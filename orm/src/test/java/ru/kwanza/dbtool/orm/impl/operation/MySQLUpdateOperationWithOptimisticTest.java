package ru.kwanza.dbtool.orm.impl.operation;

/**
 * @author Kiryl Karatsetski
 */
public class MySQLUpdateOperationWithOptimisticTest extends UpdateOperationWithOptimisticTest {

    @Override
    protected String getSpringConfigFile() {
        return "mysql-dbtool-orm-operation-test-config.xml";
    }
}
