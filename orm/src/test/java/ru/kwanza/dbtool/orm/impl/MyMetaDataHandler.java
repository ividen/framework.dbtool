package ru.kwanza.dbtool.orm.impl;

import org.dbunit.database.DefaultMetadataHandler;
import org.dbunit.util.SQLHelper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Alexander Guzanov
 */
public class MyMetaDataHandler extends DefaultMetadataHandler {
    public boolean matches(ResultSet columnsResultSet, String catalog,
                           String schema, String table, String column,
                           boolean caseSensitive) throws SQLException {
        String tableName = columnsResultSet.getString(3);
        String columnName = columnsResultSet.getString(4);


        boolean areEqual =
                areEqualIgnoreNull(table, tableName, caseSensitive) &&
                        areEqualIgnoreNull(column, columnName, caseSensitive);
        return areEqual;
    }

    private boolean areEqualIgnoreNull(String value1, String value2,
                                       boolean caseSensitive) {
        return SQLHelper.areEqualIgnoreNull(value1, value2, caseSensitive);
    }
}
