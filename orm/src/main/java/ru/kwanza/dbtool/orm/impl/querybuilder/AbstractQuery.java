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

    public final IStatement<T> prepare() {
        if (config.isUsePaging()) {
            throw new IllegalStateException("Query must use paging - prepare with offset and maxSize!");
        }
        return doPrepare(config, null, null);
    }

    public final IStatement<T> prepare(int offset, int maxSize) {
        if (!config.isUsePaging()) {
            throw new IllegalStateException("Query don't use paging - prepare without offset and maxSize!");
        }
        return doPrepare(config, offset, maxSize);
    }

    protected abstract IStatement<T> doPrepare(QueryConfig config, Integer offset, Integer maxSize);

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
