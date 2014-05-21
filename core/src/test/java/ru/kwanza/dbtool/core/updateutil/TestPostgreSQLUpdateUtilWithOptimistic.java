package ru.kwanza.dbtool.core.updateutil;

/**
 * @author Michael Yeskov
 */
public class TestPostgreSQLUpdateUtilWithOptimistic extends TestUpdateUtilWithOptimistic {
    protected String getSpringCfgFile() {
        return "postgresql_config_update_util.xml";
    }
}
