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
