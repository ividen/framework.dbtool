package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.IStatement;

/**
 * @author Alexander Guzanov
 */
public class OracleQuery<T> extends AbstractQuery<T> {
    public OracleQuery(QueryConfig<T> config) {
        super(config);
    }


    @Override
    protected IStatement<T> doPrepare(QueryConfig config, Integer offset, Integer maxSize) {
        return new OracleStatement<T>(config,offset,maxSize);
    }
}
