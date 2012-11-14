package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.IQuery;
import ru.kwanza.dbtool.orm.mapping.FetchMapping;

/**
* @author Alexander Guzanov
*/
class RelationValue {
    private FetchMapping fetchMapping;
    private IQuery fetchQuery;

    RelationValue(FetchMapping fetchMapping, IQuery fetchQuery) {
        this.fetchMapping = fetchMapping;
        this.fetchQuery = fetchQuery;
    }

    public FetchMapping getFetchMapping() {
        return fetchMapping;
    }

    public IQuery getFetchQuery() {
        return fetchQuery;
    }
}
