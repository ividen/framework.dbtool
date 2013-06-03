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

    private Collection<KeyValue<String, Object>> keyValues;

    public KeyValueCondition(Collection<KeyValue<String, Object>> keyValues) {
        this.keyValues = keyValues;
    }

    public String getWhereClause() {
        String condition = "";
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
            }
        }
        return condition;
    }

    public PreparedStatement installParams(PreparedStatement pst) throws SQLException {
        int index = 1;
        for (KeyValue<String, Object> kv : keyValues) {
            if (kv.getValue() != null) {
                FieldSetter.setValue(pst, index, kv.getValue().getClass(), kv.getValue());
                index++;
            }
        }

        return pst;
    }
}
