package ru.kwanza.dbtool.core.selectutil;

import junit.framework.Assert;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.core.ConnectionConfigListener;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.KeyValue;
import ru.kwanza.dbtool.core.TestEntity;
import ru.kwanza.dbtool.core.blob.BlobInputStream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Guzanov Alexander
 */

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class TestSelectUtil extends AbstractJUnit4SpringContextTests {

    @Resource(name = "dbtool.DBTool")
    private DBTool dbTool;
    private BlobInputStream blobIS;

    public static final String SELECT_SQL_1 = "SELECT xkey,name,version FROM test_table WHERE xkey IN(?) order by xkey";
    public static final String SELECT_SQL_2 = "SELECT xkey,name,version FROM test_table WHERE xkey IN(?) AND name=? order by xkey";
    public static final String SELECT_SQL_3 = "SELECT xkey,name,version FROM test_table WHERE name=? AND xkey IN(?)";
    public static final String SELECT_SQL_4 = "SELECT xkey,name,version FROM test_table WHERE xkey IN (?) AND name IN(?)";
    public static final String SELECT_SQL_5 = "SELECT xkey,name,version FROM test_table";
    public static final String SELECT_SQL_6 = "SELECT xkey FROM test_table WHERE xkey IN(?) " +
            " UNION ALL" +
            " SELECT xkey FROM test_table WHERE xkey IN(?)";

    public static final String SELECT_SQL_7 = "SELECT  xkey,name,version FROM test_table WHERE xkey IN(?) " +
            " UNION ALL" +
            " SELECT  xkey,name,version FROM test_table WHERE xkey IN(?)";
    public static final String SELECT_SQL_8 = "SELECT xkey,name,version FROM test_table WHERE name IN(?) AND xkey IN(?) order by xkey";

    public static final TestEntityRowMapper LIST_ROW_MAPPER = new TestEntityRowMapper();
    public static final TestEntityMapRowMapper MAP_ROW_MAPPER = new TestEntityMapRowMapper();
    public static final TestEntityMapOfMapRowMapper MAP_OF_MAP_ROW_MAPPER = new TestEntityMapOfMapRowMapper();

    public static final class TestEntityRowMapper implements RowMapper<TestEntity> {
        public TestEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TestEntity(rs.getInt("xkey"), rs.getString("name"), rs.getInt("version"));
        }
    }

    public static final class TestEntityMapRowMapper implements RowMapper<KeyValue<String, TestEntity>> {
        public KeyValue<String, TestEntity> mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new KeyValue<String, TestEntity>(rs.getString("name"),
                    new TestEntity(rs.getInt("xkey"), rs.getString("name"), rs.getInt("version")));
        }
    }

    public static final class TestEntityMapOfMapRowMapper implements RowMapper<KeyValue<String, KeyValue<Integer, TestEntity>>> {
        public KeyValue<String, KeyValue<Integer, TestEntity>> mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new KeyValue<String, KeyValue<Integer, TestEntity>>(rs.getString("name"),
                    new KeyValue<Integer, TestEntity>(rs.getInt("xkey"),
                            new TestEntity(rs.getInt("xkey"), rs.getString("name"), rs.getInt("version"))));
        }
    }


    @Component
    public static class InitDB {
        @Resource(name = "dbTester")
        private IDatabaseTester dbTester;

        private IDataSet getDataSet() throws Exception {
            IDataSet tmpExpDataSet =
                    new FlatXmlDataSetBuilder().build(this.getClass().getResourceAsStream("../data/big_data_1.xml"));
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

    @Test
    public void testEmpty() throws Exception {

        List<Integer> list = Arrays.asList(Integer.valueOf(10000), Integer.valueOf(100002), Integer.valueOf(100003));
        List<TestEntity> result =
                dbTool.selectList("SELECT xkey,name,version FROM test_table WHERE xkey IN(?) order by xkey", LIST_ROW_MAPPER, list);

        assertEquals("Must be empty", 0, result.size());

        Map<String, TestEntity> map = dbTool.selectMap(SELECT_SQL_1, MAP_ROW_MAPPER, list);
        assertEquals("Must be empty", 0, map.size());

        Map<String, List<TestEntity>> mapList = dbTool.selectMapList(SELECT_SQL_1, MAP_ROW_MAPPER, list);
        assertEquals("Must be empty", 0, mapList.size());
    }

    @Test
    public void testSelect_ALL_1() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 5000; i++) {
            keys.add(i);
        }

        List<TestEntity> result = dbTool.selectList(SELECT_SQL_1, LIST_ROW_MAPPER, keys);
        assertEquals("Wrong size", 5000, result.size());
        checkExistance(result, 0, 5000);

        Map<String, List<TestEntity>> mapList = dbTool.selectMapList(SELECT_SQL_1, MAP_ROW_MAPPER, keys);
        assertEquals("Wrong size", 1667, mapList.size());

        int count = 0;
        for (List<TestEntity> l : mapList.values()) {
            count += l.size();
        }

        checkExistance(mapList, 0, 5000);
        assertEquals("Wrong size", 5000, count);
    }

    @Test
    public void testSelect_2500_1() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 2500; i++) {
            keys.add(i);
        }

        List<TestEntity> result = dbTool.selectList(SELECT_SQL_1, LIST_ROW_MAPPER, keys);
        assertEquals("Wrong size", 2500, result.size());
        checkExistance(result, 0, 2500);

        Map<String, List<TestEntity>> mapList = dbTool.selectMapList(SELECT_SQL_1, MAP_ROW_MAPPER, keys);

        int count = 0;
        for (List<TestEntity> l : mapList.values()) {
            count += l.size();
        }

        checkExistance(mapList, 0, 2500);
        assertEquals("Wrong size", 2500, count);

        keys.clear();

        for (int i = 2500; i < 5000; i++) {
            keys.add(i);
        }

        result = dbTool.selectList(SELECT_SQL_1, LIST_ROW_MAPPER, keys);
        assertEquals("Wrong size", 2500, result.size());
        checkExistance(result, 2500, 5000);

        mapList = dbTool.selectMapList(SELECT_SQL_1, MAP_ROW_MAPPER, keys);

        count = 0;
        for (List<TestEntity> l : mapList.values()) {
            count += l.size();
        }

        checkExistance(mapList, 2500, 5000);
        assertEquals("Wrong size", 2500, count);

    }

    @Test
    public void testSelect_2() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 5000; i++) {
            keys.add(i);
        }

        List<TestEntity> result = dbTool.selectList(SELECT_SQL_2, LIST_ROW_MAPPER, keys, "n_0");
        assertEquals("Wrong size", 3, result.size());
        checkExistance(result, 0, 3);

        Map<String, List<TestEntity>> mapList = dbTool
                .selectMapList("SELECT xkey,name,version FROM test_table WHERE xkey IN(?) AND name=? order by xkey", MAP_ROW_MAPPER, keys,
                        "n_0");
        assertEquals("Wrong size", 1, mapList.size());

        int count = 0;
        for (List<TestEntity> l : mapList.values()) {
            count += l.size();
        }

        checkExistance(mapList, 0, 3);
        assertEquals("Wrong size", 3, count);
    }

    @Test
    public void testSelect_3() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 5000; i++) {
            keys.add(i);
        }

        List<TestEntity> result = dbTool.selectList(SELECT_SQL_3, LIST_ROW_MAPPER, "n_0", keys);
        assertEquals("Wrong size", 3, result.size());
        checkExistance(result, 0, 3);

        Map<String, List<TestEntity>> mapList = dbTool.selectMapList(SELECT_SQL_3, MAP_ROW_MAPPER, "n_0", keys);
        assertEquals("Wrong size", 1, mapList.size());

        int count = 0;
        for (List<TestEntity> l : mapList.values()) {
            count += l.size();
        }

        checkExistance(mapList, 0, 3);
        assertEquals("Wrong size", 3, count);
    }

    @Test
    public void testSelect_4() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 5000; i++) {
            keys.add(i);
        }

        ArrayList<String> names = new ArrayList<String>();
        for (int i = 0; i < 1000; i++) {
            names.add("n_" + i);
        }

        List<TestEntity> result = dbTool.selectList(SELECT_SQL_4, LIST_ROW_MAPPER, keys, names);
        assertEquals("Wrong size", 3000, result.size());
        checkExistance(result, 0, 3000);

        Map<String, List<TestEntity>> mapList = dbTool.selectMapList(SELECT_SQL_4, MAP_ROW_MAPPER, keys, names);
        assertEquals("Wrong size", 1000, mapList.size());

        int count = 0;
        for (List<TestEntity> l : mapList.values()) {
            count += l.size();
        }

        checkExistance(mapList, 0, 3000);
        assertEquals("Wrong size", 3000, count);
    }

    @Test
    public void testSelect_NoResult() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 5000; i++) {
            keys.add(i);
        }

        List<TestEntity> result = dbTool.selectList(SELECT_SQL_2, LIST_ROW_MAPPER, keys, "test");
        assertEquals("Wrong size", 0, result.size());
        Map<String, List<TestEntity>> mapList = dbTool.selectMapList(SELECT_SQL_4, MAP_ROW_MAPPER, keys, "test");
        assertEquals("Wrong size", 0, mapList.size());

    }

    @Test
    public void testSelect_BadCount() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 5000; i++) {
            keys.add(i);
        }

        try {
            List<TestEntity> result = dbTool.selectList(SELECT_SQL_5, LIST_ROW_MAPPER, keys, "test");
            Assert.fail("Must be Exception!");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSelect_Map_Exception() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 5000; i++) {
            keys.add(i);
        }

        try {
            dbTool.selectMap(SELECT_SQL_1, MAP_ROW_MAPPER, keys);
            Assert.fail("Must be Exception!");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSelect_Map() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 1; i++) {
            keys.add(i);
        }

        Map<String, TestEntity> map = dbTool.selectMap(SELECT_SQL_2, MAP_ROW_MAPPER, keys, "n_0");
        assertEquals("Wrong size", 1, map.size());

    }

    @Test
    public void testSelect_ListOfInt() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 1000; i++) {
            keys.add(i);
        }

        List<Integer> list = dbTool.selectList(SELECT_SQL_6, Integer.class, keys, keys);
        assertEquals("Wrong size", 2000, list.size());

        for (int i = 0; i < 1000; i++) {
            int count = 0;
            for (Integer v : list) {
                if (v.equals(i)) {
                    count++;
                }
            }

            assertEquals("Wrong result for key=" + i, 2, count);
        }
    }

    @Test
    public void testSelect_SetOfInt() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 1000; i++) {
            keys.add(i);
        }

        Set<Integer> list = dbTool.selectSet(SELECT_SQL_6, Integer.class, keys, keys);
        assertEquals("Wrong size", 1000, list.size());

        for (int i = 0; i < 1000; i++) {
            int count = 0;
            for (Integer v : list) {
                if (v.equals(i)) {
                    count++;
                }
            }

            assertEquals("Wrong result for key=" + i, 1, count);
        }
    }

    @Test
    public void testSelect_Set() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 1000; i++) {
            keys.add(i);
        }

        Set<TestEntity> list = dbTool.selectSet(SELECT_SQL_7, LIST_ROW_MAPPER, keys, keys);
        assertEquals("Wrong size", 1000, list.size());

        for (int i = 0; i < 1000; i++) {
            int count = 0;
            for (TestEntity v : list) {
                if (v.getKey() == i) {
                    count++;
                }
            }

            assertEquals("Wrong result for key=" + i, 1, count);
        }
    }

    @Test
    public void testSelect_WithSQLParameter() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 5000; i++) {
            keys.add(i);
        }

        List<TestEntity> result = dbTool.selectList(SELECT_SQL_2, LIST_ROW_MAPPER, keys, new SqlParameterValue(Types.VARCHAR, "n_0"));

        assertEquals("Wrong size", 3, result.size());
        checkExistance(result, 0, 2);

    }

    @Test
    public void testSelect_Map_By_Collection() throws Exception {
        HashSet<Integer> keys = new HashSet<Integer>();
        for (int i = 0; i < 1; i++) {
            keys.add(i);
        }

        Map<String, TestEntity> map = dbTool.selectMap(SELECT_SQL_2, MAP_ROW_MAPPER, keys, "n_0");
        assertEquals("Wrong size", 1, map.size());
    }

    @Test
    public void testSelect_MapOfMap_By_Collections() throws Exception {
        HashSet<Integer> keys = new HashSet<Integer>();
        keys.add(2);
        keys.add(5);
        keys.add(6);
        keys.add(8);

        HashSet<String> names = new HashSet<String>();
        names.add("n_0");
        names.add("n_1");
        names.add("n_2");

        Map<String, Map<Integer, TestEntity>> map = dbTool.selectMapOfMaps(SELECT_SQL_8, MAP_OF_MAP_ROW_MAPPER, names, keys);
        assertEquals("Wrong size", 3, map.size());
        assertEquals("Wrong size", 2, map.get("n_2").size());
    }

    @Test
    public void testSelect_Set_By_Collection() throws Exception {
        HashSet<Integer> keys = new HashSet<Integer>();
        for (int i = 0; i < 1000; i++) {
            keys.add(i);
        }

        Set<TestEntity> list = dbTool.selectSet(SELECT_SQL_7, LIST_ROW_MAPPER, keys, keys);
        assertEquals("Wrong size", 1000, list.size());

        for (int i = 0; i < 1000; i++) {
            int count = 0;
            for (TestEntity v : list) {
                if (v.getKey() == i) {
                    count++;
                }
            }

            assertEquals("Wrong result for key=" + i, 1, count);
        }
    }

    @Test
    public void testSelect_1120() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 1120; i++) {
            keys.add(i);
        }

        List<TestEntity> result = dbTool.selectList(SELECT_SQL_1, LIST_ROW_MAPPER, keys);
        assertEquals("Wrong size", 1120, result.size());
        checkExistance(result, 0, 1120);
    }

    @Test
    public void testSelect_150() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 150; i++) {
            keys.add(i);
        }

        List<TestEntity> result = dbTool.selectList(SELECT_SQL_1, LIST_ROW_MAPPER, keys);
        assertEquals("Wrong size", 150, result.size());
        checkExistance(result, 0, 150);
    }

    private void checkExistance(List<TestEntity> list, int from, int to) {
        for (int i = from; i < to; i++) {
            assertTrue("Entity " + i + " not found!", findEntity(list, i));
        }
    }

    private void checkExistance(Map<String, List<TestEntity>> map, int from, int to) {
        for (int i = from; i < to; i++) {
            assertTrue("Entity " + i + " not found!", findEntityInSet(map.values(), i));
        }
    }

    private boolean findEntityInSet(Collection<List<TestEntity>> set, int i) {
        for (List<TestEntity> l : set) {
            for (TestEntity t : l) {
                if (t.getKey() == i) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean findEntity(List<TestEntity> list, int i) {
        for (TestEntity t : list) {
            if (t.getKey() == i) {
                return true;
            }
        }
        return false;
    }

    
}
