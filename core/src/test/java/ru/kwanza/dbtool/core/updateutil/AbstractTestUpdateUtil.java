package ru.kwanza.dbtool.core.updateutil;

import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.SortedDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.kwanza.dbtool.core.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Guzanov Alexander
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractTestUpdateUtil extends AbstractTransactionalJUnit4SpringContextTests {
    @Resource(name = "dbtool.DBTool")
    protected DBTool dbTool;
    @Resource(name = "dbTester")
    protected IDatabaseTester dbTester;
    @Resource(name = "transactionManager")
    protected PlatformTransactionManager tm;

    public static final TestEntityRowMapper LIST_ROW_MAPPER = new TestEntityRowMapper();
    public static final TestEntityUpdateSetter1 TEST_BATCHER_1 = new TestEntityUpdateSetter1();
    public static final TestEntityUpdateSetter2 TEST_BATCHER_2 = new TestEntityUpdateSetter2();
    public static final TestEntityUpdateSetter3 TEST_BATCHER_3 = new TestEntityUpdateSetter3();
    public static final TestEntityUpdateSetter6 TEST_BATCHER_6 = new TestEntityUpdateSetter6();
    public static final KeyVersionRowMapper KEY_VERSION_MAPPER = new KeyVersionRowMapper();

    private static final class KeyVersionRowMapper implements RowMapper<KeyValue<Integer, Integer>> {
        public KeyValue<Integer, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new KeyValue<Integer, Integer>(rs.getInt("xkey"), rs.getInt("version"));
        }
    }

    protected static final class TestEntityRowMapper implements RowMapper<TestEntity> {
        public TestEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TestEntity(rs.getInt("xkey"), rs.getString("name"), rs.getInt("version"));
        }
    }

    protected static class TestEntityUpdateSetter1 implements UpdateSetter<TestEntity> {
        public boolean setValues(PreparedStatement pst, TestEntity object) throws SQLException {
            FieldSetter.setInt(pst, 1, object.getVersion());
            FieldSetter.setInt(pst, 2, object.getKey());
            return true;
        }
    }

    protected static class TestEntityUpdateSetter2 implements UpdateSetter<TestEntity> {
        public boolean setValues(PreparedStatement pst, TestEntity object) throws SQLException {
            FieldSetter.setInt(pst, 1, object.getVersion());
            FieldSetter.setString(pst, 2, object.getName());
            FieldSetter.setInt(pst, 3, object.getKey());
            return true;
        }
    }

    protected static class TestEntityUpdateSetter3 implements UpdateSetter<TestEntity> {
        public boolean setValues(PreparedStatement pst, TestEntity object) throws SQLException {
            FieldSetter.setInt(pst, 3, object.getVersion());
            FieldSetter.setString(pst, 2, object.getName());
            FieldSetter.setInt(pst, 1, object.getKey());
            return true;
        }
    }

    protected static class TestEntityUpdateSetter6 implements UpdateSetterWithVersion<TestEntity, Integer> {
        public boolean setValues(PreparedStatement pst, TestEntity object, Integer newVersion, Integer oldVersion) throws SQLException {
            FieldSetter.setInt(pst, 1, newVersion);
            FieldSetter.setString(pst, 2, object.getName());
            FieldSetter.setInt(pst, 3, object.getKey());
            FieldSetter.setInt(pst, 4, oldVersion);
            return true;
        }
    }

    protected static class TestEntityUpdateSetter4 implements UpdateSetter<TestEntity> {
        private List<Integer> skipList;
        private List<TestEntity> objList;

        protected TestEntityUpdateSetter4(List<TestEntity> list, Integer... skip) {
            this.objList = list;
            skipList = new ArrayList<Integer>();
            if (skip != null) {
                for (Integer s : skip) {
                    skipList.add(s);
                }
            }
        }

        public boolean setValues(PreparedStatement pst, TestEntity object) throws SQLException {
            if (skipList.contains(objList.indexOf(object))) {
                return false;
            }
            FieldSetter.setInt(pst, 3, object.getVersion());
            FieldSetter.setString(pst, 2, object.getName());
            FieldSetter.setInt(pst, 1, object.getKey());
            return true;
        }
    }

    protected static class TestEntityUpdateSetter5 implements UpdateSetterWithVersion<TestEntity, Integer> {
        private List<Integer> skipList;
        private List<TestEntity> objList;

        protected TestEntityUpdateSetter5(List<TestEntity> list, Integer... skip) {
            this.objList = list;
            skipList = new ArrayList<Integer>();
            if (skip != null) {
                for (Integer s : skip) {
                    skipList.add(s);
                }
            }
        }

        public boolean setValues(PreparedStatement pst, TestEntity object, Integer newVersion, Integer oldVersion) throws SQLException {
            if (skipList.contains(objList.indexOf(object))) {
                return false;
            }
            FieldSetter.setInt(pst, 1, newVersion);
            FieldSetter.setString(pst, 2, object.getName());
            FieldSetter.setInt(pst, 3, object.getKey());
            FieldSetter.setInt(pst, 4, oldVersion);
            return true;
        }
    }

    protected static interface TrxAction {
        Object work();
    }

    protected IDataSet getResourceSet(String fileName) throws DataSetException {
        return new SortedDataSet(new FlatXmlDataSetBuilder().build(this.getClass().getResourceAsStream(fileName)));
    }

    @Component
    public static class InitDB {
        @Resource(name = "dbTester")
        private IDatabaseTester dbTester;

        private IDataSet getDataSet() throws Exception {
            IDataSet tmpExpDataSet =
                    new FlatXmlDataSetBuilder().build(this.getClass().getResourceAsStream("../data/data_set.xml"));
            ReplacementDataSet rds = new ReplacementDataSet(tmpExpDataSet);
            byte[] bytes = "hello".getBytes("UTF-8");
            rds.addReplacementObject("[blob1]", bytes);
            rds.addReplacementObject("[null]", null);
            return rds;
        }

        @PostConstruct
        protected void init() throws Exception {
            dbTester.setDataSet(getDataSet());
            dbTester.setOperationListener(new ConnectionConfigListener());
            dbTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
            dbTester.onSetup();
        }

    }

    protected IDataSet getActualDataSet() throws Exception {
        return new SortedDataSet(new DatabaseConnection(dbTool.getJDBCConnection()).createDataSet(new String[]{"test_table"}));
    }

    protected Object invoke(TrxAction action) throws Exception {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("SomeTxName");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus transaction = tm.getTransaction(def);
        Object result = null;
        try {
            result = action.work();
            tm.commit(transaction);
        } catch (Exception e) {
            tm.rollback(transaction);
            throw e;
        }

        return result;
    }

}

