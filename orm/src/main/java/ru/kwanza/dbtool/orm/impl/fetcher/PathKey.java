package ru.kwanza.dbtool.orm.impl.fetcher;

/**
* @author Alexander Guzanov
*/
class PathKey {
    private Class entityClass;
    private String path;

    PathKey(Class entityClass, String path) {
        this.entityClass = entityClass;
        this.path = path;
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PathKey pathKey = (PathKey) o;

        if (!entityClass.equals(pathKey.entityClass)) return false;
        if (!path.equals(pathKey.path)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = entityClass.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }
}
