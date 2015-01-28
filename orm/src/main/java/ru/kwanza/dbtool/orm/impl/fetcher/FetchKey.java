package ru.kwanza.dbtool.orm.impl.fetcher;

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

import ru.kwanza.dbtool.orm.api.Join;

import java.util.List;

/**
 * @author Alexander Guzanov
 */
public class FetchKey {
    private Class entityClass;
    private String encodedJoins;
    private List<Join> joins;

    public FetchKey(Class entityClass, List<Join> joins) {
        this.entityClass = entityClass;
        StringBuilder result = new StringBuilder();
        for (Join join : joins) {
            encode(join, result);
        }

        this.encodedJoins = result.toString();
        this.joins = joins;
    }

    private static void encode(Join join, StringBuilder result) {
        result.append(join.getType().name()).append('(').append(join.getPropertyName());
        for (Join j : join.getSubJoins()) {
//            if (j.getType() != Join.Type.FETCH) {
                encode(j, result);
//            }
        }

        result.append(')');
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public List<Join> getJoins() {
        return joins;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FetchKey fetchKey = (FetchKey) o;

        if (!encodedJoins.equals(fetchKey.encodedJoins)) {
            return false;
        }
        if (!entityClass.equals(fetchKey.entityClass)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = entityClass.hashCode();
        result = 31 * result + encodedJoins.hashCode();
        return result;
    }
}
