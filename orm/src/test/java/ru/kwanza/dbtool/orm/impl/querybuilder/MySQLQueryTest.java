package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.kwanza.dbtool.orm.impl.MyMetaDataHandler;

import java.sql.SQLException;

/**
 * @author Alexander Guzanov
 */

@ContextConfiguration(locations = "mysql-config.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MySQLQueryTest extends QueryTest {

}
