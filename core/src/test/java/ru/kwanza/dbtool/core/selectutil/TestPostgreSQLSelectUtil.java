package ru.kwanza.dbtool.core.selectutil;

/**
 * @author Michael Yeskov
 */
public class TestPostgreSQLSelectUtil extends TestSelectUtil {
    protected String getSpringCfgFile() {
        return "postgresql_config_select_util.xml";
    }
}
