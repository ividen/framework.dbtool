package ru.kwanza.dbtool.orm.impl.filtering;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryTest;

/**
 * @author Alexander Guzanov
 */
@ContextConfiguration(locations = "oracle-config.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OracleFilteringTest extends FilteringTest {
}
