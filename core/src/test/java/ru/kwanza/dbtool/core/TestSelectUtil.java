package ru.kwanza.dbtool.core;

import org.dbunit.DBTestCase;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

/**
 * @author Guzanov Alexander
 */
public abstract class TestSelectUtil extends DBTestCase {

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

    private ApplicationContext ctx;

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

    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(this.getClass().getResourceAsStream("./data/big_data_1.xml"));
    }

    @Override
    protected void setUp() throws Exception {
        ctx = new ClassPathXmlApplicationContext(getSpringCfgFile(), TestSelectUtil.class);
        DatabaseOperation.CLEAN_INSERT.execute(getConnection(), getDataSet());
    }

    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        config.setProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);
    }

    protected abstract String getSpringCfgFile();

    public void testEmpty() throws Exception {
        DatabaseOperation.CLEAN_INSERT
                .execute(getConnection(), new FlatXmlDataSetBuilder().build(this.getClass().getResourceAsStream("./data/empty.xml")));

        List<Integer> list = Arrays.asList(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
        List<TestEntity> result =
                getDBTool().selectList("SELECT xkey,name,version FROM test_table WHERE xkey IN(?) order by xkey", LIST_ROW_MAPPER, list);

        assertEquals("Must be empty", 0, result.size());

        Map<String, TestEntity> map = getDBTool().selectMap(SELECT_SQL_1, MAP_ROW_MAPPER, list);
        assertEquals("Must be empty", 0, map.size());

        Map<String, List<TestEntity>> mapList = getDBTool().selectMapList(SELECT_SQL_1, MAP_ROW_MAPPER, list);
        assertEquals("Must be empty", 0, mapList.size());
    }

    public void testSelect_ALL_1() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 5000; i++) {
            keys.add(i);
        }

        List<TestEntity> result = getDBTool().selectList(SELECT_SQL_1, LIST_ROW_MAPPER, keys);
        assertEquals("Wrong size", 5000, result.size());
        checkExistance(result, 0, 5000);

        Map<String, List<TestEntity>> mapList = getDBTool().selectMapList(SELECT_SQL_1, MAP_ROW_MAPPER, keys);
        assertEquals("Wrong size", 1667, mapList.size());

        int count = 0;
        for (List<TestEntity> l : mapList.values()) {
            count += l.size();
        }

        checkExistance(mapList, 0, 5000);
        assertEquals("Wrong size", 5000, count);
    }

    public void testSelect_2500_1() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 2500; i++) {
            keys.add(i);
        }

        List<TestEntity> result = getDBTool().selectList(SELECT_SQL_1, LIST_ROW_MAPPER, keys);
        assertEquals("Wrong size", 2500, result.size());
        checkExistance(result, 0, 2500);

        Map<String, List<TestEntity>> mapList = getDBTool().selectMapList(SELECT_SQL_1, MAP_ROW_MAPPER, keys);

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

        result = getDBTool().selectList(SELECT_SQL_1, LIST_ROW_MAPPER, keys);
        assertEquals("Wrong size", 2500, result.size());
        checkExistance(result, 2500, 5000);

        mapList = getDBTool().selectMapList(SELECT_SQL_1, MAP_ROW_MAPPER, keys);

        count = 0;
        for (List<TestEntity> l : mapList.values()) {
            count += l.size();
        }

        checkExistance(mapList, 2500, 5000);
        assertEquals("Wrong size", 2500, count);

    }

    public void testSelect_2() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 5000; i++) {
            keys.add(i);
        }

        List<TestEntity> result = getDBTool().selectList(SELECT_SQL_2, LIST_ROW_MAPPER, keys, "n_0");
        assertEquals("Wrong size", 3, result.size());
        checkExistance(result, 0, 3);

        Map<String, List<TestEntity>> mapList = getDBTool()
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

    public void testSelect_3() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 5000; i++) {
            keys.add(i);
        }

        List<TestEntity> result = getDBTool().selectList(SELECT_SQL_3, LIST_ROW_MAPPER, "n_0", keys);
        assertEquals("Wrong size", 3, result.size());
        checkExistance(result, 0, 3);

        Map<String, List<TestEntity>> mapList = getDBTool().selectMapList(SELECT_SQL_3, MAP_ROW_MAPPER, "n_0", keys);
        assertEquals("Wrong size", 1, mapList.size());

        int count = 0;
        for (List<TestEntity> l : mapList.values()) {
            count += l.size();
        }

        checkExistance(mapList, 0, 3);
        assertEquals("Wrong size", 3, count);
    }

    public void testSelect_4() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 5000; i++) {
            keys.add(i);
        }

        ArrayList<String> names = new ArrayList<String>();
        for (int i = 0; i < 1000; i++) {
            names.add("n_" + i);
        }

        List<TestEntity> result = getDBTool().selectList(SELECT_SQL_4, LIST_ROW_MAPPER, keys, names);
        assertEquals("Wrong size", 3000, result.size());
        checkExistance(result, 0, 3000);

        Map<String, List<TestEntity>> mapList = getDBTool().selectMapList(SELECT_SQL_4, MAP_ROW_MAPPER, keys, names);
        assertEquals("Wrong size", 1000, mapList.size());

        int count = 0;
        for (List<TestEntity> l : mapList.values()) {
            count += l.size();
        }

        checkExistance(mapList, 0, 3000);
        assertEquals("Wrong size", 3000, count);
    }

    public void testSelect_NoResult() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 5000; i++) {
            keys.add(i);
        }

        List<TestEntity> result = getDBTool().selectList(SELECT_SQL_2, LIST_ROW_MAPPER, keys, "test");
        assertEquals("Wrong size", 0, result.size());
        Map<String, List<TestEntity>> mapList = getDBTool().selectMapList(SELECT_SQL_4, MAP_ROW_MAPPER, keys, "test");
        assertEquals("Wrong size", 0, mapList.size());

    }

    public void testSelect_BadCount() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 5000; i++) {
            keys.add(i);
        }

        try {
            List<TestEntity> result = getDBTool().selectList(SELECT_SQL_5, LIST_ROW_MAPPER, keys, "test");
            fail("Must be Exception!");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void testSelect_Map_Exception() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 5000; i++) {
            keys.add(i);
        }

        try {
            getDBTool().selectMap(SELECT_SQL_1, MAP_ROW_MAPPER, keys);
            fail("Must be Exception!");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void testSelect_Map() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 1; i++) {
            keys.add(i);
        }

        Map<String, TestEntity> map = getDBTool().selectMap(SELECT_SQL_2, MAP_ROW_MAPPER, keys, "n_0");
        assertEquals("Wrong size", 1, map.size());

    }

    public void testSelect_ListOfInt() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 1000; i++) {
            keys.add(i);
        }

        List<Integer> list = getDBTool().selectList(SELECT_SQL_6, Integer.class, keys, keys);
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

    public void testSelect_SetOfInt() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 1000; i++) {
            keys.add(i);
        }

        Set<Integer> list = getDBTool().selectSet(SELECT_SQL_6, Integer.class, keys, keys);
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

    public void testSelect_Set() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 1000; i++) {
            keys.add(i);
        }

        Set<TestEntity> list = getDBTool().selectSet(SELECT_SQL_7, LIST_ROW_MAPPER, keys, keys);
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

    public void testSelect_WithSQLParameter() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 5000; i++) {
            keys.add(i);
        }

        List<TestEntity> result = getDBTool().selectList(SELECT_SQL_2, LIST_ROW_MAPPER, keys, new SqlParameterValue(Types.VARCHAR, "n_0"));

        assertEquals("Wrong size", 3, result.size());
        checkExistance(result, 0, 2);

    }

    public void testSelect_Map_By_Collection() throws Exception {
        HashSet<Integer> keys = new HashSet<Integer>();
        for (int i = 0; i < 1; i++) {
            keys.add(i);
        }

        Map<String, TestEntity> map = getDBTool().selectMap(SELECT_SQL_2, MAP_ROW_MAPPER, keys, "n_0");
        assertEquals("Wrong size", 1, map.size());
    }

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

        Map<String, Map<Integer, TestEntity>> map = getDBTool().selectMapOfMaps(SELECT_SQL_8, MAP_OF_MAP_ROW_MAPPER, names, keys);
        assertEquals("Wrong size", 3, map.size());
        assertEquals("Wrong size", 2, map.get("n_2").size());
    }

    public void testSelect_Set_By_Collection() throws Exception {
        HashSet<Integer> keys = new HashSet<Integer>();
        for (int i = 0; i < 1000; i++) {
            keys.add(i);
        }

        Set<TestEntity> list = getDBTool().selectSet(SELECT_SQL_7, LIST_ROW_MAPPER, keys, keys);
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

    public void testSelect_1120() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 1120; i++) {
            keys.add(i);
        }

        List<TestEntity> result = getDBTool().selectList(SELECT_SQL_1, LIST_ROW_MAPPER, keys);
        assertEquals("Wrong size", 1120, result.size());
        checkExistance(result, 0, 1120);
    }

    public void testSelect_150() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 150; i++) {
            keys.add(i);
        }

        List<TestEntity> result = getDBTool().selectList(SELECT_SQL_1, LIST_ROW_MAPPER, keys);
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

    public DBTool getDBTool() {
        return ctx.getBean(DBTool.class);
    }
}
