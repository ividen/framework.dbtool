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

/**
 * @author Alexander Guzanov
 */
class FromFragmentHelper {
    private AbstractQueryBuilder builder;

    FromFragmentHelper(AbstractQueryBuilder builder) {
        this.builder = builder;
    }


    String createFromFragment(Parameters holder) {
        final QueryMapping root = builder.getQueryMappingFactory().getRoot();
        final StringBuilder fromPart = new StringBuilder();

        fromPart.append(root.getTableWithAlias());

        if (root != null) {
            processJoin(fromPart, root, holder);
        }
        return fromPart.toString();
    }


    private void processJoin(StringBuilder fromPart, QueryMapping root, Parameters holder) {
        if (root.hasJoins()) {
            for (QueryMapping queryMapping : root.getJoins().values()) {
                final Class relationClass = queryMapping.getRelationMapping().getRelationClass();
                StringBuilder extConditionPart = null;
                Parameters joinHolder = null;
                if (queryMapping.getRelationMapping().getCondition() != null) {
                    joinHolder = new Parameters();
                    extConditionPart = new StringBuilder();
                    builder.getWhereFragmentHelper()
                            .createConditionString(queryMapping, queryMapping.getRelationMapping().getCondition(), extConditionPart,
                                    joinHolder);
                }
                fromPart.append(queryMapping.getJoinType() == Join.Type.LEFT ? " LEFT JOIN " : " INNER JOIN ");
                if (queryMapping.hasJoins()) {
                    fromPart.append('(').append(queryMapping.getTableWithAlias());
                    processJoin(fromPart, queryMapping, holder);
                    fromPart.append(')');
                } else {
                    fromPart.append(queryMapping.getTableWithAlias());
                }

                fromPart.append(" ON ").append(root.getAlias() == null
                        ? builder.getRegistry().getEntityType(builder.getEntityClass()).getTableName()
                        : root.getAlias()).append('.').append(queryMapping.getRelationMapping().getKeyMapping().getColumn()).append('=')
                        .append(queryMapping.getAlias()).append('.')
                        .append(queryMapping.getRelationMapping().getRelationKeyMapping().getColumn()).append(' ');
                if (extConditionPart != null) {
                    fromPart.append(" AND (").append(extConditionPart).append(')');
                    holder.join(joinHolder);
                }
            }
        }
    }


}
