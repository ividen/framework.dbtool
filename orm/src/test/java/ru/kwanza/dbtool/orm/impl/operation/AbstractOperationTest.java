package ru.kwanza.dbtool.orm.impl.operation;

import org.dbunit.DBTestCase;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.SortedDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

/**
 * @author Kiryl Karatsetski
 */
public abstract class AbstractOperationTest extends DBTestCase {

    protected ApplicationContext applicationContext;

    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(this.getClass().getResourceAsStream("./data/data_set.xml"));
    }

    @Override
    protected void setUp() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext(getSpringConfigFile(), AbstractOperationTest.class);
        DatabaseOperation.CLEAN_INSERT.execute(getConnection(), getDataSet());
        getEntityMappingRegistry().registerEntityClass(TestEntity.class);
        getEntityMappingRegistry().registerEntityClass(TestEntityVersion.class);
    }

    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        config.setProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);
    }

    protected abstract String getSpringConfigFile();

    protected IDataSet getResourceSet(String fileName) throws DataSetException {
        return new SortedDataSet(new FlatXmlDataSetBuilder().build(this.getClass().getResourceAsStream(fileName)));
    }

    protected TransactionDefinition getTxDef() {
        final DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setName("SomeTxName");
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return definition;
    }

    protected PlatformTransactionManager getTxManager() {
        return (PlatformTransactionManager) applicationContext.getBean("txManager");
    }

    protected IDataSet getActualDataSet() throws Exception {
        return new SortedDataSet(getConnection().createDataSet(new String[]{"test_table"}));
    }

    protected IEntityMappingRegistry getEntityMappingRegistry() {
        return applicationContext.getBean(IEntityMappingRegistry.class);
    }

    protected IEntityManager getEntityManager() {
        return applicationContext.getBean(IEntityManager.class);
    }

    protected DBTool getDBTool() {
        return applicationContext.getBean(DBTool.class);
    }
}

