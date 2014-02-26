package ru.kwanza.dbtool.core;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Ivan Baluk
 */
public class FieldGetter {

    public static <T> T getValue(ResultSet rs, String column, Class<T> type) throws SQLException {
        if (type == String.class) {
            return (T) getString(rs, column);
        } else if (type == Long.class) {
            return (T) getLong(rs, column);
        } else if (type == Integer.class) {
            return (T) getInteger(rs, column);
        }  else if (type == Boolean.class) {
            return (T) getBoolean(rs, column);
        } else if (type == BigDecimal.class) {
            return (T) getBigDecimal(rs, column);
        } else if (type == byte[].class) {
            return (T) getBytes(rs, column);
        }  else if (Date.class.isAssignableFrom(type)) {
            return (T) getTimestamp(rs, column);
        }

        return (T) getObject(rs, column);
    }

    private static Object getObject(ResultSet rs, String column) throws SQLException {
        Object result = rs.getObject(column);
        if (rs.wasNull()) {
            result = null;
        }

        return result;
    }


    public static Integer getInteger(ResultSet rs, String col) throws SQLException {
        Integer res = rs.getInt(col);
        if (rs.wasNull()) {
            res = null;
        }
        return res;
    }

    public static Long getLong(ResultSet rs, String col) throws SQLException {
        Long res = rs.getLong(col);
        if (rs.wasNull()) {
            res = null;
        }
        return res;
    }

    public static Timestamp getTimestamp(ResultSet rs, String col) throws SQLException {
        Timestamp result = rs.getTimestamp(col);
        if (rs.wasNull()) {
            result = null;
        }
        return result;
    }

    public static String getString(ResultSet rs, String col) throws SQLException {
        String result = rs.getString(col);
        if (rs.wasNull()) {
            result = null;
        }
        return result;
    }

    public static Boolean getBoolean(ResultSet rs, String col) throws SQLException {
        Boolean res = rs.getBoolean(col);
        if (rs.wasNull()) {
            res = null;
        }
        return res;
    }

    public static BigDecimal getBigDecimal(ResultSet rs, String col) throws SQLException {
        BigDecimal result = rs.getBigDecimal(col);
        if (rs.wasNull()) {
            result = null;
        }
        return result;
    }

    public static byte[] getBytes(ResultSet rs, String col) throws SQLException {
        byte[] result = rs.getBytes(col);
        if (rs.wasNull()) {
            result = null;
        }
        return result;
    }
}
