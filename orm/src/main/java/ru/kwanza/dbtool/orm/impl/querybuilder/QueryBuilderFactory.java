package ru.kwanza.dbtool.orm.impl.querybuilder;

/*
 * #%L
 * dbtool-orm
 * %%
 * Copyright (C) 2015 Kwanza
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQueryBuilder;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.querybuilder.db.h2.H2QueryBuilder;
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
        } else if (dbType == DBTool.DBType.H2) {
            return new H2QueryBuilder<T>(em, entityClass);
        } else {
            throw new RuntimeException("Unsupported database type!");
        }
    }
}
