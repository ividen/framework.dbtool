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

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;

import java.util.StringTokenizer;

/**
 * @author Alexander Guzanov
 */
class ColumnFactory {
    public static final String DOT_CHAR = ".";
    private final AbstractQueryBuilder builder;

    ColumnFactory(AbstractQueryBuilder builder) {
        this.builder = builder;
    }

    Column findColumn(QueryMapping root, String propertyName) {
        final int index = propertyName.lastIndexOf(DOT_CHAR);
        Class entityClass = builder.getEntityClass();
        if (index > 0) {
            final String path = propertyName.substring(0, index);
            propertyName = propertyName.substring(index + 1);

            StringTokenizer st = new StringTokenizer(path, ".");
            while (st.hasMoreElements()) {
                final String token = st.nextToken();
                root = builder.getQueryMappingFactory().registerInfo(root, Join.inner(token));
                entityClass = root.getRelationMapping().getRelationClass();
            }
        }

        final IFieldMapping fieldMapping = builder.getRegistry().getEntityType(entityClass).getField(propertyName);
        if (fieldMapping == null) {
            throw new IllegalArgumentException("Unknown field " + propertyName + " in " + entityClass.getName() + "!");
        }

        return new Column(root, fieldMapping);
    }

}
