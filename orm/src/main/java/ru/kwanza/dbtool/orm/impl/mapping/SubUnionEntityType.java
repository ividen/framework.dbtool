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

import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public class SubUnionEntityType extends AbstractEntityType {
    private static final String FIELD_PREFIX = "f_";

    private IEntityType original;
    private Map<String, SubEntityFieldMapping> fields = new LinkedHashMap<String, SubEntityFieldMapping>();

    public SubUnionEntityType(IEntityType original, UnionEntityType unionEntityType) {
//        this.original = original;
//        final Collection<AbstractFieldMapping> fields1 = original.getFields();
//        for (AbstractFieldMapping field : fields1) {
//            if (unionEntityType.getCommonField(field.getName()) == null) {
//                String alias = FIELD_PREFIX + unionEntityType.nextFieldAlias();
//                SubEntityFieldMapping subEntityField = new SubEntityFieldMapping(0, field, alias);
//                SubEntityFieldMapping unionField = new SubEntityFieldMapping(0, field, alias);
//
//                fields.put(field.getName(), subEntityField);
//                addField(subEntityField);
//                unionEntityType.addField(unionField);
//            } else {
//                addField(new CommondFieldMapping(field));
//            }
//        }

//
        this.original = original;
        final Collection<AbstractFieldMapping> items = original.getFields();
        for (AbstractFieldMapping field : items) {
            if (unionEntityType.getCommonField(field.getName()) == null) {
                String alias = FIELD_PREFIX + unionEntityType.nextFieldAlias();
                UnionEntityFieldMapping unionField = new UnionEntityFieldMapping(field, alias);
                unionEntityType.addField(unionField);

                SubEntityFieldMapping subEntityField = new SubEntityFieldMapping(unionField.getOrderNum(), field, alias);
                fields.put(field.getName(), subEntityField);
                addField(subEntityField);
            } else {
                addField(new CommondFieldMapping(field));
            }
        }
    }

    @Override
    protected void validate() {
    }

    public String getName() {
        return original.getName();
    }

    public String getTableName() {
        return original.getTableName();
    }

    public String getSql() {
        return original.getSql();
    }

    public Class getEntityClass() {
        return original.getEntityClass();
    }

    public boolean isAbstract() {
        return original.isAbstract();
    }

    public IFieldMapping getIdField() {
        return original.getIdField();
    }

    public IFieldMapping getVersionField() {
        return original.getVersionField();
    }

    public Collection<SubEntityFieldMapping> getCusomFields() {
        return Collections.<SubEntityFieldMapping>unmodifiableCollection(fields.values());
    }

    public SubEntityFieldMapping getCustomField(String name) {
        return fields.get(name);
    }

    public IRelationMapping getRelation(String name) {
        return original.getRelation(name);
    }

    public Collection<IRelationMapping> getRelations() {
        return original.getRelations();
    }
}
