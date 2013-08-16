package ru.kwanza.dbtool.core;

/**
 * @author Michael Yeskov
 */
public class TestPostgreSQLUpdateUtilWithOptimistic extends TestUpdateUtilWithOptimistic {
    protected String getSpringCfgFile() {
        return "postgresql_config_update_util.xml";
    }
}
