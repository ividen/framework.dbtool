package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.core.DBTool;

/**
 * @author Alexander Guzanov
 */
public class MSSQLStatement<T> extends StatementImpl<T> {
    public MSSQLStatement(QueryConfig<T> config) {
        super(config);
    }

    @Override
    protected Object[] createParamsArray(QueryConfig<T> config, int paramsCount, Integer maxSize, Integer offset) {
        return new Object[paramsCount];
    }
}
