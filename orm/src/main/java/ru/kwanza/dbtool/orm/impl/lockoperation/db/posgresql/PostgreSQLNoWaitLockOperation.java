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

/**
 * @author Alexander Guzanov
 */
public class PostgreSQLNoWaitLockOperation<T> extends AbstractLockOperation<T> {
    public PostgreSQLNoWaitLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(em, entityClass);
    }

    protected String createSQL() {
        return "SELECT " + entityType.getIdField().getColumn() + " FROM " +
                QueryMapping.getTable(entityType) + " WHERE " + entityType.getIdField().getColumn()
                + " IN (?) ORDER BY " + entityType.getIdField().getColumn() + " FOR UPDATE NOWAIT";
    }
}
