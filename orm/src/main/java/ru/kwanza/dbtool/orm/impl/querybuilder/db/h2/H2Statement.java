package ru.kwanza.dbtool.orm.impl.querybuilder.db.h2;

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

import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;
import ru.kwanza.dbtool.orm.impl.querybuilder.StatementImpl;

import java.util.Arrays;

/**
 * @author Alexander Guzanov
 */
public class H2Statement<T> extends StatementImpl<T> {
    public H2Statement(QueryConfig<T> config) {
        super(config);
    }

    @Override
    protected String prepareSql(String sql) {
        if (isUsePaging()) {
            sql += " LIMIT ? OFFSET ?";
        }

        return sql;
    }

    @Override
    protected Object[] prepareParams(Object[] params) {
        if (!isUsePaging()) {
            return params;
        }

        final Object[] result = Arrays.copyOf(params, params.length + 2);
        result[result.length - 2] = getMaxSize() ;
        result[result.length - 1] = getOffset();

        return result;
    }
}
