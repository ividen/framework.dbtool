package ru.kwanza.dbtool.core.updateutil;

/**
 * @author Guzanov Alexander
 */
public class TestMySQLUpdateUtilWithOptimistic extends TestUpdateUtilWithOptimistic {
    protected String getSpringCfgFile() {
        return "mysql_config_update_util.xml";
    }
}
