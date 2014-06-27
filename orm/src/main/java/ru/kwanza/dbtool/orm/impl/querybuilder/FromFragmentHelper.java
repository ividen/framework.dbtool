package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.api.internal.IEntityType;

/**
 * @author Alexander Guzanov
 */
class FromFragmentHelper {
    private AbstractQueryBuilder builder;

    FromFragmentHelper(AbstractQueryBuilder builder) {
        this.builder = builder;
    }


    String createFromFragment(Parameters holder) {
        final QueryEntityInfo root = builder.getEntityInfoFactory().getRoot();
        final StringBuilder fromPart = new StringBuilder();

        fromPart.append(root.getTableWithAlias());

        if (root != null) {
            processJoin(fromPart, root, holder);
        }
        return fromPart.toString();
    }


    private void processJoin(StringBuilder fromPart, QueryEntityInfo root, Parameters holder) {
        if (root.hasJoins()) {
            for (QueryEntityInfo queryEntityInfo : root.getJoins().values()) {
                final Class relationClass = queryEntityInfo.getRelationMapping().getRelationClass();
                StringBuilder extConditionPart = null;
                Parameters joinHolder = null;
                if (queryEntityInfo.getRelationMapping().getCondition() != null) {
                    joinHolder = new Parameters();
                    extConditionPart = new StringBuilder();
                    builder.getWhereFragmentHelper()
                            .createConditionString(queryEntityInfo, queryEntityInfo.getRelationMapping().getCondition(), extConditionPart,
                                    joinHolder);
                }
                fromPart.append(queryEntityInfo.getJoinType() == Join.Type.LEFT ? " LEFT JOIN " : " INNER JOIN ");
                if (queryEntityInfo.hasJoins()) {
                    fromPart.append('(').append(queryEntityInfo.getTableWithAlias());
                    processJoin(fromPart, queryEntityInfo, holder);
                    fromPart.append(')');
                } else {
                    fromPart.append(queryEntityInfo.getTableWithAlias());
                }

                fromPart.append(" ON ").append(root.getAlias() == null
                        ? builder.getRegistry().getEntityType(builder.getEntityClass()).getTableName()
                        : root.getAlias()).append('.').append(queryEntityInfo.getRelationMapping().getKeyMapping().getColumn()).append('=')
                        .append(queryEntityInfo.getAlias()).append('.')
                        .append(queryEntityInfo.getRelationMapping().getRelationKeyMapping().getColumn()).append(' ');
                if (extConditionPart != null) {
                    fromPart.append(" AND (").append(extConditionPart).append(')');
                    holder.join(joinHolder);
                }
            }
        }
    }


}