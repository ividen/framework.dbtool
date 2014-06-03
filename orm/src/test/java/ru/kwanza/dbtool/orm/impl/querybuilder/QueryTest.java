package ru.kwanza.dbtool.orm.impl.querybuilder;

import junit.framework.Assert;
import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.SortedDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.core.ConnectionConfigListener;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.api.IStatement;
import ru.kwanza.dbtool.orm.api.If;
import ru.kwanza.dbtool.orm.impl.fetcher.TestEntity;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingRegistry;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * @author Alexander Guzanov
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class QueryTest extends AbstractJUnit4SpringContextTests {
    @Resource(name = "dbtool.IEntityManager")
    private IEntityManager em;
    @Resource(name = "dbtool.IEntityMappingRegistry")
    private EntityMappingRegistry registry;
    @Resource(name = "dataSource")
    public DataSource dataSource;


    @Component
    public static class InitDB {
        @Resource(name = "dbTester")
        private IDatabaseTester dbTester;

        private IDataSet getDataSet() throws Exception {
            return new FlatXmlDataSetBuilder().build(this.getClass().getResourceAsStream("initdb.xml"));
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
    public void testSimpleSelect() throws Exception {

        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).orderBy("id").create();
        System.out.println(query);
        IStatement<TestEntity> statement = query.prepare();
        List<TestEntity> testEntities = statement.selectList();
        assertEquals(testEntities.size(), 3000);
        Map<Long, TestEntity> mapById = statement.selectMap("id");
        assertEquals(mapById.size(), 3000);
        Map<Long, List<TestEntity>> id1 = statement.selectMapList("entityAID");
        assertEquals(id1.size(), 1500);
        assertEquals(id1.get(1l).size(), 2);
        assertEquals(id1.get(10l).size(), 2);
    }

    @Test
    public void testSelectIn() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).where(If.in("id")).orderBy("id").create();
        System.out.println(query);
        IStatement<TestEntity> statement = query.prepare().paging(0, 100);
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
        IQuery<TestEntity> query =
                em.queryBuilder(TestEntity.class).where(If.and(If.isGreaterOrEqual("id", "id"), If.isLessOrEqual("id", "id"))).orderBy("id")
                        .create();
        System.out.println(query);
        IStatement<TestEntity> statement = query.prepare().paging(0, 100);
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
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).orderBy("id").create();

        query.prepare().paging(0, 1000).setParameter(1, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testSelect_WrongParams_2() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).where(If.in("id")).orderBy("id").create();

        IStatement<TestEntity> statement = query.prepare().paging(0, 1000);
        statement.setParameter(1, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        statement.setParameter(2, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testSelect_WrongParams_3() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).where(If.in("id1")).orderBy("id").create();
        IStatement<TestEntity> statement = query.prepare().paging(0, 1000);
        statement.setParameter(1, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testSelect_WrongParams_4() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).where(If.in("id")).orderBy("id1").create();
        IStatement<TestEntity> statement = query.prepare().paging(0, 1000);
        statement.setParameter(1, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testSelect_groupField_5() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).orderBy("id").create();
        IStatement<TestEntity> statement = query.prepare().paging(0, 1000);
        statement.selectMap("title");
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testSelect_groupField_6() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).orderBy("id").create();
        IStatement<TestEntity> statement = query.prepare().paging(0, 1000);
        statement.selectMapList("title");
    }

    @Test
    public void testSelect_offset_0() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).orderBy("id").create();

        IStatement<TestEntity> statement = query.prepare().paging(99, 1);
        List<TestEntity> testEntities = statement.selectList();
        assertEquals(testEntities.size(), 1);
        Assert.assertEquals(testEntities.get(0).getId().longValue(), 99l);

        Map<Long, TestEntity> map = statement.selectMap("id");
        assertEquals(map.size(), 1);
        Assert.assertEquals(map.get(99l).getId().longValue(), 99l);
    }

    @Test
    public void testSelect_offset_1() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).orderBy("id").create();

        IStatement<TestEntity> statement = query.prepare().paging(0, 1);
        List<TestEntity> testEntities = statement.selectList();
        assertEquals(testEntities.size(), 1);
        Assert.assertEquals(testEntities.get(0).getId().longValue(), 0l);

        Map<Long, TestEntity> map = statement.selectMap("id");
        assertEquals(map.size(), 1);
        Assert.assertEquals(map.get(0l).getId().longValue(), 0l);
    }

    @Test
    public void testSelect_offset_2() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).orderBy("id").create();

        IStatement<TestEntity> statement = query.prepare().paging(99, 101);
        List<TestEntity> testEntities = statement.selectList();
        assertEquals(testEntities.size(), 101);
        Assert.assertEquals(testEntities.get(0).getId().longValue(), 99l);
    }

    @Test
    public void testSelect_offset_noteixts() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).where(If.isNull("id"))

                .orderBy("id").create();

        IStatement<TestEntity> statement = query.prepare().paging(1, 100);
        List<TestEntity> testEntities = statement.selectList();
        assertEquals(testEntities.size(), 0);
        Map<Long, TestEntity> map = statement.selectMap("id");
        assertEquals(map.size(), 0);
    }

    @Test
    public void testSelect_offset_greater() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).where(If.isEqual("id")).orderBy("id").create();
        IStatement<TestEntity> statement = query.prepare().paging(99, 1);
        List<TestEntity> testEntities = statement.setParameter(1, 100).selectList();
        assertEquals(testEntities.size(), 0);
        Map<Long, TestEntity> map = statement.setParameter(1, 100).selectMap("id");
        assertEquals(map.size(), 0);
    }

    @Test
    public void testSelect_single() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).where(If.isEqual("id")).orderBy("id").create();

        IStatement<TestEntity> statement = query.prepare();
        TestEntity select = statement.setParameter(1, 100l).select();
        assertEquals(select.getId().longValue(), 100l);
    }

    @Test(expected = IncorrectResultSizeDataAccessException.class)
    public void testSelect_single_wrong() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).where(If.isGreater("id")).orderBy("id").create();

        IStatement<TestEntity> statement = query.prepare();
        TestEntity select = statement.setParameter(1, 1).select();
        assertEquals(select.getId().longValue(), 100l);
    }

    @Test
    public void testNativeQuery_1() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).createNative("SELECT * FROM  test_entity where id in(?)");
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
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).createNative("SELECT * FROM  test_entity where id in(:ids)");
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
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).createNative("SELECT * " +
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
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).where(If.createNative("id in(:ids)")).create();
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
    public void testInnerJoin_1() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).where(If.isEqual("entityA.title")).create();
        final List<TestEntity> testEntities = query.prepare().setParameter(1, "test_entity_a1").selectList();
        assertEquals(2, testEntities.size());
        for (TestEntity testEntity : testEntities) {
            assertEquals(1l, testEntity.getEntityA().getId().longValue());
            assertNull(testEntity.getEntityB());
            assertNull(testEntity.getEntityC());
            assertNull(testEntity.getEntityD());
        }
    }

    @Test
    public void testInnerJoin_2() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).where(If.isEqual("entityA.title")).create();
        final List<TestEntity> testEntities = query.prepare().paging(0, 1).setParameter(1, "test_entity_a1").selectList();
        assertEquals(1, testEntities.size());
        for (TestEntity testEntity : testEntities) {
            assertEquals(1l, testEntity.getEntityA().getId().longValue());
            assertNull(testEntity.getEntityB());
            assertNull(testEntity.getEntityC());
            assertNull(testEntity.getEntityD());
        }
    }

    @Test
    public void testInnerJoin_3() {
        IQuery<TestEntity> query =
                em.queryBuilder(TestEntity.class).join("&entityA, &entityB, &entityC {&entityE{&entityG},!entityF} ,!entityD").create();
        final List<TestEntity> testEntities = query.prepare().selectList();
        assertEquals(3000, testEntities.size());
        for (TestEntity testEntity : testEntities) {
            assertEquals(testEntity.getEntityA().getId(), testEntity.getEntityAID());
            assertEquals(testEntity.getEntityB().getId(), testEntity.getEntityBID());
            assertEquals(testEntity.getEntityC().getId(), testEntity.getEntityCID());
            assertEquals(testEntity.getEntityD().getId(), testEntity.getEntityDID());
            assertEquals(testEntity.getEntityC().getEntityE().getId(), testEntity.getEntityC().getEntityEID());
            assertEquals(testEntity.getEntityC().getEntityF().getId(), testEntity.getEntityC().getEntityFID());
            assertEquals(testEntity.getEntityC().getEntityE().getEntityG().getId(), testEntity.getEntityC().getEntityE().getEntityGID());

        }
    }

    @Test
    public void testInnerJoin_4() {
        IQuery<TestEntity> query =
                em.queryBuilder(TestEntity.class).join("!entityA, !entityB, !entityC {!entityE{!entityG},!entityF} ,!entityD").create();
        final List<TestEntity> testEntities = query.prepare().selectList();
        assertEquals(3000, testEntities.size());
        for (TestEntity testEntity : testEntities) {
            assertEquals(testEntity.getEntityA().getId(), testEntity.getEntityAID());
            assertEquals(testEntity.getEntityB().getId(), testEntity.getEntityBID());
            assertEquals(testEntity.getEntityC().getId(), testEntity.getEntityCID());
            assertEquals(testEntity.getEntityD().getId(), testEntity.getEntityDID());
            assertEquals(testEntity.getEntityC().getEntityE().getId(), testEntity.getEntityC().getEntityEID());
            assertEquals(testEntity.getEntityC().getEntityF().getId(), testEntity.getEntityC().getEntityFID());
            assertEquals(testEntity.getEntityC().getEntityE().getEntityG().getId(), testEntity.getEntityC().getEntityE().getEntityGID());

        }
    }

    @Test
    public void testSelectDefaultParams_1() {
        IQuery<TestEntity> query =
                em.queryBuilder(TestEntity.class).where(If.in("id", If.valueOf(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)))).orderBy("id")
                        .create();
        System.out.println(query);
        IStatement<TestEntity> statement = query.prepare().paging(0, 100);
        List<TestEntity> testEntities = statement.selectList();
        assertEquals(testEntities.size(), 10);
        Map<Long, TestEntity> id = statement.selectMap("id");
        assertEquals(id.size(), 10);
        Map<Long, List<TestEntity>> id1 = statement.selectMapList("intField");
        assertEquals(id1.size(), 1);
        assertEquals(id1.get(10).size(), 10);
    }

    @Test
    public void testSelectDefaultParams_2() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).where(If
                .and(If.in("id", If.valueOf(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))), If.isEqual("intField", If.valueOf(10))))
                .orderBy("id").create();
        System.out.println(query);
        IStatement<TestEntity> statement = query.prepare().paging(0, 100);
        List<TestEntity> testEntities = statement.selectList();
        assertEquals(testEntities.size(), 10);
        Map<Long, TestEntity> id = statement.selectMap("id");
        assertEquals(id.size(), 10);
        Map<Long, List<TestEntity>> id1 = statement.selectMapList("intField");
        assertEquals(id1.size(), 1);
        assertEquals(id1.get(10).size(), 10);
    }

    @Test
    public void testSelectDefaultParams_3() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .where(If.and(If.in("id", If.valueOf(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))), If.isEqual("intField"))).orderBy("id")
                .create();
        System.out.println(query);
        IStatement<TestEntity> statement = query.prepare().paging(0, 100).setParameter(1, 10);
        List<TestEntity> testEntities = statement.selectList();
        assertEquals(testEntities.size(), 10);
        Map<Long, TestEntity> id = statement.selectMap("id");
        assertEquals(id.size(), 10);
        Map<Long, List<TestEntity>> id1 = statement.selectMapList("intField");
        assertEquals(id1.size(), 1);
        assertEquals(id1.get(10).size(), 10);
    }

    @Test
    public void testWithJoin_1() {

        final IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).join("&associatedEntityC,!entityC{!entityE,!entityF}").create();

        final List<TestEntity> testEntities = query.prepare().selectList();

        Assert.assertEquals(testEntities.size(), 3000);

        for (TestEntity testEntity : testEntities) {
            Assert.assertEquals(testEntity.getAssociatedEntityC().getId(), testEntity.getEntityC().getId());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getId(), testEntity.getEntityCID());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getEntityE().getId(), testEntity.getAssociatedEntityC().getEntityEID());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getEntityF().getId(), testEntity.getAssociatedEntityC().getEntityFID());
            assertNull(testEntity.getAssociatedEntityA());
            assertNull(testEntity.getAssociatedEntityC().getEntityE().getEntityG());
        }

    }

    @Test
    public void testWithJoin_2() {

        final IQuery<TestEntity> query =
                em.queryBuilder(TestEntity.class).join("&associatedEntityC,!entityC{!entityE,&entityF}").where(If.isEqual("entityAID"))
                        .create();

        final List<TestEntity> testEntities = query.prepare().setParameter(1, 0).selectList();

        Assert.assertEquals(testEntities.size(), 2);

        for (TestEntity testEntity : testEntities) {
            Assert.assertEquals(testEntity.getAssociatedEntityC().getId(), testEntity.getEntityC().getId());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getId(), testEntity.getEntityCID());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getEntityE().getId(), testEntity.getAssociatedEntityC().getEntityEID());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getEntityF().getId(), testEntity.getAssociatedEntityC().getEntityFID());
            assertNull(testEntity.getAssociatedEntityA());
            assertNull(testEntity.getAssociatedEntityC().getEntityE().getEntityG());
        }

    }

    @Test
    public void testWithJoin_3() {

        final IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).join("&associatedEntityC,!entityC{!entityE,&entityF}")
                .where(If.and(If.isEqual("intField", If.valueOf(10)), If.isEqual("entityAID"), If.isEqual("shortField", If.valueOf(1))))
                .create();

        final List<TestEntity> testEntities = query.prepare().setParameter(1, 0).selectList();

        Assert.assertEquals(testEntities.size(), 2);

        for (TestEntity testEntity : testEntities) {
            Assert.assertEquals(testEntity.getAssociatedEntityC().getId(), testEntity.getEntityC().getId());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getId(), testEntity.getEntityCID());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getEntityE().getId(), testEntity.getAssociatedEntityC().getEntityEID());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getEntityF().getId(), testEntity.getAssociatedEntityC().getEntityFID());
            assertNull(testEntity.getAssociatedEntityA());
            assertNull(testEntity.getAssociatedEntityC().getEntityE().getEntityG());
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithJoin_4() {

        final IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).join("&associatedEntityC,!entityC{!entityE,&entityF}")
                .where(If.and(If.isEqual("intField", If.valueOf(10)), If.isEqual("entityAID"), If.isEqual("shortField", If.valueOf(1))))
                .create();

        final List<TestEntity> testEntities = query.prepare().setParameter(1, 0).setParameter(2,1).selectList();
    }

    @Test
    public void testWithJoin_5() {

        final IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).join("associatedEntityC,entityC{entityE,entityF}")
                .where(If.and(If.isEqual("intField", If.valueOf(10)), If.isEqual("entityAID"), If.isEqual("shortField", If.valueOf(1))))
                .create();

        final List<TestEntity> testEntities = query.prepare().setParameter(1, 0).selectList();

        Assert.assertEquals(testEntities.size(), 2);

        for (TestEntity testEntity : testEntities) {
            Assert.assertEquals(testEntity.getAssociatedEntityC().getId(), testEntity.getEntityC().getId());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getId(), testEntity.getEntityCID());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getEntityE().getId(), testEntity.getAssociatedEntityC().getEntityEID());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getEntityF().getId(), testEntity.getAssociatedEntityC().getEntityFID());
            assertNull(testEntity.getAssociatedEntityA());
            assertNull(testEntity.getAssociatedEntityC().getEntityE().getEntityG());
        }

    }

    @Test
    public void testWithJoin_6() {

        final IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).join("!associatedEntityC,entityC{entityE,entityF}")
                .where(If.and(If.isEqual("intField", If.valueOf(10)), If.isEqual("entityAID"), If.isEqual("shortField", If.valueOf(1))))
                .create();

        final List<TestEntity> testEntities = query.prepare().setParameter(1, 0).selectList();

        Assert.assertEquals(testEntities.size(), 2);

        for (TestEntity testEntity : testEntities) {
            Assert.assertEquals(testEntity.getAssociatedEntityC().getId(), testEntity.getEntityC().getId());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getId(), testEntity.getEntityCID());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getEntityE().getId(), testEntity.getAssociatedEntityC().getEntityEID());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getEntityF().getId(), testEntity.getAssociatedEntityC().getEntityFID());
            assertNull(testEntity.getAssociatedEntityA());
            assertNull(testEntity.getAssociatedEntityC().getEntityE().getEntityG());
        }

    }

    @Test
    public void testWithJoin_7() {

        final IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).join("!associatedEntityC,entityC{!entityE,entityF}")
                .where(If.and(If.isEqual("intField", If.valueOf(10)), If.isEqual("entityAID"), If.isEqual("shortField", If.valueOf(1))))
                .create();

        final List<TestEntity> testEntities = query.prepare().setParameter(1, 0).selectList();

        Assert.assertEquals(testEntities.size(), 2);

        for (TestEntity testEntity : testEntities) {
            Assert.assertEquals(testEntity.getAssociatedEntityC().getId(), testEntity.getEntityC().getId());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getId(), testEntity.getEntityCID());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getEntityE().getId(), testEntity.getAssociatedEntityC().getEntityEID());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getEntityF().getId(), testEntity.getAssociatedEntityC().getEntityFID());
            assertNull(testEntity.getAssociatedEntityA());
            assertNull(testEntity.getAssociatedEntityC().getEntityE().getEntityG());
        }

    }


    @Test
    public void testWithJoin_8() {

        final IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).join("!associatedEntityC,entityC{!entityE,!entityF}")
                .where(If.and(If.isEqual("intField", If.valueOf(10)), If.isEqual("entityAID"), If.isEqual("shortField", If.valueOf(1))))
                .create();

        final List<TestEntity> testEntities = query.prepare().setParameter(1, 0).selectList();

        Assert.assertEquals(testEntities.size(), 2);

        for (TestEntity testEntity : testEntities) {
            Assert.assertEquals(testEntity.getAssociatedEntityC().getId(), testEntity.getEntityC().getId());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getId(), testEntity.getEntityCID());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getEntityE().getId(), testEntity.getAssociatedEntityC().getEntityEID());
            Assert.assertEquals(testEntity.getAssociatedEntityC().getEntityF().getId(), testEntity.getAssociatedEntityC().getEntityFID());
            assertNull(testEntity.getAssociatedEntityA());
            assertNull(testEntity.getAssociatedEntityC().getEntityE().getEntityG());
        }

    }

}
