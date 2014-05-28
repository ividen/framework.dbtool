package ru.kwanza.dbtool.orm.impl.filtering;

import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.core.ConnectionConfigListener;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.api.IFiltering;
import ru.kwanza.dbtool.orm.api.If;
import ru.kwanza.dbtool.orm.impl.fetcher.TestEntity;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingRegistry;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * @author Alexander Guzanov
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class FilteringTest extends AbstractJUnit4SpringContextTests {
    @Resource(name = "dbtool.IEntityManager")
    private IEntityManager em;

    @Resource(name = "dataSource")
    private DataSource dataSource;

    @Component
    public static class InitDB {
        @Resource(name = "dbTester")
        private IDatabaseTester dbTester;
        @Resource(name = "dbtool.IEntityMappingRegistry")
        private EntityMappingRegistry registry;

        private IDataSet getDataSet() throws Exception {
            return new FlatXmlDataSetBuilder().build(this.getClass().getResourceAsStream("initdb.xml"));
        }

        @PostConstruct
        protected void init() throws Exception {
            registry.registerEntityClass(TestEntity.class);
            dbTester.setDataSet(getDataSet());
            dbTester.setOperationListener(new ConnectionConfigListener());
            dbTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
            dbTester.onSetup();
        }
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
                .paging(0, 100)
                .filter(true, If.in("id"), Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
                .orderBy("id");

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
                .paging(0, 100)
                .filter(true, If.between("id"), 0, 9)
                .orderBy("id");

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
                .paging(0, 100)
                .filter(true, If.between("id"), 0, 9)
                .filter(If.isEqual("intField"), 10)
                .orderBy("id");

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
                .paging(100, 100)
                .filter(false, If.between("id"), 0, 10)
                .filter(false, If.isEqual("intField"), 1)
                .orderBy("id");

        List<TestEntity> testEntities = filtering.selectList();
        assertEquals(testEntities.size(), 100);
        Map<Long, TestEntity> mapById = filtering.selectMap("id");
        assertEquals(mapById.size(), 100);
        Map<Long, List<TestEntity>> id1 = filtering.selectMapList("intField");
        assertEquals(id1.size(), 1);
        assertEquals(id1.get(20).size(), 100);
    }

}
