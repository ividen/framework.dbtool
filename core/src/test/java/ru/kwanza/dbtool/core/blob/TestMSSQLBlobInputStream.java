package ru.kwanza.dbtool.core.blob;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author: Ivan Baluk
 */
@ContextConfiguration(locations = "classpath:mssql-config-blob.xml")
public class TestMSSQLBlobInputStream extends TestBlobInputStream {
}
