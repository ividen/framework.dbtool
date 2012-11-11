package ru.kwanza.dbtool;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;
import org.dbunit.ext.oracle.OracleDataTypeFactory;

/**
 * @author: Ivan Baluk
 */
public class TestMSSQLBlobInputStream extends TestBlobInputStream {

    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MsSqlDataTypeFactory());
    }

    protected String getSpringCfgFile() {
        return "mssql_config_select_util.xml";
    }
}
