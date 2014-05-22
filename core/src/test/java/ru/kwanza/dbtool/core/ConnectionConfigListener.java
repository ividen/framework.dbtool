package ru.kwanza.dbtool.core;

import org.dbunit.IOperationListener;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;

/**
 * @author Alexander Guzanov
 */
public class ConnectionConfigListener implements IOperationListener {
    public void connectionRetrieved(IDatabaseConnection connection) {
        connection.getConfig().setProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_FETCH_SIZE, 1000);
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_BATCH_SIZE, 1000);
    }

    public void operationSetUpFinished(IDatabaseConnection connection) {

    }

    public void operationTearDownFinished(IDatabaseConnection connection) {

    }
}
