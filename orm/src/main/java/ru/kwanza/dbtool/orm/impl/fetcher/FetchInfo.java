package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;
import ru.kwanza.dbtool.orm.impl.fetcher.proxy.IProxy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexander Guzanov
 */
public class FetchInfo {
    private IFieldMapping idField;
    private IRelationMapping relationMapping;
    private IQuery fetchQuery;

    public FetchInfo(IFieldMapping idField, IRelationMapping relationMapping, IQuery fetchQuery) {
        this.idField = idField;
        this.relationMapping = relationMapping;
        this.fetchQuery = fetchQuery;
    }

    public IRelationMapping getRelationMapping() {
        return relationMapping;
    }

    public IQuery getFetchQuery() {
        return fetchQuery;
    }

    public Set getRelationIds(Collection objs) {
        HashSet result = new HashSet();
        iterate(objs, result);
        return result;
    }

    private void iterate(Collection objs, HashSet result) {
        for (Object o : objs) {
            if (o instanceof Collection) {
                iterate((Collection) o, result);
            } else {
                if (isWaitingForLoad(o)) {
                    result.add(this.relationMapping.getKeyProperty().value(o));
                }
            }
        }
    }

    private boolean isWaitingForLoad(Object o) {
        final Object value = relationMapping.getProperty().value(o);
        return value == null || value instanceof IProxy;
    }

    public String getIDGroupingField() {
        return idField.getName();
    }
}
