package ru.kwanza.dbtool.core.blob;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;

/**
 * @author: Ivan Baluk
 */
public class TestMySQLBlobOutputStream extends TestBlobOutputStream {
    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
    }

    protected String getSpringCfgFile() {
        return "mysql-config.xml";
    }
}
