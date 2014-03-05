package ru.kwanza.dbtool.orm.impl.lockoperation;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.SortedDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Value;
import ru.kwanza.dbtool.orm.impl.fetcher.TestFetcherIml;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingRegistry;
import ru.kwanza.txn.api.spi.ITransactionManager;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author Alexander Guzanov
 */

public class InitBean {
    @Resource(name = "dbtool.IEntityMappingRegistry")
    private EntityMappingRegistry registry;
    @Resource(name = "dataSource")
    private DataSource dataSource;
    @Resource(name = "txn.ITransactionManager")
    protected ITransactionManager tm;

    @Value("${jdbc.schema}")
    private String schema;

    @PostConstruct
    public void setUpDV() throws Exception {
        tm.begin();
        IDatabaseConnection connection = getConnection();
        DatabaseOperation.CLEAN_INSERT.execute(connection, getInitDataSet());
        connection.getConnection().close();
        tm.commit();
    }

    private static IDataSet getDataSet() throws IOException,
            DataSetException {
        return new FlatXmlDataSetBuilder().build(TestFetcherIml.class.getResourceAsStream("initdb.xml"));
    }


    public IDataSet getActualDataSet() throws Exception {
        return new SortedDataSet(getConnection().createDataSet(new String[]{"test_entity"}));
    }


    private IDataSet getInitDataSet() throws IOException,
            DataSetException {

        return new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream("initdb.xml"));
    }

    public IDatabaseConnection getConnection() throws SQLException, DatabaseUnitException {
        DatabaseConnection connection = new DatabaseConnection(dataSource.getConnection(), schema);
        connection.getConfig().setProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);
        return connection;
    }


}
