package ru.kwanza.dbtool.orm.impl.operation;

/**
 * @author Kiryl Karatsetski
 */
public class OracleUpdateOperationWithOptimisticTest extends UpdateOperationWithOptimisticTest {

    @Override
    protected String getSpringConfigFile() {
        return "oracle-dbtool-orm-operation-test-config.xml";
    }
}
