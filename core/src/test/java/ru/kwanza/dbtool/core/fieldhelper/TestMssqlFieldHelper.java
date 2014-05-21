package ru.kwanza.dbtool.core.fieldhelper;

import org.springframework.test.context.ContextConfiguration;

/**
 * @author Ivan Baluk
 */
@ContextConfiguration(locations = "classpath:mssql-config.xml")
public class TestMssqlFieldHelper extends AbstractTestFieldHelper {
    protected String getContextFileName() {
        return "mssql-config.xml";
    }
}
