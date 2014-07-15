package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;

/**
 * @author Alexander Guzanov
 */
class QueryMappingFactory {
    private final AbstractQueryBuilder builder;
    private final QueryMapping root;
    private int aliasCounter;

    QueryMappingFactory(AbstractQueryBuilder builder) {
        this.builder = builder;
        this.aliasCounter = 1;
        this.root = new QueryMapping.QueryRootMapping(builder.getRegistry().getEntityType(builder.getEntityClass()));
    }

    QueryMapping getRoot() {
        return root;
    }

    QueryMapping registerInfo(QueryMapping root, Join join) {
        QueryMapping queryMapping = root.getJoin(join.getPropertyName());

        if (queryMapping == null) {
            queryMapping = doRegister(root, join);
        }

        if (queryMapping != null) {
            for (Join subJoin : queryMapping.getJoin().getSubJoins()) {
                registerInfo(queryMapping, subJoin);
            }
        }

        return queryMapping;
    }

    private QueryMapping doRegister(QueryMapping root, Join join) {
        if (join.getType() == Join.Type.FETCH) {
            root.addFetch(join);
            return null;
        } else {
            Class entityClass = root.isRoot() ? builder.getEntityClass() : root.getRelationMapping().getRelationClass();
            final IEntityType entityType = builder.getRegistry().getEntityType(entityClass);
            final IRelationMapping relationMapping = entityType.getRelation(join.getPropertyName());

            if (relationMapping == null) {
                throw new IllegalArgumentException(
                        "Wrong relation name for " + entityClass.getName() + " : " + join.getPropertyName() + " !");
            }

            QueryMapping queryMapping = new QueryMapping(join,
                    builder.getRegistry().getEntityType(relationMapping.getRelationClass()), "t" + aliasCounter++, relationMapping);
            root.addJoin(join.getPropertyName(), queryMapping);
            return queryMapping;
        }
    }
}
