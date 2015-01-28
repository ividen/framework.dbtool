package ru.kwanza.dbtool.orm.impl.lockoperation.db.posgresql;

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

import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.lockoperation.AbstractLockOperation;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryMapping;
import ru.kwanza.toolbox.fieldhelper.FieldHelper;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public class PostgreSQLSkipLockOperation<T> extends AbstractLockOperation<T> {
    private int tableId;

    public PostgreSQLSkipLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(em, entityClass);
        tableId = QueryMapping.getTable(entityType).hashCode();
    }

    @Override
    protected String createSQL() {
        return "SELECT " + entityType.getIdField().getColumn() + " FROM " +
                QueryMapping.getTable(entityType) + " WHERE " + entityType.getIdField().getColumn()
                + " IN (?) and pg_try_advisory_xact_lock(CAST(((CAST(? AS numeric)*31 + CAST("
                + entityType.getIdField().getColumn() + " AS numeric))%9223372036854775807) as bigint)) ORDER BY "
                + entityType.getIdField().getColumn();
    }

    @Override
    protected Object[] getParams(Collection<T> items) {
        return new Object[]{FieldHelper.getFieldCollection(items, entityType.getIdField().getProperty()),tableId};
    }
}
