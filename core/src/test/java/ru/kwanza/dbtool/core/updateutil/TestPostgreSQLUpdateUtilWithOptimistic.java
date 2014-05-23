package ru.kwanza.dbtool.core.updateutil;

import org.springframework.test.context.ContextConfiguration;

/**
 * @author Michael Yeskov
 */
@ContextConfiguration(locations = "classpath:ru/kwanza/dbtool/core/updateutil/postgresql-config-updateutil.xml")
public class TestPostgreSQLUpdateUtilWithOptimistic extends TestUpdateUtilWithOptimistic {
}
