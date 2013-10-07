package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.impl.mapping.RelationMapping;

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

        this.root = new JoinRelation(builder.getRegistry().getTableName(builder.getEntityClass()));
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

        final RelationMapping relationMapping = builder.getRegistry().getFetchMappingByPropertyName(entityClass, propertyName);
        if (relationMapping == null) {
            throw new IllegalArgumentException("Wrong relation name for " + entityClass.getName() + " : " + propertyName + " !");
        }

        joinRelation = new JoinRelation(type, "t" + aliasCounter++, relationMapping);
        root.addChild(propertyName, joinRelation);
        return joinRelation;
    }
}
