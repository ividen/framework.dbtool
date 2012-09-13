package ru.kwanza.dbtool;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Guzanov Alexander
 */
public interface UpdateSetterWithVersion<T, V> {
    boolean setValues(PreparedStatement pst, T object, V newVersion, V oldVersion) throws SQLException;
}
