package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.core.KeyValue;
import ru.kwanza.dbtool.orm.api.If;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Alexander Guzanov
 */
public class ParamsHolder {
    private ArrayList<KeyValue<Integer, Object>> typesAndValues;
    private HashMap<String, List<Integer>> nameToIndex;
    private Object[] params;

    public ParamsHolder complete(){
        this.params = new Object[getCount()];
        for (int i = 0; i < params.length; i++) {
            params[i] = typesAndValues.get(i).getValue();
        }

        return this;
    }

    public void addParam(If condition, int type) {
        addParam(condition.getParamName(), condition.getValue(), type);
    }

    public void addParam(String name, int type) {
        addParam(name, null, type);
    }

    public void addParam(int type) {
        addParam(null, null, type);
    }

    private void addParam(String paramName, Object value, int type) {
        getTypesAndValues().add(new KeyValue<Integer, Object>(type, value));

        if (paramName != null) {
            List<Integer> indexes = getNameToIndex().get(paramName);
            if (indexes == null) {
                indexes = new ArrayList<Integer>();
                getNameToIndex().put(paramName, indexes);
            }
            indexes.add(getTypesAndValues().size());
        }
    }

    private HashMap<String, List<Integer>> getNameToIndex() {
        if (nameToIndex == null) {
            nameToIndex = new HashMap<String, List<Integer>>();
        }
        return nameToIndex;
    }

    private List<KeyValue<Integer, Object>> getTypesAndValues() {
        if (typesAndValues == null) {
            typesAndValues = new ArrayList<KeyValue<Integer, Object>>();
        }
        return typesAndValues;
    }

    public int getParamType(int index) {
        return typesAndValues.get(index).getKey();
    }

    public List<Integer> getIndexes(String name) {
        return nameToIndex.get(name);
    }

    public int getCount() {
        return typesAndValues != null ? typesAndValues.size() : 0;
    }

    public Object[] createParamsArray() {
        return Arrays.copyOf(params,params.length);
    }
}
