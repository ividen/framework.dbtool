package ru.kwanza.dbtool;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.SortedDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Guzanov Alexander
 */
public abstract class AbstractTestUpdateUtil extends DBTestCase {
    protected ApplicationContext ctx;

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


    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(this.getClass().getResourceAsStream("./data/data_set.xml"));
    }

    @Override
    protected void setUp() throws Exception {
        ctx = new ClassPathXmlApplicationContext(getSpringCfgFile(), TestSelectUtil.class);
        DatabaseOperation.CLEAN_INSERT.execute(getConnection(), getDataSet());
    }

    protected abstract String getSpringCfgFile();

    protected IDataSet getResourceSet(String fileName) throws DataSetException {
        return new SortedDataSet(new FlatXmlDataSetBuilder().build(this.getClass().getResourceAsStream(fileName)));
    }

    protected TransactionDefinition getTxDef() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("SomeTxName");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return def;
    }

    protected PlatformTransactionManager getTxManager() {
        return (PlatformTransactionManager) ctx.getBean("txManager");
    }

    protected IDataSet getActualDataSet() throws Exception {
        return new SortedDataSet(getConnection().createDataSet(new String[]{"test_table"}));
    }

    public DBTool getDBTool() {
        return ctx.getBean(DBTool.class);
    }


}

