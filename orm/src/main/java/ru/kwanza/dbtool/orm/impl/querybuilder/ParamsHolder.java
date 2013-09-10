package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.Condition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
class ParamsHolder extends HashMap<String, List<Integer>> {

    void addParam(Condition condition, List<Integer> paramsTypes, int type) {
        paramsTypes.add(type);
        String paramName = condition.getParamName();
        if (paramName != null) {
            List<Integer> indexes = get(paramName);
            if (indexes == null) {
                indexes = new ArrayList<Integer>();
                put(paramName, indexes);
            }
            indexes.add(paramsTypes.size());
        }
    }
}
