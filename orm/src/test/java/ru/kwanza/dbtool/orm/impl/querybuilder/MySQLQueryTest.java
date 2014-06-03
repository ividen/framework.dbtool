package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author Alexander Guzanov
 */

@ContextConfiguration(locations = "mysql-config.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class MySQLQueryTest extends QueryTest {

}
