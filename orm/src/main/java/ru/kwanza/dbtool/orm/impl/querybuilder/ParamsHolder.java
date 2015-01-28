package ru.kwanza.dbtool.orm.impl.querybuilder;

/*
 * #%L
 * dbtool-orm
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

import org.springframework.jdbc.core.SqlParameterValue;
import ru.kwanza.dbtool.core.KeyValue;
import ru.kwanza.dbtool.core.SqlCollectionParameterValue;

import java.util.*;

/**
 * @author Alexander Guzanov
 */
public class ParamsHolder {
    private Map<String, List<Integer>> nameToIndex;
    private List<KeyValue<Integer, Integer>> indexesAndTypes;
    private List<KeyValue<Integer, SqlParameterValue>> defaultValues;
    private int totalParamsCount = 0;

    public ParamsHolder(List<Parameters.Param> rawParams) {
        if (rawParams != null) {
            for (Parameters.Param rawParam : rawParams) {
                addParam(rawParam.getName(), rawParam.getValue(), rawParam.getSqlType());
            }
        }
    }

    private void addParam(String paramName, Object value, int type) {
        if (value != null) {
            getDefaultValues().add(new KeyValue<Integer, SqlParameterValue>(totalParamsCount, value instanceof Collection
                    ? new SqlCollectionParameterValue(type, (Collection) value)
                    : new SqlParameterValue(type, value)));
        } else {
            getIndexesAndTypes().add(new KeyValue<Integer, Integer>(totalParamsCount, type));
            if (paramName != null) {
                List<Integer> indexes = getNameToIndex().get(paramName);
                if (indexes == null) {
                    indexes = new ArrayList<Integer>();
                    getNameToIndex().put(paramName, indexes);
                }
                indexes.add(getIndexesAndTypes().size());
            }
        }
        totalParamsCount++;
    }

    private Map<String, List<Integer>> getNameToIndex() {
        if (nameToIndex == null) {
            nameToIndex = new HashMap<String, List<Integer>>();
        }
        return nameToIndex;
    }

    private List<KeyValue<Integer, SqlParameterValue>> getDefaultValues() {
        if (defaultValues == null) {
            defaultValues = new ArrayList<KeyValue<Integer, SqlParameterValue>>();
        }
        return defaultValues;
    }

    private List<KeyValue<Integer, Integer>> getIndexesAndTypes() {
        if (indexesAndTypes == null) {
            indexesAndTypes = new ArrayList<KeyValue<Integer, Integer>>();
        }
        return indexesAndTypes;
    }

    public int getParamType(int index) {
        return indexesAndTypes.get(index).getValue();
    }

    public List<Integer> getIndexes(String name) {
        return name == null ? null : nameToIndex.get(name);
    }

    public int getCount() {
        return indexesAndTypes != null ? indexesAndTypes.size() : 0;
    }

    public Object[] createParamsArray() {
        return new Object[getCount()];
    }

    public Object[] fullParamsArray(Object[] params) {
        if (defaultValues == null) {
            return params;
        }

        Object[] result = new Object[totalParamsCount];

        if (indexesAndTypes != null) {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                result[indexesAndTypes.get(i).getKey()] = param;
            }
        }

        for (final KeyValue<Integer, SqlParameterValue> defaultValue : defaultValues) {
            result[defaultValue.getKey()] = defaultValue.getValue();
        }

        return result;
    }
}
