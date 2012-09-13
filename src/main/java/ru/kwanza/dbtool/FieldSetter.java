package ru.kwanza.dbtool;

import java.math.BigDecimal;
import java.sql.*;

/**
 * @author Ivan Baluk
 */
public class FieldSetter {
    public static void setInt(PreparedStatement pst, int index, Integer value) throws SQLException {
        if (value == null) {
            pst.setNull(index, Types.INTEGER);
        } else {
            pst.setInt(index, value);
        }
    }

    public static void setLong(PreparedStatement pst, int index, Long value) throws SQLException {
        if (value == null) {
            pst.setNull(index, Types.BIGINT);
        } else {
            pst.setLong(index, value);
        }
    }

    public static void setString(PreparedStatement pst, int index, String value) throws SQLException {
        if (value == null) {
            pst.setNull(index, Types.VARCHAR);
        } else {
            pst.setString(index, value);
        }
    }

    public static void setBoolean(PreparedStatement pst, int index, Boolean value) throws SQLException {
        if (value == null) {
            pst.setNull(index, Types.NUMERIC);
        } else {
            pst.setBoolean(index, value);
        }
    }

    public static void setTimestamp(PreparedStatement pst, int index, Timestamp value) throws SQLException {
        if (value == null) {
            pst.setNull(index, Types.TIMESTAMP);
        } else {
            pst.setTimestamp(index, value);
        }
    }

    public static void setTimestamp(PreparedStatement pst, int index, java.util.Date value) throws SQLException {
        if (value == null) {
            pst.setNull(index, Types.TIMESTAMP);
        } else {
            pst.setTimestamp(index, new Timestamp(value.getTime()));
        }
    }

    public static void setBlob(PreparedStatement pst, int index, byte[] value) throws SQLException {
        if (value == null) {
            pst.setNull(index, Types.BINARY);
        } else {
            pst.setBytes(index, value);
        }
    }

    public static void setBigDecimal(PreparedStatement pst, int index, BigDecimal value) throws SQLException {
        if (value == null) {
            pst.setNull(index, Types.DECIMAL);
        } else {
            pst.setBigDecimal(index, value);
        }
    }
}
