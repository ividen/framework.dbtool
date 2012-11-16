package ru.kwanza.dbtool.orm.impl.querybuilder;

import junit.framework.Assert;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.SortedDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.orm.api.Condition;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.api.OrderBy;
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
    private DataSource dataSource;


    @Before
    public void setUpDV() throws Exception {
        DatabaseOperation.CLEAN_INSERT.execute(getConnection(), getDataSet());
    }

    private static IDataSet getDataSet() throws IOException,
            DataSetException {

        return new FlatXmlDataSetBuilder().build(QueryTest.class.getResourceAsStream("initdb.xml"));
    }

    public IDatabaseConnection getConnection() throws SQLException, DatabaseUnitException {
        return new DatabaseConnection(dataSource.getConnection());
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

        List<TestEntity> testEntities = query.selectList();
        assertEquals(testEntities.size(), 200);
        Map<Long, TestEntity> mapById = query.selectMap("id");
        assertEquals(mapById.size(), 200);
        Map<Long, List<TestEntity>> id1 = query.selectMapList("intField");
        assertEquals(id1.size(), 2);
        assertEquals(id1.get(10).size(), 100);
        assertEquals(id1.get(20).size(), 100);
    }

    @Test
    public void testSelectIn() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .setMaxSize(100)
                .where(Condition.in("id"))
                .orderBy(OrderBy.ASC("id")).create();

        query.setParameter(1, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        List<TestEntity> testEntities = query.selectList();
        assertEquals(testEntities.size(), 10);
        Map<Long, TestEntity> id = query.selectMap("id");
        assertEquals(id.size(), 10);
        Map<Long, List<TestEntity>> id1 = query.selectMapList("intField");
        assertEquals(id1.size(), 1);
        assertEquals(id1.get(10).size(), 10);
    }

    @Test
    @ExpectedException(java.lang.IllegalArgumentException.class)
    public void testSelect_WrongParams_1() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .setMaxSize(1000)
                .orderBy(OrderBy.ASC("id")).create();

        query.setParameter(1, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test
    @ExpectedException(java.lang.IllegalArgumentException.class)
    public void testSelect_WrongParams_2() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .setMaxSize(1000)
                .where(Condition.in("id"))
                .orderBy(OrderBy.ASC("id")).create();

        query.setParameter(1, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        query.setParameter(2, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test
    @ExpectedException(java.lang.IllegalArgumentException.class)
    public void testSelect_WrongParams_3() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .setMaxSize(1000)
                .where(Condition.in("id1"))
                .orderBy(OrderBy.ASC("id")).create();

        query.setParameter(1, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test
    @ExpectedException(java.lang.IllegalArgumentException.class)
    public void testSelect_WrongParams_4() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .setMaxSize(1000)
                .where(Condition.in("id"))
                .orderBy(OrderBy.ASC("id1")).create();

        query.setParameter(1, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }


    @Test
    @ExpectedException(java.lang.IllegalArgumentException.class)
    public void testSelect_groupField_5() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .setMaxSize(1000)
                .orderBy(OrderBy.ASC("id")).create();

        query.selectMap("title");
    }

    @Test
    @ExpectedException(java.lang.IllegalArgumentException.class)
    public void testSelect_groupField_6() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .setMaxSize(1000)
                .orderBy(OrderBy.ASC("id")).create();

        query.selectMapList("title");
    }

    @Test
    public void testSelect_offset() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .setMaxSize(100)
                .setOffset(99)
                .orderBy(OrderBy.ASC("id")).create();

        List<TestEntity> testEntities = query.selectList();
        assertEquals(testEntities.size(), 1);
        Assert.assertEquals(testEntities.get(0).getId().longValue(), 99l);

        Map<Long, TestEntity> map = query.selectMap("id");
        assertEquals(map.size(), 1);
        Assert.assertEquals(map.get(99l).getId().longValue(), 99l);
    }

    @Test
    public void testSelect_offset_noteixts() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .setMaxSize(100)
                .where(Condition.isNull("id"))
                .setOffset(99)
                .orderBy(OrderBy.ASC("id")).create();

        List<TestEntity> testEntities = query.selectList();
        assertEquals(testEntities.size(), 0);
        Map<Long, TestEntity> map = query.selectMap("id");
        assertEquals(map.size(), 0);
    }

    @Test
    public void testSelect_offset_greater() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .setMaxSize(100)
                .where(Condition.isEqual("id"))
                .setOffset(99)
                .orderBy(OrderBy.ASC("id")).create();

        List<TestEntity> testEntities = query.setParameter(1, 100).selectList();
        assertEquals(testEntities.size(), 0);
        Map<Long, TestEntity> map = query.setParameter(1, 100).selectMap("id");
        assertEquals(map.size(), 0);
    }


    @Test
    public void testSelect_single() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .where(Condition.isEqual("id"))
                .orderBy(OrderBy.ASC("id")).create();

        TestEntity select = query.setParameter(1, 100l).select();
        assertEquals(select.getId().longValue(), 100l);
    }


}
