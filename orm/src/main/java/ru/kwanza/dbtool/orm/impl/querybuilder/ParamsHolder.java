package ru.kwanza.dbtool.orm.impl.querybuilder;

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

        for (int i = 0; i < defaultValues.size(); i++) {
            final KeyValue<Integer, SqlParameterValue> defaultValue = defaultValues.get(i);
            result[defaultValue.getKey()] = defaultValue.getValue();
        }

        return result;
    }
}
