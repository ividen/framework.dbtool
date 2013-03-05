package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.api.IStatement;

/**
 * @author Alexander Guzanov
 */
public class QueryImpl<T> implements IQuery<T> {
    private QueryConfig config;


    public QueryImpl(QueryConfig<T> config) {
        this.config = config;
    }

    public IStatement<T> prepare() {
        return new StatementImpl<T>(config);
    }


    public QueryConfig getConfig() {
        return config;
    }

    @Override
    public String toString() {
        return "Query{" +
                "query='" + config.getSql() + '\'' +
                '}';
    }


}
