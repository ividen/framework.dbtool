package ru.kwanza.dbtool.orm.impl.fetcher;

/**
 * @author Alexander Guzanov
 */
class RelationKey {
    private Class entityClass;
    private String relationPropertyName;

    RelationKey(Class entityClass, String relationPropertyName) {
        this.entityClass = entityClass;
        this.relationPropertyName = relationPropertyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RelationKey that = (RelationKey) o;

        if (!entityClass.equals(that.entityClass)) {
            return false;
        }
        if (!relationPropertyName.equals(that.relationPropertyName)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = entityClass.hashCode();
        result = 31 * result + relationPropertyName.hashCode();
        return result;
    }
}
