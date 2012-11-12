package ru.kwanza.dbtool.core.blob;

import ru.kwanza.dbtool.core.KeyValue;

import java.util.Collection;

/**
 * @author: Ivan Baluk
 */
class KeyValueCondition {

    private Collection<KeyValue<String, Object>> keyValues;

    public KeyValueCondition(Collection<KeyValue<String, Object>> keyValues) {
        this.keyValues = keyValues;
    }

    public String getStringCondition() {
        String condition = "";
        for (KeyValue<String, Object> kv : keyValues) {
            if (!condition.isEmpty()) {
                condition += " and ";
            }
            condition += kv.getKey();
            Object value = kv.getValue();
            if (value == null) {
                condition += " is null";
            } else {
                condition += " = " + String.valueOf(value);
            }
        }
        return condition;
    }
}
