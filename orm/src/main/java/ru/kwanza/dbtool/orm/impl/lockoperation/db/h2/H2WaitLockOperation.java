package ru.kwanza.dbtool.orm.impl.lockoperation.db.h2;

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

import ru.kwanza.dbtool.orm.api.LockResult;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryMapping;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public class H2WaitLockOperation<T> extends H2LockOperation<T> {

    public static final int TIMEOUT = 1 * 60 * 1000;

    public H2WaitLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(em, entityClass);
    }

    @Override
    public LockResult<T> lock(Collection<T> items) {
        int lockTimeout = getLockTimeout();
        setLockTimeout(TIMEOUT);
        try {
            return super.lock(items);
        } finally {
            setLockTimeout(lockTimeout);
        }
    }

    @Override
    protected String createSQL() {
        return "SELECT " + entityType.getIdField().getColumn() + " FROM " +
                QueryMapping.getTable(entityType) + " WHERE " + entityType.getIdField().getColumn() + " IN (?) FOR UPDATE";
    }
}
