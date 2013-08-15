package ru.kwanza.dbtool.orm.impl.querybuilder;

/**
 * @author Alexander Guzanov
 */
public class MySQLStatement<T> extends StatementImpl<T> {
    public MySQLStatement(QueryConfig<T> config, Integer offset, Integer maxSize) {
        super(config,offset,maxSize);
    }

    @Override
    protected Object[] createParamsArray(QueryConfig<T> config, int paramsCount, Integer maxSize, Integer offset) {
        Object[] result;
        if (maxSize != null) {
            int size = paramsCount + 1;
            if (offset != null) {
                size++;
            }

            result = new Object[size];

            if (offset != null) {
                result[size - 2] = offset;
            }

            result[size - 1] = maxSize;

        } else {
            result = new Object[paramsCount];
        }
        return result;
    }
}
