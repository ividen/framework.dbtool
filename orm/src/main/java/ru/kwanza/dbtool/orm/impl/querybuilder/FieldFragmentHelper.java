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

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
class FieldFragmentHelper {
    public AbstractQueryBuilder builder;

    public FieldFragmentHelper(AbstractQueryBuilder builder) {
        this.builder = builder;
    }

    String createFieldsFragment() {
        StringBuilder result = new StringBuilder();
        processFields(0, builder.getQueryMappingFactory().getRoot(), result);
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    private int processFields(int fieldStartIndex, QueryMapping root, StringBuilder result) {
        root.setFieldStartIndex(fieldStartIndex);
        Collection<IFieldMapping> fields = root.getEntityType().getFields();
        for (IFieldMapping fm : fields) {
            result.append(root.getColumnWithAlias(fm)).append(",");
        }

        fieldStartIndex += fields.size();

        if (root.getJoins() != null) {
            for (QueryMapping queryMapping : root.getJoins().values()) {
                if (queryMapping.getJoinType() != Join.Type.FETCH) {
                    fieldStartIndex = processFields(fieldStartIndex, queryMapping, result);
                }
            }
        }

        return fieldStartIndex;
    }
}
