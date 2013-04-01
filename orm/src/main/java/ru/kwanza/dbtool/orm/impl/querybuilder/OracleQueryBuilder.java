package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import java.util.List;

/**
 * @author Alexander Guzanov
 */
public class OracleQueryBuilder<T> extends AbstractQueryBuilder<T> {

    public OracleQueryBuilder(DBTool dbTool, IEntityMappingRegistry registry, Class entityClass) {
        super(dbTool, registry, entityClass);
    }

    protected IQuery<T> createQuery(List<Integer> paramsTypes, String sqlString) {
        return new OracleQuery<T>(new QueryConfig<T>(dbTool, registry, entityClass,
                sqlString, maxSize, offset, paramsTypes, namedParams));
    }

    protected StringBuilder createSQLString(String conditions, String orderBy, String fieldsString) {
        StringBuilder sql;
        if (maxSize != null) {
            sql = new StringBuilder("SELECT  * FROM (")
                    .append(createDefaultSQLString(fieldsString, conditions, orderBy))
                    .append(") WHERE rownum <= ?");
        } else {
            sql = createDefaultSQLString(fieldsString, conditions, orderBy);
        }
        return sql;
    }


}
