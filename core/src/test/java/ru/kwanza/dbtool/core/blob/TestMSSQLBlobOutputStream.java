package ru.kwanza.dbtool.core.blob;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author: Ivan Baluk
 */
@ContextConfiguration(locations = "classpath:mssql-config.xml")
public class TestMSSQLBlobOutputStream extends TestBlobOutputStream {
//    @Override
//    protected void setUpDatabaseConfig(DatabaseConfig config) {
//        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MsSqlDataTypeFactory());
//    }
}
