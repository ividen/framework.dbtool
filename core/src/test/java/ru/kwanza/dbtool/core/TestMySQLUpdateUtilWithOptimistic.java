package ru.kwanza.dbtool.core;

/**
 * @author Guzanov Alexander
 */
public class TestMySQLUpdateUtilWithOptimistic extends TestUpdateUtilWithOptimistic {
    protected String getSpringCfgFile() {
        return "mysql_config_update_util.xml";
    }
}
