package ru.kwanza.dbtool.core;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;

/**
 * @author: Ivan Baluk
 */
public class TestOracleBlobInputStream extends TestBlobInputStream {
    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
    }

    protected String getSpringCfgFile() {
        return "oracle_config_select_util.xml";
    }
}