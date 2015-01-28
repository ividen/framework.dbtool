package ru.kwanza.dbtool.orm.impl.querybuilder.db.mssql;

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

import ru.kwanza.dbtool.orm.api.IStatement;
import ru.kwanza.dbtool.orm.impl.querybuilder.AbstractQuery;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;

/**
 * @author Alexander Guzanov
 */
public class MSSQLQuery<T> extends AbstractQuery<T> {
    public MSSQLQuery(QueryConfig<T> config) {
        super(config);
    }

    @Override
    public IStatement<T> prepare() {
        return new MSSQLStatement<T>(config);
    }

}
