package ru.kwanza.dbtool.orm.impl.filtering;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author Alexander Guzanov
 */

@ContextConfiguration(locations = "mysql-config.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MySQLFilteringTest extends FilteringTest {
}
