package ru.kwanza.dbtool.core.updateutil;

import ru.kwanza.dbtool.core.updateutil.TestUpdateUtil;

/**
 *
 * Date: 17.07.13
 * @author Michael Yeskov
 *
 */

public class TestPostgresUpdateUtil extends TestUpdateUtil {


    @Override
    protected String getSpringCfgFile() {
        return "postgresql_config_update_util.xml";
    }
}
