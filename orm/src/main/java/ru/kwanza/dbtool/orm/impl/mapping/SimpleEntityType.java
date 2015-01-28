package ru.kwanza.dbtool.orm.impl.mapping;

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

/**
 * @author Alexander Guzanov
 */
public class SimpleEntityType extends AbstractEntityType {
    public SimpleEntityType(Class entityClass, String entityName, String tableName, String sql) {
        super(entityClass, entityName, tableName, sql);
    }

    @Override
    protected void validate() {
    }

    public boolean isAbstract() {
        return false;
    }
}