package ru.kwanza.dbtool.orm.impl.querybuilder;

/**
 * @author Alexander Guzanov
 */
public class MSSQLStatement<T> extends StatementImpl<T> {

    private long top;

    public MSSQLStatement(QueryConfig config) {
        super(config);
    }

    @Override
    protected Object[] createParamsArray(QueryConfig<T> config, int paramsCount) {
        return new Object[paramsCount];
    }

    @Override
    protected void installPagingParams(Object[] params, int maxSize, int offset) {
        this.top = (long) maxSize + (long) offset;
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
