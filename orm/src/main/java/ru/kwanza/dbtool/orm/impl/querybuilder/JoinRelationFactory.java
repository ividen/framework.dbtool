package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.impl.mapping.FetchMapping;

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

    JoinRelation createJoinRelation(JoinRelation root, Join.Type type, String propertyName) {
        JoinRelation joinRelation;

        Class entityClass = root.isRoot() ? builder.getEntityClass() : root.getFetchMapping().getRelationClass();

        final FetchMapping fetchMapping = builder.getRegistry().getFetchMappingByPropertyName(entityClass, propertyName);
        if (fetchMapping == null) {
            throw new IllegalArgumentException("Wrong relation name for " + entityClass.getName() + " : " + propertyName + " !");
        }

        joinRelation = new JoinRelation(type, "t" + aliasCounter++, fetchMapping);
        root.addChild(propertyName, joinRelation);
        return joinRelation;
    }
}
