package ru.kwanza.dbtool.core.blob;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author: Ivan Baluk
 */
@ContextConfiguration(locations = "classpath:mysql-config.xml")
public class TestMySQLBlobOutputStream extends TestBlobOutputStream {
}
