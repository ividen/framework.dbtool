package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.IStatement;

/**
 * @author Michael Yeskov
 */
public class PostgreSQLQuery<T> extends AbstractQuery<T> {
    public PostgreSQLQuery(QueryConfig<T> config) {
        super(config);
    }

    @Override
    protected IStatement<T> doPrepare(QueryConfig config, Integer offset, Integer maxSize) {
        return new PostgreSQLStatement<T>(config,offset,maxSize);
    }
}
