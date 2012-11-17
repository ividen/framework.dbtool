package ru.kwanza.dbtool.orm.impl.operation;

/**
 * @author Kiryl Karatsetski
 */
public class OracleUpdateOperationTest extends UpdateOperationTest {

    @Override
    protected String getSpringConfigFile() {
        return "oracle-dbtool-orm-operation-test-config.xml";
    }
}
