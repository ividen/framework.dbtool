package ru.kwanza.dbtool.orm.impl.querybuilder;

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
        final JoinRelation rootRelations = builder.getRelationFactory().getRoot();
        final StringBuilder fromPart = new StringBuilder();
        if (rootRelations.getEntityType().getSql() != null) {
            fromPart.append('(').append(rootRelations.getEntityType().getSql()).append(") ");
        }

        fromPart.append(rootRelations.getAlias());

        if (rootRelations != null) {
            processJoinRelation(fromPart, rootRelations, holder);
        }
        return fromPart.toString();
    }

    private void processJoinRelation(StringBuilder fromPart, JoinRelation rootRelations, Parameters holder) {
        if (rootRelations.hasChilds()) {
            for (JoinRelation joinRelation : rootRelations.getAllChilds().values()) {
                final Class relationClass = joinRelation.getRelationMapping().getRelationClass();
                StringBuilder extConditionPart = null;
                Parameters joinHolder = null;
                if (joinRelation.getRelationMapping().getCondition() != null) {
                    joinHolder = new Parameters();
                    extConditionPart = new StringBuilder();
                    builder.getWhereFragmentHelper()
                            .createConditionString(joinRelation, joinRelation.getRelationMapping().getCondition(), extConditionPart,
                                    joinHolder);
                }
                fromPart.append(joinRelation.getType() == Join.Type.LEFT ? " LEFT JOIN " : " INNER JOIN ");
                if (joinRelation.hasChilds()) {
                    fromPart.append('(').append(builder.getRegistry().getEntityType(relationClass).getTableName()).append(' ')
                            .append(joinRelation.getAlias());
                    processJoinRelation(fromPart, joinRelation, holder);
                    fromPart.append(')');
                } else {
                    fromPart.append(builder.getRegistry().getEntityType(relationClass).getTableName()).append(' ')
                            .append(joinRelation.getAlias());
                }

                fromPart.append(" ON ")
                        .append(rootRelations.getAlias() == null ? builder.getRegistry().getEntityType(builder.getEntityClass())
                                .getTableName() : rootRelations.getAlias()).append('.')
                        .append(joinRelation.getRelationMapping().getKeyMapping().getColumn()).append('=').append(joinRelation.getAlias())
                        .append('.').append(joinRelation.getRelationMapping().getRelationKeyMapping().getColumn()).append(' ');
                if (extConditionPart != null) {
                    fromPart.append(" AND (").append(extConditionPart).append(')');
                    holder.join(joinHolder);
                }
            }
        }
    }
}
