package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.api.IStatement;

/**
 * @author Alexander Guzanov
 */
public abstract class AbstractQuery<T> implements IQuery<T> {
    protected QueryConfig config;


    public AbstractQuery(QueryConfig<T> config) {
        this.config = config;
    }

    public abstract IStatement<T> prepare();


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
