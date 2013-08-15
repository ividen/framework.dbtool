package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.IStatement;

/**
 * @author Alexander Guzanov
 */
public class MSSQLQuery<T> extends AbstractQuery<T> {
    public MSSQLQuery(QueryConfig<T> config) {
        super(config);
    }

    @Override
    protected IStatement<T> doPrepare(QueryConfig config, Integer offset, Integer maxSize) {
        return new MSSQLStatement<T>(config, offset, maxSize);
    }
}
