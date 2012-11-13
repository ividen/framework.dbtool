package ru.kwanza.dbtool.core.util;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Alexander Guzanov
 */
public final class FieldValueExtractor {
    private FieldValueExtractor() {
    }

    public static Object getValue(ResultSet rs, String columnName, Class requiredType) throws SQLException {
        Object value = null;
        boolean wasNullCheck = false;

        if (String.class.equals(requiredType)) {
            value = rs.getString(columnName);
        } else if (boolean.class.equals(requiredType) || Boolean.class.equals(requiredType)) {
            value = rs.getBoolean(columnName);
            wasNullCheck = true;
        } else if (byte.class.equals(requiredType) || Byte.class.equals(requiredType)) {
            value = rs.getByte(columnName);
            wasNullCheck = true;
        } else if (short.class.equals(requiredType) || Short.class.equals(requiredType)) {
            value = rs.getShort(columnName);
            wasNullCheck = true;
        } else if (int.class.equals(requiredType) || Integer.class.equals(requiredType)) {
            value = rs.getInt(columnName);
            wasNullCheck = true;
        } else if (long.class.equals(requiredType) || Long.class.equals(requiredType)) {
            value = rs.getLong(columnName);
            wasNullCheck = true;
        } else if (float.class.equals(requiredType) || Float.class.equals(requiredType)) {
            value = rs.getFloat(columnName);
            wasNullCheck = true;
        } else if (double.class.equals(requiredType) || Double.class.equals(requiredType) ||
                Number.class.equals(requiredType)) {
            value = rs.getDouble(columnName);
            wasNullCheck = true;
        } else if (byte[].class.equals(requiredType)) {
            value = rs.getBytes(columnName);
        } else if (java.sql.Date.class.equals(requiredType)) {
            value = rs.getDate(columnName);
        } else if (java.sql.Time.class.equals(requiredType)) {
            value = rs.getTime(columnName);
        } else if (java.sql.Timestamp.class.equals(requiredType) || java.util.Date.class.equals(requiredType)) {
            value = rs.getTimestamp(columnName);
        } else if (BigDecimal.class.equals(requiredType)) {
            value = rs.getBigDecimal(columnName);
        } else if (Blob.class.equals(requiredType)) {
            value = rs.getBlob(columnName);
        } else if (Clob.class.equals(requiredType)) {
            value = rs.getClob(columnName);
        } else {
            value = rs.getObject(columnName);
        }

        if (wasNullCheck && value != null && rs.wasNull()) {
            value = null;
        }
        return value;
    }
}
