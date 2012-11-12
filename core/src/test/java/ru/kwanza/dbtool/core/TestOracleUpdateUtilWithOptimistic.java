package ru.kwanza.dbtool.core;

/**
 * @author Guzanov Alexander
 */
public class TestOracleUpdateUtilWithOptimistic extends TestUpdateUtilWithOptimistic {
    protected String getSpringCfgFile() {
        return "oracle_config_update_util.xml";
    }
}
