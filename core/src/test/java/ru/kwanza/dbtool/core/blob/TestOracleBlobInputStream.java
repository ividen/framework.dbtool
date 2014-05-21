package ru.kwanza.dbtool.core.blob;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author: Ivan Baluk
 */
@ContextConfiguration(locations = "classpath:oracle-config.xml")
public class TestOracleBlobInputStream extends TestBlobInputStream {
}
