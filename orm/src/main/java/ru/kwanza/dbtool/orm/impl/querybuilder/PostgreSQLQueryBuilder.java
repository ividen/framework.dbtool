package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import java.util.List;

/**
 * @author Michael Yeskov
 */
public class PostgreSQLQueryBuilder<T> extends AbstractQueryBuilder<T> {

    public PostgreSQLQueryBuilder(DBTool dbTool, IEntityMappingRegistry registry, Class entityClass) {
        super(dbTool, registry, entityClass);
    }

    protected IQuery<T> createQuery(List<Integer> paramsTypes, String sqlString) {
        return new PostgreSQLQuery<T>(new QueryConfig<T>(dbTool, registry, entityClass,
                sqlString, maxSize, offset, paramsTypes, namedParams));
    }

    protected StringBuilder createSQLString(String conditions, String orderBy, String fieldsString) {
        StringBuilder sql = createDefaultSQLString(fieldsString, conditions, orderBy);
        if (maxSize != null) {
            sql.append(" LIMIT ?");
        }
        if (offset != null) {
            sql.append(" OFFSET ?");
        }
        return sql;
    }


}
