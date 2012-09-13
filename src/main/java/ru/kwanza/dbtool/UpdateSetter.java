package ru.kwanza.dbtool;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Guzanov Alexander
 */
public interface UpdateSetter<T> {
    boolean setValues(PreparedStatement pst, T object) throws SQLException;
}
