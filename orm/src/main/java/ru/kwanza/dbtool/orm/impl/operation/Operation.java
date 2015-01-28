package ru.kwanza.dbtool.orm.impl.operation;

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

import org.springframework.jdbc.core.JdbcTemplate;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;

/**
 * @author Kiryl Karatsetski
 */
public abstract class Operation {

    protected EntityManagerImpl em;
    protected Class entityClass;

    protected Operation(EntityManagerImpl em, Class entityClass) {
        if (!em.getRegistry().isRegisteredEntityClass(entityClass)) {
            throw new RuntimeException("Not registered entity class: " + entityClass);
        }
        this.em = em;
        this.entityClass = entityClass;
        initOperation();
    }

    protected abstract void initOperation();

    protected JdbcTemplate getJdbcTemplate() {
        return em.getDbTool().getJdbcTemplate();
    }
}
