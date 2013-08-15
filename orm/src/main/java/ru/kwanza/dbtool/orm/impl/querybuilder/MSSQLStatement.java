package ru.kwanza.dbtool.orm.impl.querybuilder;

/**
 * @author Alexander Guzanov
 */
public class MSSQLStatement<T> extends StatementImpl<T> {
    private Long top;

    public MSSQLStatement(QueryConfig config, Integer offset, Integer maxSize) {
        super(config, offset, maxSize);
    }

    @Override
    protected Object[] createParamsArray(QueryConfig<T> config, int paramsCount, Integer maxSize, Integer offset) {
        if (getConfig().isUsePaging()) {
            this.top = (long)maxSize + (long)offset;
        }
        return new Object[paramsCount];
    }

    @Override
    protected String prepareSql() {
        String sql = getConfig().getSql();
        if (getConfig().isUsePaging()) {
            sql = MSSQLQueryBuilder.replaceTop(sql, top);
        }

        return sql;
    }
}
