package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;

/**
 * @author Alexander Guzanov
 */
class JoinRelationFactory {
    private final AbstractQueryBuilder builder;
    private final JoinRelation root;
    private int aliasCounter;

    JoinRelationFactory(AbstractQueryBuilder builder) {
        this.builder = builder;
        this.aliasCounter = 1;

        this.root = new JoinRelation(builder.getRegistry().getEntityType(builder.getEntityClass()));
    }

    JoinRelation getRoot() {
        return root;
    }

    JoinRelation registerRelation(JoinRelation root, Join.Type type, String propertyName) {
        JoinRelation joinRelation = root.getChild(propertyName);

        if (joinRelation != null) {
            return joinRelation;
        }

        Class entityClass = root.isRoot() ? builder.getEntityClass() : root.getRelationMapping().getRelationClass();

        final IEntityType entityType = builder.getRegistry().getEntityType(entityClass);
        final IRelationMapping relationMapping = entityType.getRelation(propertyName);

        if (relationMapping == null) {
            throw new IllegalArgumentException("Wrong relation name for " + entityClass.getName() + " : " + propertyName + " !");
        }

        joinRelation = new JoinRelation(type,entityType, "t" + aliasCounter++, relationMapping);
        root.addChild(propertyName, joinRelation);
        return joinRelation;
    }
}
