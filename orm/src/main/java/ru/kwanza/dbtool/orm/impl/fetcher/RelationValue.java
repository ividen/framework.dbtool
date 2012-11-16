package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.impl.mapping.FetchMapping;
import ru.kwanza.dbtool.orm.impl.mapping.FieldMapping;
import ru.kwanza.toolbox.fieldhelper.FieldHelper;

import java.util.Collection;
import java.util.Set;

/**
 * @author Alexander Guzanov
 */
class RelationValue {
    private FieldMapping idField;
    private FetchMapping fetchMapping;
    private IQuery fetchQuery;

    RelationValue(FieldMapping idField, FetchMapping fetchMapping, IQuery fetchQuery) {
        this.idField = idField;
        this.fetchMapping = fetchMapping;
        this.fetchQuery = fetchQuery;
    }

    public FetchMapping getFetchMapping() {
        return fetchMapping;
    }

    public IQuery getFetchQuery() {
        return fetchQuery;
    }


    public Set getRelationIds(Collection objs) {
        return FieldHelper.getFieldSet(objs, new FieldHelper.Field() {
            public Object value(Object object) {
                  return fetchMapping.getPropertyField().getValue(object);
            }
        });
    }

    public String getIDGroupingField() {
        return idField.getPropertyName();
    }
}
