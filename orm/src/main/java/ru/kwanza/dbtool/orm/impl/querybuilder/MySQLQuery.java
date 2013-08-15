package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.IStatement;

/**
 * @author Alexander Guzanov
 */
public class MySQLQuery<T> extends AbstractQuery<T> {
    public MySQLQuery(QueryConfig<T> config) {
        super(config);
    }


    @Override
    protected IStatement<T> doPrepare(QueryConfig config, Integer offset, Integer maxSize) {
        return new MySQLStatement<T>(config,offset,maxSize);
    }
}
