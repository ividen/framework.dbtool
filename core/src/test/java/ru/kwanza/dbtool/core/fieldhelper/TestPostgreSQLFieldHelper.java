package ru.kwanza.dbtool.core.fieldhelper;

/**
 * @author Michael Yeskov
 */
public class TestPostgreSQLFieldHelper extends AbstractTestFieldHelper {
    @Override
    protected String getContextFileName() {
        return "postgresql_config_select_util.xml";
    }
}
