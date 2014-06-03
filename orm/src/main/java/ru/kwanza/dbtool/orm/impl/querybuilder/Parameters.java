package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.If;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Guzanov
 */
class Parameters {
    private List<Param> rawParams;

    static class Param {
        private int sqlType;
        private String name;
        private Object value;

        public Param(int sqlType, String name, Object value) {
            this.sqlType = sqlType;
            this.name = name;
            this.value = value;
        }

        int getSqlType() {
            return sqlType;
        }

        String getName() {
            return name;
        }

        Object getValue() {
            return value;
        }
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
        getRawParams().add(new Param(type, paramName, value));
    }

    private List<Param> getRawParams() {
        if (rawParams == null) {
            rawParams = new ArrayList<Param>();
        }

        return rawParams;
    }

    public Parameters join(Parameters whereParams) {
        if (whereParams.rawParams != null) {
            getRawParams().addAll(whereParams.rawParams);
        }

        return this;
    }

    public ParamsHolder createHolder() {
        return new ParamsHolder(rawParams);
    }
}
