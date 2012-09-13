package ru.kwanza.dbtool;

/**
 * @author Guzanov Alexander
 */
public class TestOracleUpdateUtilWithOptimistic extends TestUpdateUtilWithOptimistic {
    protected String getSpringCfgFile() {
        return "oracle_config_update_util.xml";
    }
}
