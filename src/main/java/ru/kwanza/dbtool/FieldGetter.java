package ru.kwanza.dbtool;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author Ivan Baluk
 */
public class FieldGetter {

    public static Integer getInteger(ResultSet rs, String col) throws SQLException {
        Integer res = rs.getInt(col);
        if (rs.wasNull()) res = null;
        return res;
    }

    public static Long getLong(ResultSet rs, String col) throws SQLException {
        Long res = rs.getLong(col);
        if (rs.wasNull()) res = null;
        return res;
    }

    public static Timestamp getTimestamp(ResultSet rs, String col) throws SQLException {
        return rs.getTimestamp(col);
    }

    public static String getString(ResultSet rs, String col) throws SQLException {
        return rs.getString(col);
    }

    public static Boolean getBoolean(ResultSet rs, String col) throws SQLException {
        Boolean res = rs.getBoolean(col);
        if (rs.wasNull()) res = null;
        return res;
    }

    public static BigDecimal getBigDecimal(ResultSet rs, String col) throws SQLException {
        return rs.getBigDecimal(col);
    }

    public static byte[] getBytes(ResultSet rs, String col) throws SQLException {
        return rs.getBytes(col);
    }
}
