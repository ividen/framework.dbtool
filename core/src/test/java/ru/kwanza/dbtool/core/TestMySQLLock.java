package ru.kwanza.dbtool.core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Alexander Guzanov
 */
public class TestMySQLLock extends AbstractTestLock {

    public static final String LOCK_SQL = "SELECT GET_LOCK(?,?)";
    public static final String RELEASE_LOCK_SQL = "SELECT RELEASE_LOCK(?)";

    protected String getContextFileName() {
        return "mysql_config_select_util.xml";
    }

    @Override
    protected void lock(String name) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(LOCK_SQL);
            ps.setString(1, name);
            ps.setInt(2, 100);

            while (true) {
                if (rs != null) {
                    rs.close();
                }
                rs = ps.executeQuery();
                rs.next();
                int result = rs.getInt(1);
                if (result == 1) {
                    break;
                }
            }
        } finally {
            dbTool.closeResources(rs, ps);
        }
    }
}
