package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.core.util.FieldValueExtractor;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Alexander Guzanov
 */
abstract class QueryColumnExtractor {

    public abstract Object readColumnValue(ResultSet rs, QueryMapping queryMapping, IFieldMapping fm)
            throws SQLException;


    public static final QueryColumnExtractor BY_INDEX = new QueryColumnExtractor() {
        public Object readColumnValue(ResultSet rs, QueryMapping queryMapping, IFieldMapping fm)
                throws SQLException {
            return FieldValueExtractor.getValue(rs, queryMapping.getColumnIndex(fm), fm.getProperty().getType());
        }
    };

    public static final QueryColumnExtractor BY_ALIAS = new QueryColumnExtractor() {
        public Object readColumnValue(ResultSet rs, QueryMapping queryMapping, IFieldMapping fm)
                throws SQLException {
            return FieldValueExtractor.getValue(rs, queryMapping.getColumnAlias(fm), fm.getProperty().getType());
        }
    };
}
