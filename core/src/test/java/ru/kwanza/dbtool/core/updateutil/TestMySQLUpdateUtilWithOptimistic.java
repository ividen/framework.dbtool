package ru.kwanza.dbtool.core.updateutil;

import org.springframework.test.context.ContextConfiguration;

/**
 * @author Guzanov Alexander
 */
@ContextConfiguration(locations = "classpath:mysql-config-updateutil.xml")
public class TestMySQLUpdateUtilWithOptimistic extends TestUpdateUtilWithOptimistic {
}
