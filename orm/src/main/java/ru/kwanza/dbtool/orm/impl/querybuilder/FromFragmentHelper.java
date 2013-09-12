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

     String createFromFragment() {
        final JoinRelation rootRelations = builder.getRelationFactory().getRoot();
        final StringBuilder fromPart = new StringBuilder(rootRelations.getAlias());

        if (rootRelations != null) {
            processJoinRelation(fromPart, rootRelations);
        }
        return fromPart.toString();
    }

    private void processJoinRelation(StringBuilder fromPart, JoinRelation rootRelations) {
        if (rootRelations.getAllChilds() != null) {
            for (JoinRelation joinRelation : rootRelations.getAllChilds().values()) {
                final Class relationClass = joinRelation.getFetchMapping().getRelationClass();
                fromPart.append(joinRelation.getType() == Join.Type.LEFT ? "\n\tLEFT JOIN " : "\n\tINNER JOIN ")
                        .append(builder.getRegistry().getTableName(relationClass)).append(' ').append(joinRelation.getAlias())
                        .append(" ON ").append(rootRelations.getAlias() == null
                        ? builder.getRegistry().getTableName(builder.getEntityClass())
                        : rootRelations.getAlias()).append('.').append(joinRelation.getFetchMapping().getPropertyMapping().getColumn())
                        .append('=').append(joinRelation.getAlias()).append('.')
                        .append(joinRelation.getFetchMapping().getRelationPropertyMapping().getColumn());

                processJoinRelation(fromPart, joinRelation);
            }
        }
    }
}
