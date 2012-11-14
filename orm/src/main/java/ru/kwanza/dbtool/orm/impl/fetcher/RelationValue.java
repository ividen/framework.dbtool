package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.IQuery;
import ru.kwanza.dbtool.orm.mapping.FetchMapping;
import ru.kwanza.dbtool.orm.mapping.FieldMapping;

/**
* @author Alexander Guzanov
*/
class RelationValue {
    private FieldMapping idField;
    private FetchMapping fetchMapping;
    private IQuery fetchQuery;

    RelationValue(FieldMapping idField,FetchMapping fetchMapping, IQuery fetchQuery) {
        this.idField = idField;
        this.fetchMapping = fetchMapping;
        this.fetchQuery = fetchQuery;
    }

    public FieldMapping getIdField() {
        return idField;
    }

    public FetchMapping getFetchMapping() {
        return fetchMapping;
    }

    public IQuery getFetchQuery() {
        return fetchQuery;
    }
}
