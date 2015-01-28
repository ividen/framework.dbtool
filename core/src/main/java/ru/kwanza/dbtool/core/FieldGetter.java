package ru.kwanza.dbtool.core;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Утилита для чтения значений полей из ResultSet.
 * <p/>
 * Основное значение: дополнительно используется функционал {@link java.sql.ResultSet#wasNull()}
 *
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
        } else if (type == Boolean.class) {
            return (T) getBoolean(rs, column);
        } else if (type == BigDecimal.class) {
            return (T) getBigDecimal(rs, column);
        } else if (type == byte[].class) {
            return (T) getBytes(rs, column);
        } else if (Date.class.isAssignableFrom(type)) {
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

    /**
     * @see ru.kwanza.dbtool.core.FieldGetter
     */
    public static Integer getInteger(ResultSet rs, String col) throws SQLException {
        Integer res = rs.getInt(col);
        if (rs.wasNull()) {
            res = null;
        }
        return res;
    }

    /**
     * @see ru.kwanza.dbtool.core.FieldGetter
     */
    public static Long getLong(ResultSet rs, String col) throws SQLException {
        Long res = rs.getLong(col);
        if (rs.wasNull()) {
            res = null;
        }
        return res;
    }

    /**
     * @see ru.kwanza.dbtool.core.FieldGetter
     */
    public static Timestamp getTimestamp(ResultSet rs, String col) throws SQLException {
        Timestamp result = rs.getTimestamp(col);
        if (rs.wasNull()) {
            result = null;
        }
        return result;
    }

    /**
     * @see ru.kwanza.dbtool.core.FieldGetter
     */
    public static String getString(ResultSet rs, String col) throws SQLException {
        String result = rs.getString(col);
        if (rs.wasNull()) {
            result = null;
        }
        return result;
    }

    /**
     * @see ru.kwanza.dbtool.core.FieldGetter
     */
    public static Boolean getBoolean(ResultSet rs, String col) throws SQLException {
        Boolean res = rs.getBoolean(col);
        if (rs.wasNull()) {
            res = null;
        }
        return res;
    }

    /**
     * @see ru.kwanza.dbtool.core.FieldGetter
     */
    public static BigDecimal getBigDecimal(ResultSet rs, String col) throws SQLException {
        BigDecimal result = rs.getBigDecimal(col);
        if (rs.wasNull()) {
            result = null;
        }
        return result;
    }

    /**
     * @see ru.kwanza.dbtool.core.FieldGetter
     */
    public static byte[] getBytes(ResultSet rs, String col) throws SQLException {
        byte[] result = rs.getBytes(col);
        if (rs.wasNull()) {
            result = null;
        }
        return result;
    }
}
