package ru.kwanza.dbtool.core.blob;

import ru.kwanza.dbtool.core.FieldSetter;
import ru.kwanza.dbtool.core.KeyValue;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author: Ivan Baluk
 */
class KeyValueCondition {

    private String whereClause;
    private int paramsCount;
    private Collection<KeyValue<String, Object>> keyValues;

    public KeyValueCondition(Collection<KeyValue<String, Object>> keyValues) {
        this.keyValues = keyValues;
        createWhereClause();
    }

    public void createWhereClause() {
        String condition = "";
        int index = 0;

        for (KeyValue<String, Object> kv : keyValues) {
            if (!condition.isEmpty()) {
                condition += " AND ";
            }
            condition += kv.getKey();
            Object value = kv.getValue();
            if (value == null) {
                condition += " IS NULL";
            } else {
                condition += " = ?";
                index++;
            }
        }

        this.whereClause = condition;
        this.paramsCount = index;
    }

    public int getParamsCount() {
        return paramsCount;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public PreparedStatement installParams(PreparedStatement pst) throws SQLException {
       return installParams(1,pst);
    }

    public PreparedStatement installParams(int index, PreparedStatement pst) throws SQLException {
        for (KeyValue<String, Object> kv : keyValues) {
            if (kv.getValue() != null) {
                FieldSetter.setValue(pst, index, kv.getValue().getClass(), kv.getValue());
                index++;
            }
        }

        return pst;
    }
}
