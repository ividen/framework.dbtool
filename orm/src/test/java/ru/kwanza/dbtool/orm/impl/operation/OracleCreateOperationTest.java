package ru.kwanza.dbtool.orm.impl.operation;

/**
 * @author Kiryl Karatsetski
 */
public class OracleCreateOperationTest extends CreateOperationTest {

    @Override
    protected String getSpringConfigFile() {
        return "oracle-dbtool-orm-operation-test-config.xml";
    }
}
