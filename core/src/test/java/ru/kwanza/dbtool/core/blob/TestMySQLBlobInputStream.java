package ru.kwanza.dbtool.core.blob;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;

/**
 * @author: Ivan Baluk
 */
public class TestMySQLBlobInputStream extends TestBlobInputStream {

    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MsSqlDataTypeFactory());
    }

    protected String getSpringCfgFile() {
        return "mysql_config_blob.xml";
    }
}
