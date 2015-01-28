package ru.kwanza.dbtool.core.blob;

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
        return installParams(1, pst);
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
