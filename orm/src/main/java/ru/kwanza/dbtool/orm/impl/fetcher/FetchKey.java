package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.api.Join;

import java.util.List;

/**
 * @author Alexander Guzanov
 */
public class FetchKey {
    private Class entityClass;
    private String encodedJoins;
    private List<Join> joins;

    public FetchKey(Class entityClass, List<Join> joins) {
        this.entityClass = entityClass;
        StringBuilder result = new StringBuilder();
        for (Join join : joins) {
            encode(join, result);
        }

        this.encodedJoins = result.toString();
        this.joins = joins;
    }

    private static void encode(Join join, StringBuilder result) {
        result.append(join.getType().name()).append('(').append(join.getPropertyName());
        for (Join j : join.getSubJoins()) {
//            if (j.getType() != Join.Type.FETCH) {
                encode(j, result);
//            }
        }

        result.append(')');
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public List<Join> getJoins() {
        return joins;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FetchKey fetchKey = (FetchKey) o;

        if (!encodedJoins.equals(fetchKey.encodedJoins)) {
            return false;
        }
        if (!entityClass.equals(fetchKey.entityClass)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = entityClass.hashCode();
        result = 31 * result + encodedJoins.hashCode();
        return result;
    }
}
