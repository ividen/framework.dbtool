package ru.kwanza.dbtool.core;

import java.math.BigDecimal;
import java.sql.*;

/**
 * @author Ivan Baluk
 */
public class FieldSetter {

    public static void setValue(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        final Class requiredType = value != null ? value.getClass() : null;
        if (String.class.equals(requiredType)) {
            setString(preparedStatement, index, (String) value);
        } else if (boolean.class.equals(requiredType) || Boolean.class.equals(requiredType)) {
            setBoolean(preparedStatement, index, (Boolean) value);
        } else if (byte.class.equals(requiredType) || Byte.class.equals(requiredType)) {
            setInt(preparedStatement, index, ((Byte) value).intValue());
        } else if (short.class.equals(requiredType) || Short.class.equals(requiredType)) {
            setInt(preparedStatement, index, ((Short) value).intValue());
        } else if (int.class.equals(requiredType) || Integer.class.equals(requiredType)) {
            setInt(preparedStatement, index, (Integer) value);
        } else if (long.class.equals(requiredType) || Long.class.equals(requiredType)) {
            setLong(preparedStatement, index, (Long) value);
        } else if (float.class.equals(requiredType) || Float.class.equals(requiredType)) {
            //TODO
            throw new UnsupportedOperationException("Temporary");
        } else if (double.class.equals(requiredType) || Double.class.equals(requiredType) || Number.class.equals(requiredType)) {
            //TODO
            throw new UnsupportedOperationException("Temporary");
        } else if (byte[].class.equals(requiredType)) {
            setBlob(preparedStatement, index, (byte[]) value);
        } else if (java.sql.Date.class.equals(requiredType)) {
            setTimestamp(preparedStatement, index, (java.sql.Date) value);
            throw new UnsupportedOperationException("Temporary");
        } else if (java.sql.Time.class.equals(requiredType)) {
            setTimestamp(preparedStatement, index, (java.sql.Time) value);
            throw new UnsupportedOperationException("Temporary");
        } else if (java.sql.Timestamp.class.equals(requiredType) || java.util.Date.class.equals(requiredType)) {
            setTimestamp(preparedStatement, index, (java.sql.Timestamp) value);
            throw new UnsupportedOperationException("Temporary");
        } else if (BigDecimal.class.equals(requiredType)) {
            setBigDecimal(preparedStatement, index, (BigDecimal) value);
        } else if (Blob.class.equals(requiredType)) {
            //TODO
            throw new UnsupportedOperationException("Temporary");
        } else if (Clob.class.equals(requiredType)) {
            //TODO
            throw new UnsupportedOperationException("Temporary");
        } else {
            preparedStatement.setObject(index, value);
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
