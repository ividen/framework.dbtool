package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

/**
 * @author Alexander Guzanov
 */
public class MSSQLQueryBuilder<T> extends AbstractQueryBuilder<T> {

    public static final String $_TOP = "$TOP$";

    public MSSQLQueryBuilder(DBTool dbTool, IEntityMappingRegistry registry, Class entityClass) {
        super(dbTool, registry, entityClass);
    }

    protected IQuery<T> createQuery(QueryConfig config) {
        return new MSSQLQuery<T>(config);
    }

    protected StringBuilder createSQLString(String fieldsString, String from, String where, String orderBy) {
        return super.createSQLString(usePaging ? " TOP " + $_TOP + " " + fieldsString : fieldsString, from, where, orderBy);
    }

    static String replaceTop(String sql, Long top) {
        return sql.replace($_TOP, String.valueOf(top));
    }
}
