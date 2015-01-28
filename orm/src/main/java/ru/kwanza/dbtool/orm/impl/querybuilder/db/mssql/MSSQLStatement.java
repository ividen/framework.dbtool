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

import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;
import ru.kwanza.dbtool.orm.impl.querybuilder.StatementImpl;

/**
 * @author Alexander Guzanov
 */
public class MSSQLStatement<T> extends StatementImpl<T> {

    private long top;

    public MSSQLStatement(QueryConfig config) {
        super(config);
    }

    @Override
    protected String prepareSql(String sql) {
        if (isUsePaging()) {
            sql = "SELECT TOP " + (getOffset() + getMaxSize()) + " " + sql.substring("SELECT".length());
        }

        return sql;
    }

    @Override
    protected boolean isSupportAbsoluteOffset() {
        return true;
    }
}
