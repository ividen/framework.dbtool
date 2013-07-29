package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQueryBuilder;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

/**
 * @author Alexander Guzanov
 */
public class QueryBuilderFactory {

    public static  <T> IQueryBuilder<T> createBuilder(DBTool dbTool, IEntityMappingRegistry mappingRegistry, Class<T> entityClass) {
        DBTool.DBType dbType = dbTool.getDbType();
        if (dbType == DBTool.DBType.ORACLE) {
            return new OracleQueryBuilder<T>(dbTool, mappingRegistry, entityClass);
        } else if (dbType == DBTool.DBType.MYSQL) {
            return new MySQLQueryBuilder<T>(dbTool, mappingRegistry, entityClass);
        } else if (dbType == DBTool.DBType.MSSQL) {
            return new MSSQLQueryBuilder<T>(dbTool, mappingRegistry, entityClass);
        } else if (dbType == DBTool.DBType.POSTGRESQL) {
            return new PostgreSQLQueryBuilder<T>(dbTool, mappingRegistry, entityClass);
        } else {
            throw new RuntimeException("Unsupported database type!");
        }
    }
}
