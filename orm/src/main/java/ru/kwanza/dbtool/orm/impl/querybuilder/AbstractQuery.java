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

import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.api.IStatement;

/**
 * @author Alexander Guzanov
 */
public abstract class AbstractQuery<T> implements IQuery<T> {
    protected QueryConfig config;

    public AbstractQuery(QueryConfig<T> config) {
        this.config = config;
    }

    public abstract IStatement<T> prepare();

    public QueryConfig getConfig() {
        return config;
    }

    @Override
    public String toString() {
        return "Query{" +
                "query='" + config.getSql() + '\'' +
                '}';
    }

}
