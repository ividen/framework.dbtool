package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQueryBuilder;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.querybuilder.db.mssql.MSSQLQueryBuilder;
import ru.kwanza.dbtool.orm.impl.querybuilder.db.mysql.MySQLQueryBuilder;
import ru.kwanza.dbtool.orm.impl.querybuilder.db.oracle.OracleQueryBuilder;
import ru.kwanza.dbtool.orm.impl.querybuilder.db.postgresql.PostgreSQLQueryBuilder;

/**
 * @author Alexander Guzanov
 */
public abstract class QueryBuilderFactory {

    public static <T> IQueryBuilder<T> createBuilder(EntityManagerImpl em, Class<T> entityClass) {
        DBTool.DBType dbType = em.getDbTool().getDbType();
        if (dbType == DBTool.DBType.ORACLE) {
            return new OracleQueryBuilder<T>(em, entityClass);
        } else if (dbType == DBTool.DBType.MYSQL) {
            return new MySQLQueryBuilder<T>(em, entityClass);
        } else if (dbType == DBTool.DBType.MSSQL) {
            return new MSSQLQueryBuilder<T>(em, entityClass);
        } else if (dbType == DBTool.DBType.POSTGRESQL) {
            return new PostgreSQLQueryBuilder<T>(em, entityClass);
        } else {
            throw new RuntimeException("Unsupported database type!");
        }
    }
}
