package ru.kwanza.dbtool.orm.impl.querybuilder;

import junit.framework.Assert;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.SortedDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.orm.api.*;
import ru.kwanza.dbtool.orm.impl.fetcher.TestEntity;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingRegistryImpl;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * @author Alexander Guzanov
 */
public abstract class QueryTest extends AbstractJUnit4SpringContextTests {
    @Resource(name = "dbtool.IEntityManager")
    private IEntityManager em;
    @Resource(name = "dbtool.IEntityMappingRegistry")
    private EntityMappingRegistryImpl registry;
    @Resource(name = "dataSource")
    public DataSource dataSource;

    @Value("${jdbc.schema}")
    private String schema;


    @Before
    public void setUpDV() throws Exception {
        DatabaseOperation.CLEAN_INSERT.execute(getConnection(), getDataSet());
    }

    private static IDataSet getDataSet() throws IOException,
            DataSetException {

        return new FlatXmlDataSetBuilder().build(QueryTest.class.getResourceAsStream("initdb.xml"));
    }

    public IDatabaseConnection getConnection() throws SQLException, DatabaseUnitException {
        DatabaseConnection connection = new DatabaseConnection(dataSource.getConnection(), schema);
        connection.getConfig().setProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);
        return connection;
    }

    @Before
    public void init() {
        registry.registerEntityClass(TestEntity.class);
    }

    public IDataSet getActualDataSet() throws Exception {
        return new SortedDataSet(getConnection().createDataSet(new String[]{"test_entity"}));
    }

    @Test
    public void testSimpleSelect() throws Exception {

        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .orderBy(OrderBy.ASC("id")).create();
        System.out.println(query);
        IStatement<TestEntity> statement = query.prepare();
        List<TestEntity> testEntities = statement.selectList();
        assertEquals(testEntities.size(), 200);
        Map<Long, TestEntity> mapById = statement.selectMap("id");
        assertEquals(mapById.size(), 200);
        Map<Long, List<TestEntity>> id1 = statement.selectMapList("intField");
        assertEquals(id1.size(), 2);
        assertEquals(id1.get(10).size(), 100);
        assertEquals(id1.get(20).size(), 100);
    }

    @Test
    public void testSelectIn() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .usePaging(true)
                .where(Condition.in("id"))
                .orderBy(OrderBy.ASC("id")).create();
        System.out.println(query);
        IStatement<TestEntity> statement = query.prepare().paging(0,100);
        statement.setParameter(1, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        List<TestEntity> testEntities = statement.selectList();
        assertEquals(testEntities.size(), 10);
        Map<Long, TestEntity> id = statement.selectMap("id");
        assertEquals(id.size(), 10);
        Map<Long, List<TestEntity>> id1 = statement.selectMapList("intField");
        assertEquals(id1.size(), 1);
        assertEquals(id1.get(10).size(), 10);
    }


    @Test
    public void testSelect_With_NamedParams() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .usePaging(true)
                .where(Condition.and(
                        Condition.isGreaterOrEqual("id", "id"),
                        Condition.isLessOrEqual("id", "id"))
                )
                .orderBy(OrderBy.ASC("id")).create();
        System.out.println(query);
        IStatement<TestEntity> statement = query.prepare().paging(0,100);
        statement.setParameter("id", 1l);
        List<TestEntity> testEntities = statement.selectList();
        assertEquals(testEntities.size(), 1);
        Map<Long, TestEntity> id = statement.selectMap("id");
        assertEquals(id.size(), 1);
        Map<Long, List<TestEntity>> id1 = statement.selectMapList("intField");
        assertEquals(id1.size(), 1);
        assertEquals(id1.get(10).size(), 1);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testSelect_WrongParams_1() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .usePaging(true)
                .orderBy(OrderBy.ASC("id")).create();

        query.prepare().paging(0,1000).setParameter(1, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testSelect_WrongParams_2() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .usePaging(true)
                .where(Condition.in("id"))
                .orderBy(OrderBy.ASC("id")).create();

        IStatement<TestEntity> statement = query.prepare().paging(0,1000);
        statement.setParameter(1, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        statement.setParameter(2, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testSelect_WrongParams_3() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .usePaging(true)
                .where(Condition.in("id1"))
                .orderBy(OrderBy.ASC("id")).create();
        IStatement<TestEntity> statement = query.prepare().paging(0,1000);
        statement.setParameter(1, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testSelect_WrongParams_4() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .usePaging(true)
                .where(Condition.in("id"))
                .orderBy(OrderBy.ASC("id1")).create();
        IStatement<TestEntity> statement = query.prepare().paging(0,1000);
        statement.setParameter(1, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }


    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testSelect_groupField_5() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .usePaging(true)
                .orderBy(OrderBy.ASC("id")).create();
        IStatement<TestEntity> statement = query.prepare().paging(0,1000);
        statement.selectMap("title");
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testSelect_groupField_6() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .usePaging(true)
                .orderBy(OrderBy.ASC("id")).create();
        IStatement<TestEntity> statement = query.prepare().paging(0,1000);
        statement.selectMapList("title");
    }

    @Test
    public void testSelect_offset_0() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .usePaging(true)
                .orderBy(OrderBy.ASC("id")).create();

        IStatement<TestEntity> statement = query.prepare().paging(99,1);
        List<TestEntity> testEntities = statement.selectList();
        assertEquals(testEntities.size(), 1);
        Assert.assertEquals(testEntities.get(0).getId().longValue(), 99l);

        Map<Long, TestEntity> map = statement.selectMap("id");
        assertEquals(map.size(), 1);
        Assert.assertEquals(map.get(99l).getId().longValue(), 99l);
    }


    @Test
    public void testSelect_offset_1() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .usePaging(true)
                .orderBy(OrderBy.ASC("id")).create();

        IStatement<TestEntity> statement = query.prepare().paging(0,1);
        List<TestEntity> testEntities = statement.selectList();
        assertEquals(testEntities.size(), 1);
        Assert.assertEquals(testEntities.get(0).getId().longValue(), 0l);

        Map<Long, TestEntity> map = statement.selectMap("id");
        assertEquals(map.size(), 1);
        Assert.assertEquals(map.get(0l).getId().longValue(), 0l);
    }

    @Test
    public void testSelect_offset_2() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .usePaging(true)
                .orderBy(OrderBy.ASC("id")).create();

        IStatement<TestEntity> statement = query.prepare().paging(99,101);
        List<TestEntity> testEntities = statement.selectList();
        assertEquals(testEntities.size(), 101);
        Assert.assertEquals(testEntities.get(0).getId().longValue(), 99l);
    }

    @Test
    public void testSelect_offset_noteixts() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .usePaging(true)
                .where(Condition.isNull("id"))

                .orderBy(OrderBy.ASC("id")).create();

        IStatement<TestEntity> statement = query.prepare().paging(1,100);
        List<TestEntity> testEntities = statement.selectList();
        assertEquals(testEntities.size(), 0);
        Map<Long, TestEntity> map = statement.selectMap("id");
        assertEquals(map.size(), 0);
    }

    @Test
    public void testSelect_offset_greater() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .usePaging(true)
                .where(Condition.isEqual("id"))
                .orderBy(OrderBy.ASC("id")).create();
        IStatement<TestEntity> statement = query.prepare().paging(99,1);
        List<TestEntity> testEntities = statement.setParameter(1, 100).selectList();
        assertEquals(testEntities.size(), 0);
        Map<Long, TestEntity> map = statement.setParameter(1, 100).selectMap("id");
        assertEquals(map.size(), 0);
    }


    @Test
    public void testSelect_single() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .where(Condition.isEqual("id"))
                .orderBy(OrderBy.ASC("id")).create();

        IStatement<TestEntity> statement = query.prepare();
        TestEntity select = statement.setParameter(1, 100l).select();
        assertEquals(select.getId().longValue(), 100l);
    }


    @Test(expected = IncorrectResultSizeDataAccessException.class)
    public void testSelect_single_wrong() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .where(Condition.isGreater("id"))
                .orderBy(OrderBy.ASC("id")).create();

        IStatement<TestEntity> statement = query.prepare();
        TestEntity select = statement.setParameter(1, 1).select();
        assertEquals(select.getId().longValue(), 100l);
    }


    @Test
    public void testNativeQuery_1() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .createNative("SELECT * FROM  test_entity where id in(?)");
        System.out.println(query);
        IStatement<TestEntity> statement = query.prepare();
        statement.setParameter(1, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        List<TestEntity> testEntities = statement.selectList();
        assertEquals(testEntities.size(), 10);
        Map<Long, TestEntity> id = statement.selectMap("id");
        assertEquals(id.size(), 10);
        Map<Long, List<TestEntity>> id1 = statement.selectMapList("intField");
        assertEquals(id1.size(), 1);
        assertEquals(id1.get(10).size(), 10);
    }


    @Test
    public void testNativeQuery_2() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .createNative("SELECT * FROM  test_entity where id in(:ids)");
        System.out.println(query);
        IStatement<TestEntity> statement = query.prepare();
        statement.setParameter(1, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        List<TestEntity> testEntities = statement.selectList();
        assertEquals(testEntities.size(), 10);
        Map<Long, TestEntity> id = statement.selectMap("id");
        assertEquals(id.size(), 10);
        Map<Long, List<TestEntity>> id1 = statement.selectMapList("intField");
        assertEquals(id1.size(), 1);
        assertEquals(id1.get(10).size(), 10);
    }

    @Test
    public void testNativeQuery_3() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .createNative(
                        "SELECT * " +
                                "FROM  test_entity where id in(:ids) " +
                                "UNION ALL \n" +
                                "SELECT * " +
                                "FROM  test_entity where id in(:ids) ");
        System.out.println(query);
        IStatement<TestEntity> statement = query.prepare();
        statement.setParameter("ids", Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        List<TestEntity> testEntities = statement.selectList();
        assertEquals(testEntities.size(), 20);
        Map<Long, List<TestEntity>> id1 = statement.selectMapList("id");
        assertEquals(id1.size(), 10);
        assertEquals(id1.get(10l).size(), 2);
    }


    @Test
    public void testNativeQuery_4() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .where(Condition.createNative("id in(:ids)")).create();
        IStatement<TestEntity> statement = query.prepare();
        statement.setParameter(1, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        List<TestEntity> testEntities = statement.selectList();
        assertEquals(testEntities.size(), 10);
        Map<Long, TestEntity> id = statement.selectMap("id");
        assertEquals(id.size(), 10);
        Map<Long, List<TestEntity>> id1 = statement.selectMapList("intField");
        assertEquals(id1.size(), 1);
        assertEquals(id1.get(10).size(), 10);
    }

}
