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

import ru.kwanza.toolbox.fieldhelper.FieldHelper;
import ru.kwanza.toolbox.fieldhelper.Property;

import java.sql.Types;

/**
 * @author Kiryl Karatsetski
 */
class FieldMapping extends AbstractFieldMapping {
    private String name;
    private String column;
    public int type;
    private Property property;

    private FieldMapping(String name, String column, int type, Property property) {
        this.name = name;
        this.column = column;
        this.type = type;
        this.property = property;
    }

    public static FieldMapping createFakeField(String name, Property property) {
        return new FieldMapping(name, null, Types.BIGINT, property);
    }

    public static FieldMapping createIdField(AbstractEntityType entityType, String name, String column, int type) {
        if (entityType.getIdField() != null) {
            throw new RuntimeException("Duplicate @IdField definition in class " + entityType.getEntityClass());
        }

        final FieldMapping result = create(entityType, name, column, type);
        entityType.setIdField(result);
        return result;
    }

    public static FieldMapping createVersionField(AbstractEntityType entityType, String name, String column, int type) {
        if (entityType.getVersionField() != null) {
            throw new RuntimeException("Duplicate @IdField definition in class " + entityType.getEntityClass());
        }

        final FieldMapping result = create(entityType, name, column, type);
        entityType.setVersionField(result);
        return result;
    }

    public static FieldMapping create(AbstractEntityType entityType, String name, String column, int type) {
        final Property property = name == null ? null : FieldHelper.constructProperty(entityType.getEntityClass(), name);
        final FieldMapping result = new FieldMapping(name, column, type, property);

        entityType.addField(result);

        EntityMappingRegistry.logRegisterFieldMapping(entityType.getEntityClass(), result);

        return result;
    }

    public String getColumn() {
        return column;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Property getProperty() {
        return this.property;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FieldMapping{");
        stringBuilder.append("id='").append(getOrderNum()).append('\'');
        stringBuilder.append(", name='").append(name).append('\'');
        stringBuilder.append(", column='").append(column).append('\'');
        stringBuilder.append(", type=").append(type);
        stringBuilder.append(", property=").append(property != null ? property.getClass() : null);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
