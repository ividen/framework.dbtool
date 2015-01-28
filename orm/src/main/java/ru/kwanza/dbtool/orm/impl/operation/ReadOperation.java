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

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.api.If;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryBuilderFactory;

import java.util.Collection;
import java.util.Map;

/**
 * @author Kiryl Karatsetski
 */
public class ReadOperation extends Operation implements IReadOperation {

    private IQuery queryForObject;

    private IQuery queryForCollection;

    public ReadOperation(EntityManagerImpl em, Class entityClass) {
        super(em,  entityClass);
    }

    @Override
    protected void initOperation() {
        final IFieldMapping idFieldMapping = em.getRegistry().getEntityType(entityClass).getIdField();
        if (idFieldMapping==null) {
            throw new RuntimeException("IdFieldMapping for entity class" + entityClass + " not found");
        }

        final String propertyName = idFieldMapping.getName();

        this.queryForObject =
                QueryBuilderFactory.createBuilder(em, entityClass).where(If.isEqual(propertyName)).create();
        this.queryForCollection =
                QueryBuilderFactory.createBuilder(em, entityClass).where(If.in(propertyName)).create();
    }

    public Object selectByKey(Object key) {
        return queryForObject.prepare().setParameter(1, key).select();
    }

    public Collection selectByKeys(Object keys) {
        return queryForCollection.prepare().setParameter(1, keys).selectList();
    }

    public Map selectMapByKeys(Object keys, String propertyName) {
        return queryForCollection.prepare().setParameter(1, keys).selectMap(propertyName);
    }

    public Map selectMapListByKeys(Object keys, String propertyName) {
        return queryForCollection.prepare().setParameter(1, keys).selectMapList(propertyName);
    }
}
