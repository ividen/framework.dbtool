package ru.kwanza.dbtool.orm.impl.fetcher;

/**
 * @author Alexander Guzanov
 */
public class FetchEntry {
    private Class entityClass;
    private String fetchQuery;

    public FetchEntry(Class entityClass, String fetchQuery) {
        this.entityClass = entityClass;
        this.fetchQuery = fetchQuery;
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public String getFetchQuery() {
        return fetchQuery;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FetchEntry that = (FetchEntry) o;

        if (!entityClass.equals(that.entityClass)) {
            return false;
        }
        if (!fetchQuery.equals(that.fetchQuery)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = entityClass.hashCode();
        result = 31 * result + fetchQuery.hashCode();
        return result;
    }
}
