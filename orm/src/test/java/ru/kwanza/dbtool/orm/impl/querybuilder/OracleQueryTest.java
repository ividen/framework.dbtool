package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.sql.SQLException;

/**
 * @author Alexander Guzanov
 */
@ContextConfiguration(locations = "oracle-config.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class OracleQueryTest extends QueryTest {

    public IDatabaseConnection getConnection() throws SQLException, DatabaseUnitException {
        return new DatabaseConnection(dataSource.getConnection(), "DBTOOL_TEST");
    }
}
