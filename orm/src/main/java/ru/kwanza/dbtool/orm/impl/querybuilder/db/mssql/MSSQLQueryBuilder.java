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

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.querybuilder.AbstractQueryBuilder;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;

/**
 * @author Alexander Guzanov
 */
public class MSSQLQueryBuilder<T> extends AbstractQueryBuilder<T> {

    public MSSQLQueryBuilder(EntityManagerImpl em, Class entityClass) {
        super(em, entityClass);
    }

    protected IQuery<T> createQuery(QueryConfig config) {
        return new MSSQLQuery<T>(config);
    }
}
