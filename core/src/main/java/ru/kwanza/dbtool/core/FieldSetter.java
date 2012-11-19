package ru.kwanza.dbtool.core;

import java.math.BigDecimal;
import java.sql.*;

/**
 * @author Ivan Baluk
 */
public class FieldSetter {

    public static void setValue(PreparedStatement preparedStatement, int index, Class type, Object value) throws SQLException {
        if (String.class.equals(type)) {
            setString(preparedStatement, index, (String) value);
        } else if (boolean.class.equals(type) || Boolean.class.equals(type)) {
            setBoolean(preparedStatement, index, (Boolean) value);
        } else if (byte.class.equals(type) || Byte.class.equals(type)) {
            setByte(preparedStatement, index, ((Byte) value));
        } else if (short.class.equals(type) || Short.class.equals(type)) {
            setShort(preparedStatement, index, ((Short) value));
        } else if (int.class.equals(type) || Integer.class.equals(type)) {
            setInt(preparedStatement, index, (Integer) value);
        } else if (long.class.equals(type) || Long.class.equals(type)) {
            setLong(preparedStatement, index, (Long) value);
        } else if (float.class.equals(type) || Float.class.equals(type)) {
            setFloat(preparedStatement, index, (Float) value);
        } else if (double.class.equals(type) || Double.class.equals(type)) {
            setDouble(preparedStatement, index, (Double) value);
        } else if (byte[].class.equals(type)) {
            setBlob(preparedStatement, index, (byte[]) value);
        } else if (java.util.Date.class.equals(type)) {
            setTimestamp(preparedStatement, index, (java.util.Date) value);
        } else if (java.sql.Date.class.equals(type)) {
            setTimestamp(preparedStatement, index, (java.sql.Date) value);
        } else if (java.sql.Time.class.equals(type)) {
            setTimestamp(preparedStatement, index, (java.sql.Time) value);
        } else if (java.sql.Timestamp.class.equals(type)) {
            setTimestamp(preparedStatement, index, (java.sql.Timestamp) value);
        } else if (BigDecimal.class.equals(type)) {
            setBigDecimal(preparedStatement, index, (BigDecimal) value);
        } else if (Blob.class.equals(type)) {
            setBlob(preparedStatement, index, (Blob) value);
        } else if (Clob.class.equals(type)) {
            setClob(preparedStatement, index, (Clob) value);
        } else {
            throw new IllegalArgumentException("Unsupported class " + type.getName());
        }
    }

    public static void setByte(PreparedStatement pst, int index, Byte value) throws SQLException {
        if (value == null) {
            pst.setNull(index, Types.TINYINT);
        } else {
            pst.setByte(index, value);
        }
    }

    public static void setShort(PreparedStatement pst, int index, Short value) throws SQLException {
        if (value == null) {
            pst.setNull(index, Types.SMALLINT);
        } else {
            pst.setShort(index, value);
        }
    }

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

    public static void setFloat(PreparedStatement pst, int index, Float value) throws SQLException {
        if (value == null) {
            pst.setNull(index, Types.FLOAT);
        } else {
            pst.setFloat(index, value);
        }
    }

    public static void setDouble(PreparedStatement pst, int index, Double value) throws SQLException {
        if (value == null) {
            pst.setNull(index, Types.DOUBLE);
        } else {
            pst.setDouble(index, value);
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

    public static void setBlob(PreparedStatement pst, int index, Blob value) throws SQLException {
        if (value == null) {
            pst.setNull(index, Types.BLOB);
        } else {
            pst.setBlob(index, value);
        }
    }

    public static void setClob(PreparedStatement pst, int index, Clob value) throws SQLException {
        if (value == null) {
            pst.setNull(index, Types.CLOB);
        } else {
            pst.setClob(index, value);
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
