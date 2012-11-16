package ru.kwanza.dbtool.orm.impl.fetcher;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.SortedDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import ru.kwanza.dbtool.orm.entity.TestEntity1;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingRegistryImpl;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author Alexander Guzanov
 */

public class InitRegistryAndDB {
    @Resource(name = "dbtool.IEntityMappingRegistry")
    private EntityMappingRegistryImpl registry;
    @Resource(name = "dataSource")
    private DataSource dataSource;

    public void init() throws Exception {
        registry.registerEntityClass(TestEntity.class);
        registry.registerEntityClass(TestEntityA.class);
        registry.registerEntityClass(TestEntityB.class);
        registry.registerEntityClass(TestEntityC.class);
        registry.registerEntityClass(TestEntityD.class);
        registry.registerEntityClass(TestEntityE.class);
        registry.registerEntityClass(TestEntityF.class);
        registry.registerEntityClass(TestEntityG.class);
        setUpDV();
    }

    public void setUpDV() throws Exception {
        DatabaseOperation.CLEAN_INSERT.execute(getConnection(), getDataSet());
    }

    private static IDataSet getDataSet() throws IOException,
            DataSetException {
        return new FlatXmlDataSetBuilder().build(TestFetcherIml.class.getResourceAsStream("initdb.xml"));
    }

    public IDatabaseConnection getConnection() throws SQLException, DatabaseUnitException {
        return new DatabaseConnection(dataSource.getConnection());
    }


    public IDataSet getActualDataSet() throws Exception {
        return new SortedDataSet(getConnection().createDataSet(new String[]{"test_entity"}));
    }
}
