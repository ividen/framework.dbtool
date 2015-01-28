package ru.kwanza.dbtool.core;

/*
 * #%L
 * dbtool-core
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

import org.dbunit.IOperationListener;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;

/**
 * @author Alexander Guzanov
 */
public class ConnectionConfigListener implements IOperationListener {
    private DBTool.DBType type;

    public ConnectionConfigListener(DBTool.DBType type) {
        this.type = type;
    }

    public ConnectionConfigListener() {
    }

    public void connectionRetrieved(IDatabaseConnection connection) {
        connection.getConfig().setProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_FETCH_SIZE, 1000);
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_BATCH_SIZE, 1000);
        if(type== DBTool.DBType.ORACLE){
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,new org.dbunit.ext.oracle.OracleDataTypeFactory());
        }else if(type== DBTool.DBType.MSSQL){
            connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,new  org.dbunit.ext.mssql.MsSqlDataTypeFactory());
        }else if(type== DBTool.DBType.MYSQL){
            connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,new org.dbunit.ext.mysql.MySqlDataTypeFactory());
        }else if(type== DBTool.DBType.POSTGRESQL){
            connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,new org.dbunit.ext.postgresql.PostgresqlDataTypeFactory());
        }

    }

    public void operationSetUpFinished(IDatabaseConnection connection) {

    }

    public void operationTearDownFinished(IDatabaseConnection connection) {

    }
}
