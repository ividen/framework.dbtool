package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;

import java.util.ArrayList;

/**
 * @author Alexander Guzanov
 */
class EntityInfoFactory {
    private final AbstractQueryBuilder builder;
    private final EntityInfo root;
    private int aliasCounter;

    EntityInfoFactory(AbstractQueryBuilder builder) {
        this.builder = builder;
        this.aliasCounter = 1;

        this.root = new EntityInfo(builder.getRegistry().getEntityType(builder.getEntityClass()));
    }

    EntityInfo getRoot() {
        return root;
    }

    EntityInfo registerInfo(EntityInfo root, Join join) {
        EntityInfo entityInfo = root.getChild(join.getPropertyName());

        if (entityInfo != null) {
            if (join.getType() != entityInfo.getJoinType() && entityInfo.getJoinType() == Join.Type.FETCH) {
                ArrayList<Join> subJoins = new ArrayList<Join>();
                subJoins.addAll(join.getSubJoins());
                subJoins.addAll(entityInfo.getJoin().getSubJoins());
                Join newJoin = join.getType() == Join.Type.LEFT
                        ? Join.left(join.getPropertyName(), subJoins)
                        : Join.inner(join.getPropertyName(), subJoins);

                entityInfo = doRegister(root, newJoin);
                for (Join subJoin : subJoins) {
                    registerInfo(entityInfo, subJoin);
                }
            }

            return entityInfo;
        } else {
            entityInfo = doRegister(root, join);
        }

        if (entityInfo.getJoinType() != Join.Type.FETCH) {
            for (Join subJoin : entityInfo.getJoin().getSubJoins()) {
                registerInfo(entityInfo, subJoin);
            }
        }

        return entityInfo;
    }

    private EntityInfo doRegister(EntityInfo root, Join join) {
        EntityInfo entityInfo;
        Class entityClass = root.isRoot() ? builder.getEntityClass() : root.getRelationMapping().getRelationClass();

        final IEntityType entityType = builder.getRegistry().getEntityType(entityClass);
        final IRelationMapping relationMapping = entityType.getRelation(join.getPropertyName());

        if (relationMapping == null) {
            throw new IllegalArgumentException("Wrong relation name for " + entityClass.getName() + " : " + join.getPropertyName() + " !");
        }

        entityInfo = new EntityInfo(join, entityType, "t" + aliasCounter++, relationMapping);
        root.addChild(join.getPropertyName(), entityInfo);
        return entityInfo;
    }
}
