package ru.kwanza.dbtool.core.updateutil;

import org.springframework.test.context.ContextConfiguration;

/**
 * @author Michael Yeskov
 */
@ContextConfiguration(locations = "classpath:postgresql-config-updateutil.xml")
public class TestPostgreSQLUpdateUtilWithOptimistic extends TestUpdateUtilWithOptimistic {
}
