package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.api.internal.IEntityType;

import java.util.ArrayList;

/**
 * @author Alexander Guzanov
 */
class FromFragmentHelper {
    private AbstractQueryBuilder builder;

    FromFragmentHelper(AbstractQueryBuilder builder) {
        this.builder = builder;
    }


    String createFromFragment(Parameters holder) {
        final EntityInfo root = builder.getEntityInfoFactory().getRoot();
        final StringBuilder fromPart = new StringBuilder();
        if (root.getEntityType().getSql() != null) {
            fromPart.append('(').append(root.getEntityType().getSql()).append(") ");
        }

        fromPart.append(root.getAlias());

        if (root != null) {
            processJoin(fromPart, root, holder);
        }
        return fromPart.toString();
    }

    private void processJoin(StringBuilder fromPart, EntityInfo root, Parameters holder) {
        if (root.hasJoins()) {
            for (EntityInfo entityInfo : root.getJoins().values()) {
                final Class relationClass = entityInfo.getRelationMapping().getRelationClass();
                StringBuilder extConditionPart = null;
                Parameters joinHolder = null;
                if (entityInfo.getRelationMapping().getCondition() != null) {
                    joinHolder = new Parameters();
                    extConditionPart = new StringBuilder();
                    builder.getWhereFragmentHelper()
                            .createConditionString(entityInfo, entityInfo.getRelationMapping().getCondition(), extConditionPart,
                                    joinHolder);
                }
                fromPart.append(entityInfo.getJoinType() == Join.Type.LEFT ? " LEFT JOIN " : " INNER JOIN ");
                final IEntityType entityType = builder.getRegistry().getEntityType(relationClass);
                if (entityInfo.hasJoins()) {
                    fromPart.append('(').append(getTableName(entityType)).append(' ').append(entityInfo.getAlias());
                    processJoin(fromPart, entityInfo, holder);
                    fromPart.append(')');
                } else {
                    fromPart.append(getTableName(entityType)).append(' ').append(entityInfo.getAlias());
                }

                fromPart.append(" ON ").append(root.getAlias() == null
                        ? builder.getRegistry().getEntityType(builder.getEntityClass()).getTableName()
                        : root.getAlias()).append('.').append(entityInfo.getRelationMapping().getKeyMapping().getColumn()).append('=')
                        .append(entityInfo.getAlias()).append('.')
                        .append(entityInfo.getRelationMapping().getRelationKeyMapping().getColumn()).append(' ');
                if (extConditionPart != null) {
                    fromPart.append(" AND (").append(extConditionPart).append(')');
                    holder.join(joinHolder);
                }
            }
        }
    }


}
