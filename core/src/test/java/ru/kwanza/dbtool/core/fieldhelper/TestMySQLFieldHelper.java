package ru.kwanza.dbtool.core.fieldhelper;

import ru.kwanza.dbtool.core.fieldhelper.AbstractTestFieldHelper;

/**
 * @author Ivan Baluk
 */
public class TestMySQLFieldHelper extends AbstractTestFieldHelper {
    @Override
    protected String getContextFileName() {
        return "mysql_config_select_util.xml";
    }
}
