package ru.kwanza.dbtool.orm.impl.operation;

/**
 * @author Kiryl Karatsetski
 */
public class MySQLCreateOperationTest extends CreateOperationTest {

    @Override
    protected String getSpringConfigFile() {
        return "mysql-dbtool-orm-operation-test-config.xml";
    }
}
