package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.impl.fetcher.proxy.IProxy;
import ru.kwanza.dbtool.orm.impl.mapping.FetchMapping;
import ru.kwanza.dbtool.orm.impl.mapping.FieldMapping;
import ru.kwanza.toolbox.fieldhelper.FieldHelper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexander Guzanov
 */
class RelationValue {
    private FieldMapping idField;
    private FetchMapping fetchMapping;
    private IQuery fetchQuery;
    private FieldHelper.Field relationField;

    RelationValue(FieldMapping idField, FetchMapping fetchMapping, IQuery fetchQuery) {
        this.idField = idField;
        this.fetchMapping = fetchMapping;
        this.fetchQuery = fetchQuery;
        this.relationField = new FieldHelper.Field() {
            public Object value(Object object) {
                return RelationValue.this.fetchMapping.getPropertyField().getValue(object);
            }
        };
    }

    public FetchMapping getFetchMapping() {
        return fetchMapping;
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
                    result.add(relationField.value(o));
                }
            }
        }
    }

    private boolean isWaitingForLoad(Object o) {
        final Object value = fetchMapping.getFetchField().getValue(o);
        return value == null || value instanceof IProxy;
    }

    public String getIDGroupingField() {
        return idField.getName();
    }
}
