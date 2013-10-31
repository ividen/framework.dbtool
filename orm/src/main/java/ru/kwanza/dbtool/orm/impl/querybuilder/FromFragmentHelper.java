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

    static class Result {
        String sqlPart;
        ArrayList<EntityInfo> fetchEntities;

        Result(String sqlPart, ArrayList<EntityInfo> fetchEntities) {
            this.sqlPart = sqlPart;
            this.fetchEntities = fetchEntities;
        }
    }

    Result createFromFragment(Parameters holder) {
        final EntityInfo rootRelations = builder.getEntityInfoFactory().getRoot();
        final StringBuilder fromPart = new StringBuilder();
        if (rootRelations.getEntityType().getSql() != null) {
            fromPart.append('(').append(rootRelations.getEntityType().getSql()).append(") ");
        }

        fromPart.append(rootRelations.getAlias());

        ArrayList<EntityInfo> fetchEntities = new ArrayList<EntityInfo>();

        if (rootRelations != null) {
            processJoinRelation(fromPart, rootRelations, holder, fetchEntities);
        }
        return new Result(fromPart.toString(), fetchEntities);
    }

    private void processJoinRelation(StringBuilder fromPart, EntityInfo root, Parameters holder, ArrayList<EntityInfo> fetchEntities) {
        if (root.hasChilds()) {
            for (EntityInfo entityInfo : root.getAllChilds().values()) {
                if (entityInfo.getJoinType() == Join.Type.FETCH) {
                    fetchEntities.add(entityInfo);
                    continue;
                }
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
                if (entityInfo.hasChilds()) {
                    fromPart.append('(').append(getTableName(entityType)).append(' ').append(entityInfo.getAlias());
                    processJoinRelation(fromPart, entityInfo, holder, fetchEntities);
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

    private String getTableName(IEntityType entityType) {
        final String sql = entityType.getSql();
        return sql == null ? entityType.getTableName() : "(" + sql + ") ";
    }
}
