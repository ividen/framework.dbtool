package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;

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

    EntityInfo registerInfo(EntityInfo root, Join.Type type, String propertyName) {
        EntityInfo entityInfo = root.getChild(propertyName);

        if (entityInfo != null) {
            if(type!=entityInfo.getJoinType() && entityInfo.getJoinType()== Join.Type.FETCH){
                entityInfo.setJoinType(type);
            }

            return entityInfo;
        }

        Class entityClass = root.isRoot() ? builder.getEntityClass() : root.getRelationMapping().getRelationClass();

        final IEntityType entityType = builder.getRegistry().getEntityType(entityClass);
        final IRelationMapping relationMapping = entityType.getRelation(propertyName);

        if (relationMapping == null) {
            throw new IllegalArgumentException("Wrong relation name for " + entityClass.getName() + " : " + propertyName + " !");
        }

        entityInfo = new EntityInfo(type,entityType, "t" + aliasCounter++, relationMapping);
        root.addChild(propertyName, entityInfo);
        return entityInfo;
    }
}
