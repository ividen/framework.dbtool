package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQueryBuilder;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.querybuilder.db.mssql.MSSQLQueryBuilder;
import ru.kwanza.dbtool.orm.impl.querybuilder.db.mysql.MySQLQueryBuilder;
import ru.kwanza.dbtool.orm.impl.querybuilder.db.oracle.OracleQueryBuilder;
import ru.kwanza.dbtool.orm.impl.querybuilder.db.postgresql.PostgreSQLQueryBuilder;

/**
 * @author Alexander Guzanov
 */
public abstract class QueryBuilderFactory {

    public static <T> IQueryBuilder<T> createBuilder(DBTool dbTool, IEntityMappingRegistry mappingRegistry, Class<T> entityClass) {
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
