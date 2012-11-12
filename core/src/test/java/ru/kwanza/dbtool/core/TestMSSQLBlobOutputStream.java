package ru.kwanza.dbtool.core;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;

/**
 * @author: Ivan Baluk
 */
public class TestMSSQLBlobOutputStream extends TestBlobOutputStream {
    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MsSqlDataTypeFactory());
    }

    protected String getSpringCfgFile() {
        return "mssql_config_select_util.xml";
    }
}
