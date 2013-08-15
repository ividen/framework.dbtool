package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author Michael Yeskov
 */
@ContextConfiguration(locations = "postgresql-config.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PostgreSQLQueryTest extends QueryTest {
}
