package ru.kwanza.dbtool.orm.impl.filtering;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.SortedDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
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
public abstract class FilteringTest extends AbstractJUnit4SpringContextTests {
    @Resource(name = "dbtool.IEntityManager")
    private IEntityManager em;
    @Resource(name = "dbtool.IEntityMappingRegistry")
    private EntityMappingRegistryImpl registry;
    @Resource(name = "dataSource")
    private DataSource dataSource;

    @Value("${jdbc.schema}")
    private String schema;


    @Before
    public void setUpDV() throws Exception {
        DatabaseOperation.CLEAN_INSERT.execute(getConnection(), getDataSet());
    }

    private static IDataSet getDataSet() throws IOException,
            DataSetException {

        return new FlatXmlDataSetBuilder().build(FilteringTest.class.getResourceAsStream("initdb.xml"));
    }

    public IDatabaseConnection getConnection() throws SQLException, DatabaseUnitException {
        return new DatabaseConnection(dataSource.getConnection(), schema);
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

        IFiltering<TestEntity> filtering = em.filtering(TestEntity.class);
        List<TestEntity> testEntities = filtering.selectList();
        assertEquals(testEntities.size(), 200);
        Map<Long, TestEntity> mapById = filtering.selectMap("id");
        assertEquals(mapById.size(), 200);
        Map<Long, List<TestEntity>> id1 = filtering.selectMapList("intField");
        assertEquals(id1.size(), 2);
        assertEquals(id1.get(10).size(), 100);
        assertEquals(id1.get(20).size(), 100);
    }

    @Test
    public void testSelectIn() {
        IFiltering<TestEntity> filtering = em.filtering(TestEntity.class);

        filtering
                .setMaxSize(100)
                .filter(new Filter(true, Condition.in("id"), Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)))
                .orderBy(OrderBy.ASC("id"));

        List<TestEntity> testEntities = filtering.selectList();
        assertEquals(testEntities.size(), 10);
        Map<Long, TestEntity> id = filtering.selectMap("id");
        assertEquals(id.size(), 10);
        Map<Long, List<TestEntity>> id1 = filtering.selectMapList("intField");
        assertEquals(id1.size(), 1);
        assertEquals(id1.get(10).size(), 10);
    }


    @Test
    public void testSelectBetween() {
        IFiltering<TestEntity> filtering = em.filtering(TestEntity.class);

        filtering
                .setMaxSize(100)
                .filter(new Filter(true, Condition.between("id"), 0, 9))
                .orderBy(OrderBy.ASC("id"));

        List<TestEntity> testEntities = filtering.selectList();
        assertEquals(testEntities.size(), 10);
        Map<Long, TestEntity> id = filtering.selectMap("id");
        assertEquals(id.size(), 10);
        Map<Long, List<TestEntity>> id1 = filtering.selectMapList("intField");
        assertEquals(id1.size(), 1);
        assertEquals(id1.get(10).size(), 10);
    }


    @Test
    public void testSelectSomeFilters() {
        IFiltering<TestEntity> filtering = em.filtering(TestEntity.class);

        filtering
                .setMaxSize(100)
                .filter(new Filter(true, Condition.between("id"), 0, 9),
                        new Filter(true, Condition.isEqual("intField"), 10))
                .orderBy(OrderBy.ASC("id"));

        List<TestEntity> testEntities = filtering.selectList();
        assertEquals(testEntities.size(), 10);
        Map<Long, TestEntity> id = filtering.selectMap("id");
        assertEquals(id.size(), 10);
        Map<Long, List<TestEntity>> id1 = filtering.selectMapList("intField");
        assertEquals(id1.size(), 1);
        assertEquals(id1.get(10).size(), 10);
    }


    @Test
    public void testSelectSomeFiltersWithFalse() {
        IFiltering<TestEntity> filtering = em.filtering(TestEntity.class);

        filtering
                .setOffset(100)
                .filter(new Filter(false, Condition.between("id"), 0, 10),
                        new Filter(false, Condition.isEqual("intField"), 1))
                .orderBy(OrderBy.ASC("id"));

        List<TestEntity> testEntities = filtering.selectList();
        assertEquals(testEntities.size(), 100);
        Map<Long, TestEntity> mapById = filtering.selectMap("id");
        assertEquals(mapById.size(), 100);
        Map<Long, List<TestEntity>> id1 = filtering.selectMapList("intField");
        assertEquals(id1.size(), 1);
        assertEquals(id1.get(20).size(), 100);
    }


}
