package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author Alexander Guzanov
 */

@ContextConfiguration(locations = "mssql-config.xml")
public class MSSQLQueryTest extends QueryTest {
}
