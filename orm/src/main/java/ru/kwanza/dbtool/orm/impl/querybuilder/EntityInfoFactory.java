package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;

/**
 * @author Alexander Guzanov
 */
class EntityInfoFactory {
    private final AbstractQueryBuilder builder;
    private final QueryEntityInfo root;
    private int aliasCounter;

    EntityInfoFactory(AbstractQueryBuilder builder) {
        this.builder = builder;
        this.aliasCounter = 1;
        this.root = new QueryEntityInfo.RootQueryEntityInfo(builder.getRegistry().getEntityType(builder.getEntityClass()));
    }

    QueryEntityInfo getRoot() {
        return root;
    }

    QueryEntityInfo registerInfo(QueryEntityInfo root, Join join) {
        QueryEntityInfo queryEntityInfo = root.getJoin(join.getPropertyName());

        if (queryEntityInfo == null) {
            queryEntityInfo = doRegister(root, join);
        }

        if (queryEntityInfo != null) {
            for (Join subJoin : queryEntityInfo.getJoin().getSubJoins()) {
                registerInfo(queryEntityInfo, subJoin);
            }
        }

        return queryEntityInfo;
    }

    private QueryEntityInfo doRegister(QueryEntityInfo root, Join join) {
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

            QueryEntityInfo queryEntityInfo = new QueryEntityInfo(join,
                    builder.getRegistry().getEntityType(relationMapping.getRelationClass()), "t" + aliasCounter++, relationMapping);
            root.addJoin(join.getPropertyName(), queryEntityInfo);
            return queryEntityInfo;
        }
    }
}
