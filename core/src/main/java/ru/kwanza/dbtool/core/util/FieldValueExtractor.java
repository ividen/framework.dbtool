package ru.kwanza.dbtool.core.util;

/*
 * #%L
 * dbtool-core
 * %%
 * Copyright (C) 2015 Kwanza
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.math.BigDecimal;
import java.sql.*;

/**
 * @author Alexander Guzanov
 */
public final class FieldValueExtractor {
    private FieldValueExtractor() {
    }

    public static Object getValue(ResultSet rs, String column, Class requiredType) throws SQLException {
        return getValue0(rs, column, requiredType);
    }

    public static Object getValue(ResultSet rs, int column, Class requiredType) throws SQLException {
        return getValue0(rs, column, requiredType);
    }

    private static Object getValue0(ResultSet rs, Object column, Class requiredType) throws SQLException {
        Object value = null;
        boolean wasNullCheck = false;

        if (String.class.equals(requiredType)) {
            value = getString(rs, column);
        } else if (boolean.class.equals(requiredType) || Boolean.class.equals(requiredType)) {
            value = getBoolean(rs, column);
            wasNullCheck = true;
        } else if (byte.class.equals(requiredType) || Byte.class.equals(requiredType)) {
            value = getByte(rs, column);
            wasNullCheck = true;
        } else if (short.class.equals(requiredType) || Short.class.equals(requiredType)) {
            value = getShort(rs, column);
            wasNullCheck = true;
        } else if (int.class.equals(requiredType) || Integer.class.equals(requiredType)) {
            value = getInt(rs, column);
            wasNullCheck = true;
        } else if (long.class.equals(requiredType) || Long.class.equals(requiredType)) {
            value = getLong(rs, column);
            wasNullCheck = true;
        } else if (float.class.equals(requiredType) || Float.class.equals(requiredType)) {
            value = getFloat(rs, column);
            wasNullCheck = true;
        } else if (double.class.equals(requiredType) || Double.class.equals(requiredType) ||
                Number.class.equals(requiredType)) {
            value = getDouble(rs, column);
            wasNullCheck = true;
        } else if (byte[].class.equals(requiredType)) {
            value = getBytes(rs, column);
        } else if (java.sql.Date.class.equals(requiredType)) {
            value = getDate(rs, column);
        } else if (java.sql.Time.class.equals(requiredType)) {
            value = getTime(rs, column);
        } else if (java.sql.Timestamp.class.equals(requiredType) || java.util.Date.class.equals(requiredType)) {
            value = getTimestamp(rs, column);
        } else if (BigDecimal.class.equals(requiredType)) {
            value = getBigDecimal(rs, column);
        } else if (Blob.class.equals(requiredType)) {
            value = getBlob(rs, column);
        } else if (Clob.class.equals(requiredType)) {
            value = getClob(rs, column);
        } else {
            value = getObject(rs, column);
        }

        if (wasNullCheck && value != null && rs.wasNull()) {
            value = null;
        }
        return value;
    }

    private static BigDecimal getBigDecimal(ResultSet rs, Object column) throws SQLException {
        return column instanceof String ? rs.getBigDecimal((String) column) : rs.getBigDecimal((Integer) column);
    }

    private static Object getObject(ResultSet rs, Object column) throws SQLException {
        return column instanceof String ? rs.getObject((String) column) : rs.getObject((Integer) column);
    }

    private static Clob getClob(ResultSet rs, Object column) throws SQLException {
        return column instanceof String ? rs.getClob((String) column) : rs.getClob((Integer) column);
    }

    private static Blob getBlob(ResultSet rs, Object column) throws SQLException {
        return column instanceof String ? rs.getBlob((String) column) : rs.getBlob((Integer) column);
    }

    private static Timestamp getTimestamp(ResultSet rs, Object column) throws SQLException {
        return column instanceof String ? rs.getTimestamp((String) column) : rs.getTimestamp((Integer) column);
    }

    private static Time getTime(ResultSet rs, Object column) throws SQLException {
        return column instanceof String ? rs.getTime((String) column) : rs.getTime((Integer) column);
    }

    private static Date getDate(ResultSet rs, Object column) throws SQLException {
        return column instanceof String ? rs.getDate((String) column) : rs.getDate((Integer) column);
    }

    private static byte[] getBytes(ResultSet rs, Object column) throws SQLException {
        return column instanceof String ? rs.getBytes((String) column) : rs.getBytes((Integer) column);
    }

    private static double getDouble(ResultSet rs, Object column) throws SQLException {
        return column instanceof String ? rs.getDouble((String) column) : rs.getDouble((Integer) column);
    }

    private static float getFloat(ResultSet rs, Object column) throws SQLException {
        return column instanceof String ? rs.getFloat((String) column) : rs.getFloat((Integer) column);
    }

    private static long getLong(ResultSet rs, Object column) throws SQLException {
        return column instanceof String ? rs.getLong((String) column) : rs.getLong((Integer) column);
    }

    private static int getInt(ResultSet rs, Object column) throws SQLException {
        return column instanceof String ? rs.getInt((String) column) : rs.getInt((Integer) column);
    }

    private static short getShort(ResultSet rs, Object column) throws SQLException {
        return column instanceof String ? rs.getShort((String) column) : rs.getShort((Integer) column);
    }

    private static byte getByte(ResultSet rs, Object column) throws SQLException {
        return column instanceof String ? rs.getByte((String) column) : rs.getByte((Integer) column);
    }

    private static boolean getBoolean(ResultSet rs, Object column) throws SQLException {
        return column instanceof String ? rs.getBoolean((String) column) : rs.getBoolean((Integer) column);
    }

    private static String getString(ResultSet rs, Object column) throws SQLException {
        return column instanceof String ? rs.getString((String) column) : rs.getString((Integer) column);
    }
}
