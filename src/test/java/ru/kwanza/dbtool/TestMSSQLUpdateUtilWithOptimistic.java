package ru.kwanza.dbtool;

/**
 * @author Guzanov Alexander
 */
public class TestMSSQLUpdateUtilWithOptimistic extends TestUpdateUtilWithOptimistic {
    protected String getSpringCfgFile() {
        return "mssql_config_update_util.xml";
    }
}
