package ru.kwanza.dbtool.core;

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
