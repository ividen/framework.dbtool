package ru.kwanza.dbtool.orm.impl.fetcher;

import junit.framework.Assert;
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
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.api.If;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingRegistry;
import ru.kwanza.toolbox.SerializationHelper;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Test fetching in hierarchy:  TestEntity1{TestEntityA,TestEntityB,TestEntityC{TestEntityF{TestEntityG},TestEntityC},TestEntityD}
 *
 * @author Alexander Guzanov
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class TestFetcherIml extends AbstractJUnit4SpringContextTests {

    @Resource(name = "dbtool.IEntityManager")
    private IEntityManager em;
    @Resource(name = "dbtool.IEntityMappingRegistry")
    private EntityMappingRegistry registry;


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
    public void testFetch1() {
        List<TestEntity> testEntities = query().prepare().selectList();
        em.fetch(TestEntity.class, testEntities, "entityA,entityB,entityC{entityF,entityE{entityG}},entityD");
        for (int i = 0; i < 1500; i++) {
            TestEntity testEntity = testEntities.get(i);
            Assert.assertEquals(testEntity.getId().intValue(), i);
            Assert.assertEquals(testEntity.getEntityA().getId().intValue(), i);
            Assert.assertEquals(testEntity.getEntityB().getId().intValue() - 1500, i);
            Assert.assertEquals(testEntity.getEntityC().getId().intValue() - 9000, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityE().getId().intValue() - 7500, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityF().getId().intValue() - 4500, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityE().getEntityG().getId().intValue() - 6000, i);
            Assert.assertEquals(testEntity.getEntityD().getId().intValue() - 3000, i);
        }

        for (int i = 0; i < 1500; i++) {
            TestEntity testEntity = testEntities.get(i + 1500);
            Assert.assertEquals(testEntity.getId().intValue(), i + 1500);
            Assert.assertEquals(testEntity.getEntityA().getId().intValue(), i);
            Assert.assertEquals(testEntity.getEntityB().getId().intValue() - 1500, i);
            Assert.assertEquals(testEntity.getEntityC().getId().intValue() - 9000, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityE().getId().intValue() - 7500, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityF().getId().intValue() - 4500, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityE().getEntityG().getId().intValue() - 6000, i);
            Assert.assertEquals(testEntity.getEntityD().getId().intValue() - 3000, i);
        }
    }

    private IQuery<TestEntity> query() {
        return em.queryBuilder(TestEntity.class).orderBy("id ASC").create();
    }

    private IQuery<TestEntityA> queryEntityA() {
        return em.queryBuilder(TestEntityA.class).orderBy("id ASC").create();
    }

    @Test
    public void testFetch2() {
        List<TestEntity> testEntities = query().prepare().selectList();
        em.fetch(TestEntity.class, testEntities, "entityA,entityB,entityC{entityF,entityE},entityD");
        for (int i = 0; i < 1500; i++) {
            TestEntity testEntity = testEntities.get(i);
            Assert.assertEquals(testEntity.getId().intValue(), i);
            Assert.assertEquals(testEntity.getEntityA().getId().intValue(), i);
            Assert.assertEquals(testEntity.getEntityB().getId().intValue() - 1500, i);
            Assert.assertEquals(testEntity.getEntityC().getId().intValue() - 9000, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityE().getId().intValue() - 7500, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityF().getId().intValue() - 4500, i);
            Assert.assertNull(testEntity.getEntityC().getEntityE().getEntityG());
            Assert.assertEquals(testEntity.getEntityD().getId().intValue() - 3000, i);
        }

        for (int i = 0; i < 1500; i++) {
            TestEntity testEntity = testEntities.get(i + 1500);
            Assert.assertEquals(testEntity.getId().intValue(), i + 1500);
            Assert.assertEquals(testEntity.getEntityA().getId().intValue(), i);
            Assert.assertEquals(testEntity.getEntityB().getId().intValue() - 1500, i);
            Assert.assertEquals(testEntity.getEntityC().getId().intValue() - 9000, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityE().getId().intValue() - 7500, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityF().getId().intValue() - 4500, i);
            Assert.assertNull(testEntity.getEntityC().getEntityE().getEntityG());
            Assert.assertEquals(testEntity.getEntityD().getId().intValue() - 3000, i);
        }
    }

    @Test
    public void testFetch3() {
        List<TestEntity> testEntities = query().prepare().selectList();
        em.fetch(TestEntity.class, testEntities, "entityA,entityB,entityC,entityD");
        for (int i = 0; i < 1500; i++) {
            TestEntity testEntity = testEntities.get(i);
            Assert.assertEquals(testEntity.getId().intValue(), i);
            Assert.assertEquals(testEntity.getEntityA().getId().intValue(), i);
            Assert.assertEquals(testEntity.getEntityB().getId().intValue() - 1500, i);
            Assert.assertEquals(testEntity.getEntityC().getId().intValue() - 9000, i);
            Assert.assertNull(testEntity.getEntityC().getEntityE());
            Assert.assertNull(testEntity.getEntityC().getEntityF());
            Assert.assertEquals(testEntity.getEntityD().getId().intValue() - 3000, i);
        }

        for (int i = 0; i < 1500; i++) {
            TestEntity testEntity = testEntities.get(i + 1500);
            Assert.assertEquals(testEntity.getId().intValue(), i + 1500);
            Assert.assertEquals(testEntity.getEntityA().getId().intValue(), i);
            Assert.assertEquals(testEntity.getEntityB().getId().intValue() - 1500, i);
            Assert.assertEquals(testEntity.getEntityC().getId().intValue() - 9000, i);
            Assert.assertNull(testEntity.getEntityC().getEntityE());
            Assert.assertNull(testEntity.getEntityC().getEntityF());
            Assert.assertEquals(testEntity.getEntityD().getId().intValue() - 3000, i);
        }
    }

    @Test
    public void testFetch4() {
        List<TestEntity> testEntities = query().prepare().selectList();
        em.fetch(TestEntity.class, testEntities, "entityC{entityE,entityF}");
        for (int i = 0; i < 1500; i++) {
            TestEntity testEntity = testEntities.get(i);
            Assert.assertEquals(testEntity.getId().intValue(), i);
            Assert.assertNull(testEntity.getEntityA());
            Assert.assertNull(testEntity.getEntityB());
            Assert.assertEquals(testEntity.getEntityC().getId().intValue() - 9000, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityE().getId().intValue() - 7500, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityF().getId().intValue() - 4500, i);
            Assert.assertNull(testEntity.getEntityD());
        }

        for (int i = 0; i < 1500; i++) {
            TestEntity testEntity = testEntities.get(i + 1500);
            Assert.assertEquals(testEntity.getId().intValue(), i + 1500);
            Assert.assertNull(testEntity.getEntityA());
            Assert.assertNull(testEntity.getEntityB());
            Assert.assertEquals(testEntity.getEntityC().getId().intValue() - 9000, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityE().getId().intValue() - 7500, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityF().getId().intValue() - 4500, i);
            Assert.assertNull(testEntity.getEntityD());
        }
    }

    @Test
    public void testFetch5() {
        IQuery<TestEntityC> query = em.queryBuilder(TestEntityC.class).orderBy("id").create();
        List<TestEntityC> testEntities = query.prepare().selectList();
        em.fetch(TestEntityC.class, testEntities, "entityF,entityE{entityG}");

        for (int i = 0; i < 1500; i++) {
            TestEntityC testEntity = testEntities.get(i);
            Assert.assertEquals(testEntity.getId().intValue() - 9000, i);
            Assert.assertEquals(testEntity.getEntityE().getId().intValue() - 7500, i);
            Assert.assertEquals(testEntity.getEntityF().getId().intValue() - 4500, i);
            Assert.assertEquals(testEntity.getEntityE().getEntityG().getId().intValue() - 6000, i);
        }
    }

    @Test
    public void testFetch6() {
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).where(If.isLess("id")).create();
        List<TestEntity> testEntities = query.prepare().setParameter(1, 0l).selectList();
        em.fetch(TestEntity.class, testEntities, "entityA,entityB,entityC{entityF,entityE{entityG}},entityD");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFetch7() {
        List<TestEntityC> testEntities = em.queryBuilder(TestEntityC.class).create().prepare().selectList();
        em.fetch(TestEntityC.class, testEntities, "entityF,entityE{entityG},entityA");

    }

    @Test
    public void testFetch8() {
        IQuery<TestEntityG> query = em.queryBuilder(TestEntityG.class).create();
        List<TestEntityG> testEntities = query.prepare().selectList();
        em.fetch(TestEntityG.class, testEntities, "entitiesE{entitiesC{testEntities{entityA,entityB,entityD},entityF}}");
        long count = 0;
        for (TestEntityG g : testEntities) {
            for (TestEntityE e : g.getEntitiesE()) {
                for (TestEntityC c : e.getEntitiesC()) {
                    count += c.getTestEntities().size();
                }
            }

        }
        Assert.assertEquals(count, 3000);
    }

    @Test
    public void testNoEntityFetch1() {
        List<TestEntity> testEntities = query().prepare().selectList();

        List<TestEvent> testEvents = new ArrayList<TestEvent>(testEntities.size());

        for (TestEntity testEntity : testEntities) {
            testEvents.add(new TestEvent(testEntity.getId()));
        }

        em.fetch(TestEvent.class, testEvents, "testEntity{entityA,entityB,entityC{entityF,entityE{entityG}},entityD}");
        for (int i = 0; i < 1500; i++) {
            final TestEvent testEvent = testEvents.get(i);
            TestEntity testEntity = testEvent.getTestEntity();
            Assert.assertEquals(testEntity.getId().intValue(), testEvent.getEntityId().intValue());

            Assert.assertEquals(testEntity.getId().intValue(), i);
            Assert.assertEquals(testEntity.getEntityA().getId().intValue(), i);
            Assert.assertEquals(testEntity.getEntityB().getId().intValue() - 1500, i);
            Assert.assertEquals(testEntity.getEntityC().getId().intValue() - 9000, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityE().getId().intValue() - 7500, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityF().getId().intValue() - 4500, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityE().getEntityG().getId().intValue() - 6000, i);
            Assert.assertEquals(testEntity.getEntityD().getId().intValue() - 3000, i);
        }

        for (int i = 0; i < 1500; i++) {
            final TestEvent testEvent = testEvents.get(i + 1500);
            TestEntity testEntity = testEvent.getTestEntity();
            Assert.assertEquals(testEntity.getId().intValue(), testEvent.getEntityId().intValue());
            Assert.assertEquals(testEntity.getId().intValue(), i + 1500);
            Assert.assertEquals(testEntity.getEntityA().getId().intValue(), i);
            Assert.assertEquals(testEntity.getEntityB().getId().intValue() - 1500, i);
            Assert.assertEquals(testEntity.getEntityC().getId().intValue() - 9000, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityE().getId().intValue() - 7500, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityF().getId().intValue() - 4500, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityE().getEntityG().getId().intValue() - 6000, i);
            Assert.assertEquals(testEntity.getEntityD().getId().intValue() - 3000, i);
        }
    }

    @Test
    public void testNoEntityFetch2() {
        List<TestEntityA> testEntities = queryEntityA().prepare().selectList();

        List<TestEventWithAssociation> testEvents = new ArrayList<TestEventWithAssociation>(testEntities.size());

        for (TestEntityA testEntity : testEntities) {
            testEvents.add(new TestEventWithAssociation(testEntity.getId()));
        }

        em.fetch(TestEventWithAssociation.class, testEvents, "entities{entityB,entityC{entityF,entityE{entityG}},entityD}");

        for (TestEventWithAssociation testEvent : testEvents) {
            Assert.assertEquals(testEvent.getEntities().size(), 2);
            for (TestEntity testEntity : testEvent.getEntities()) {
                Assert.assertEquals(testEntity.getEntityBID(), testEntity.getEntityB().getId());
                Assert.assertEquals(testEntity.getEntityCID(), testEntity.getEntityC().getId());
                Assert.assertEquals(testEntity.getEntityDID(), testEntity.getEntityD().getId());

                Assert.assertEquals(testEntity.getEntityC().getEntityEID(), testEntity.getEntityC().getEntityE().getId());
                Assert.assertEquals(testEntity.getEntityC().getEntityFID(), testEntity.getEntityC().getEntityF().getId());

                Assert.assertEquals(testEntity.getEntityC().getEntityE().getEntityGID(),
                        testEntity.getEntityC().getEntityE().getEntityG().getId());
            }
        }

    }
    @Test
    public void testLazyFetch_1() throws Exception {
        List<TestEntity> testEntities = query().prepare().selectList();


        em.fetchLazy(TestEntity.class, testEntities);

        final byte[] bytes = SerializationHelper.objectToBytes(applicationContext, testEntities);
        System.out.println(bytes.length);
        testEntities = (List<TestEntity>) SerializationHelper.bytesToObject(applicationContext, bytes);

        for (TestEntity testEntity : testEntities) {
            Assert.assertEquals(testEntity.getEntityAID(), testEntity.getEntityA().getId());
            Assert.assertEquals(testEntity.getEntityBID(), testEntity.getEntityB().getId());
            Assert.assertEquals(testEntity.getEntityCID(), testEntity.getEntityC().getId());
            Assert.assertEquals(testEntity.getEntityDID(), testEntity.getEntityD().getId());

            Assert.assertEquals(testEntity.getEntityC().getEntityEID(), testEntity.getEntityC().getEntityE().getId());
            Assert.assertEquals(testEntity.getEntityC().getEntityFID(), testEntity.getEntityC().getEntityF().getId());
//
            Assert.assertEquals(testEntity.getEntityC().getEntityE().getEntityGID(),
                    testEntity.getEntityC().getEntityE().getEntityG().getId());
        }

        System.out.println(SerializationHelper.objectToBytes(applicationContext, testEntities).length);

    }

    @Test
    public void testLazyFetch_2() {
        List<TestEntityA> testEntityAs = queryEntityA().prepare().selectList();
        em.fetchLazy(TestEntityA.class, testEntityAs);

        for (TestEntityA testEntityA : testEntityAs) {
            Assert.assertEquals(testEntityA.getTestEntities().size(), 2);
            for (TestEntity testEntity : testEntityA.getTestEntities()) {
                Assert.assertEquals(testEntity.getEntityAID(), testEntityA.getId());
                Assert.assertEquals(testEntity.getEntityBID(), testEntity.getEntityB().getId());
            }
        }
    }

    @Test
    public void testLazyFetch_3() {
        List<TestEntity> testEntities = query().prepare().selectList();

        List<TestEvent> testEvents = new ArrayList<TestEvent>(testEntities.size());

        for (TestEntity testEntity : testEntities) {
            testEvents.add(new TestEvent(testEntity.getId()));
        }

        em.fetchLazy(TestEvent.class, testEvents);
        for (int i = 0; i < 1500; i++) {
            final TestEvent testEvent = testEvents.get(i);
            TestEntity testEntity = testEvent.getTestEntity();
            Assert.assertEquals(testEntity.getId().intValue(), testEvent.getEntityId().intValue());

            Assert.assertEquals(testEntity.getId().intValue(), i);
            Assert.assertEquals(testEntity.getEntityA().getId().intValue(), i);
            Assert.assertEquals(testEntity.getEntityB().getId().intValue() - 1500, i);
            Assert.assertEquals(testEntity.getEntityC().getId().intValue() - 9000, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityE().getId().intValue() - 7500, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityF().getId().intValue() - 4500, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityE().getEntityG().getId().intValue() - 6000, i);
            Assert.assertEquals(testEntity.getEntityD().getId().intValue() - 3000, i);
        }

        for (int i = 0; i < 1500; i++) {
            final TestEvent testEvent = testEvents.get(i + 1500);
            TestEntity testEntity = testEvent.getTestEntity();
            Assert.assertEquals(testEntity.getId().intValue(), testEvent.getEntityId().intValue());
            Assert.assertEquals(testEntity.getId().intValue(), i + 1500);
            Assert.assertEquals(testEntity.getEntityA().getId().intValue(), i);
            Assert.assertEquals(testEntity.getEntityB().getId().intValue() - 1500, i);
            Assert.assertEquals(testEntity.getEntityC().getId().intValue() - 9000, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityE().getId().intValue() - 7500, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityF().getId().intValue() - 4500, i);
            Assert.assertEquals(testEntity.getEntityC().getEntityE().getEntityG().getId().intValue() - 6000, i);
            Assert.assertEquals(testEntity.getEntityD().getId().intValue() - 3000, i);
        }
    }

    @Test
    public void testLazyFetch_4() {
        List<TestEntityA> testEntities = queryEntityA().prepare().selectList();

        List<TestEventWithAssociation> testEvents = new ArrayList<TestEventWithAssociation>(testEntities.size());

        for (TestEntityA testEntity : testEntities) {
            testEvents.add(new TestEventWithAssociation(testEntity.getId()));
        }

        em.fetchLazy(TestEventWithAssociation.class, testEvents);

        for (TestEventWithAssociation testEvent : testEvents) {
            Assert.assertEquals(testEvent.getEntities().size(), 2);
            for (TestEntity testEntity : testEvent.getEntities()) {
                Assert.assertEquals(testEntity.getEntityBID(), testEntity.getEntityB().getId());
                Assert.assertEquals(testEntity.getEntityCID(), testEntity.getEntityC().getId());
                Assert.assertEquals(testEntity.getEntityDID(), testEntity.getEntityD().getId());

                Assert.assertEquals(testEntity.getEntityC().getEntityEID(), testEntity.getEntityC().getEntityE().getId());
                Assert.assertEquals(testEntity.getEntityC().getEntityFID(), testEntity.getEntityC().getEntityF().getId());

                Assert.assertEquals(testEntity.getEntityC().getEntityE().getEntityGID(),
                        testEntity.getEntityC().getEntityE().getEntityG().getId());
            }
        }

    }

    @Test
    public void testLazyFetch_5() throws Exception {
        List<TestEntity> testEntities = query().prepare().selectList();
        em.fetchLazy(TestEntity.class, testEntities);

        for (TestEntity testEntity : testEntities) {
            Assert.assertEquals(testEntity.getEntityAID(), testEntity.getEntityA().getId());
        }

        final byte[] bytes = SerializationHelper.objectToBytes(testEntities);

        List<TestEntity> result = (List) SerializationHelper.bytesToObject(applicationContext, bytes);
        for (TestEntity testEntity : testEntities) {
            Assert.assertEquals(testEntity.getEntityAID(), testEntity.getEntityA().getId());
            Assert.assertEquals(testEntity.getEntityBID(), testEntity.getEntityB().getId());
            Assert.assertEquals(testEntity.getEntityCID(), testEntity.getEntityC().getId());
            Assert.assertEquals(testEntity.getEntityDID(), testEntity.getEntityD().getId());

            Assert.assertEquals(testEntity.getEntityC().getEntityEID(), testEntity.getEntityC().getEntityE().getId());
            Assert.assertEquals(testEntity.getEntityC().getEntityFID(), testEntity.getEntityC().getEntityF().getId());

            Assert.assertEquals(testEntity.getEntityC().getEntityE().getEntityGID(),
                    testEntity.getEntityC().getEntityE().getEntityG().getId());
        }

    }

    @Test
    public void testAssociationWithCondition_1() {
        List<TestEntity> testEntities = query().prepare().selectList();

        List<TestEventWithIfAssociation> testEvents = new ArrayList<TestEventWithIfAssociation>(testEntities.size());

        for (TestEntity testEntity : testEntities) {
            testEvents.add(new TestEventWithIfAssociation(testEntity.getId()));
        }

        em.fetchLazy(TestEventWithIfAssociation.class, testEvents);

        for (TestEventWithIfAssociation testEvent : testEvents) {
            final TestEntity testEntity = testEvent.getEntity();
            Assert.assertEquals(testEntity.getEntityAID(), testEntity.getEntityA().getId());
            Assert.assertEquals(testEntity.getEntityBID(), testEntity.getEntityB().getId());
            Assert.assertEquals(testEntity.getEntityCID(), testEntity.getEntityC().getId());
            Assert.assertEquals(testEntity.getEntityDID(), testEntity.getEntityD().getId());

            Assert.assertEquals(testEntity.getEntityC().getEntityEID(), testEntity.getEntityC().getEntityE().getId());
            Assert.assertEquals(testEntity.getEntityC().getEntityFID(), testEntity.getEntityC().getEntityF().getId());

            Assert.assertEquals(testEntity.getEntityC().getEntityE().getEntityGID(),
                    testEntity.getEntityC().getEntityE().getEntityG().getId());
        }

    }

    @Test
    public void testAssociationWithCondition_2() {
        List<TestEntity> testEntities = query().prepare().selectList();

        List<TestEventWithIfAssociation> testEvents = new ArrayList<TestEventWithIfAssociation>(testEntities.size());

        for (TestEntity testEntity : testEntities) {
            testEvents.add(new TestEventWithIfAssociation(testEntity.getId()));
        }

        em.fetch(TestEventWithIfAssociation.class, testEvents, "entity{entityA,entityB,entityC{entityF,entityE{entityG}},entityD}");

        for (TestEventWithIfAssociation testEvent : testEvents) {
            final TestEntity testEntity = testEvent.getEntity();
            Assert.assertEquals(testEntity.getEntityAID(), testEntity.getEntityA().getId());
            Assert.assertEquals(testEntity.getEntityBID(), testEntity.getEntityB().getId());
            Assert.assertEquals(testEntity.getEntityCID(), testEntity.getEntityC().getId());
            Assert.assertEquals(testEntity.getEntityDID(), testEntity.getEntityD().getId());

            Assert.assertEquals(testEntity.getEntityC().getEntityEID(), testEntity.getEntityC().getEntityE().getId());
            Assert.assertEquals(testEntity.getEntityC().getEntityFID(), testEntity.getEntityC().getEntityF().getId());

            Assert.assertEquals(testEntity.getEntityC().getEntityE().getEntityGID(),
                    testEntity.getEntityC().getEntityE().getEntityG().getId());
        }
    }

    @Test
    public void testAssociationWithGroupBy_1() {
        List<TestEntityA> testEntities = queryEntityA().prepare().selectList();

        List<TestEventWithAssociation> testEvents = new ArrayList<TestEventWithAssociation>(testEntities.size());

        for (TestEntityA testEntity : testEntities) {
            testEvents.add(new TestEventWithAssociation(testEntity.getId()));
        }

        em.fetch(TestEventWithAssociation.class, testEvents, "entitiesById");

        for (TestEventWithAssociation testEvent : testEvents) {
            final Map<Long, TestEntity> entitiesById = testEvent.getEntitiesById();
            for (Map.Entry<Long, TestEntity> e : entitiesById.entrySet()) {
                Assert.assertEquals(e.getKey(), e.getValue().getId());
                Assert.assertNull(e.getValue().getEntityA());
                Assert.assertNull(e.getValue().getEntityB());
                Assert.assertNull(e.getValue().getEntityC());
                Assert.assertNull(e.getValue().getEntityD());
            }
        }
    }

    @Test
    public void testAssociationWithGroupBy_2() {
        List<TestEntityA> testEntities = queryEntityA().prepare().selectList();

        List<TestEventWithAssociation> testEvents = new ArrayList<TestEventWithAssociation>(testEntities.size());

        for (TestEntityA testEntity : testEntities) {
            testEvents.add(new TestEventWithAssociation(testEntity.getId()));
        }

        em.fetch(TestEventWithAssociation.class, testEvents, "entitiesByEntityA");

        for (TestEventWithAssociation testEvent : testEvents) {
            final Map<TestEntityA, List<TestEntity>> entitiesByEntityA = testEvent.getEntitiesByEntityA();
            for (Map.Entry<TestEntityA, List<TestEntity>> entry : entitiesByEntityA.entrySet()) {
                for (TestEntity testEntity : entry.getValue()) {
                    Assert.assertEquals(entry.getKey(), testEntity.getEntityA());
                    Assert.assertNull(testEntity.getEntityB());
                    Assert.assertNull(testEntity.getEntityC());
                    Assert.assertNull(testEntity.getEntityD());
                }
            }
        }
    }

    @Test
    public void testAssociationWithGroupBy_3() {
        List<TestEntityA> testEntities = queryEntityA().prepare().selectList();

        List<TestEventWithAssociation> testEvents = new ArrayList<TestEventWithAssociation>(testEntities.size());

        for (TestEntityA testEntity : testEntities) {
            testEvents.add(new TestEventWithAssociation(testEntity.getId()));
        }

        em.fetch(TestEventWithAssociation.class, testEvents, "entitiesByACEId");

        for (TestEventWithAssociation testEvent : testEvents) {
            final Map<TestEntityA, Map<Long, List<TestEntity>>> entitiesByACEId = testEvent.getEntitiesByACEId();
            for (Map.Entry<TestEntityA, Map<Long, List<TestEntity>>> e : entitiesByACEId.entrySet()) {
                for (Map.Entry<Long, List<TestEntity>> entry : e.getValue().entrySet()) {
                    for (TestEntity testEntity : entry.getValue()) {
                        Assert.assertEquals(e.getKey(), testEntity.getEntityA());
                        Assert.assertNull(testEntity.getEntityB());
                        Assert.assertNotNull(testEntity.getEntityC());
                        Assert.assertEquals(entry.getKey(), testEntity.getEntityC().getEntityE().getId());
                        Assert.assertNotNull(testEntity.getEntityC().getEntityE());
                        Assert.assertNull(testEntity.getEntityD());
                    }
                }
            }
        }
    }

    @Test
    public void testAssociationWithGroupBy_4() {
        List<TestEntityA> testEntities = queryEntityA().prepare().selectList();

        List<TestEventWithAssociation> testEvents = new ArrayList<TestEventWithAssociation>(testEntities.size());

        for (TestEntityA testEntity : testEntities) {
            testEvents.add(new TestEventWithAssociation(testEntity.getId()));
        }

        em.fetch(TestEventWithAssociation.class, testEvents, "entitiesById{entityA,entityB,entityC{entityF},entityD}");

        for (TestEventWithAssociation testEvent : testEvents) {
            final Map<Long, TestEntity> entitiesById = testEvent.getEntitiesById();
            for (Map.Entry<Long, TestEntity> e : entitiesById.entrySet()) {
                Assert.assertEquals(e.getKey(), e.getValue().getId());
                Assert.assertEquals(e.getValue().getEntityA().getId(),e.getValue().getEntityAID());
                Assert.assertEquals(e.getValue().getEntityB().getId(),e.getValue().getEntityBID());
                Assert.assertEquals(e.getValue().getEntityC().getId(),e.getValue().getEntityCID());
                Assert.assertEquals(e.getValue().getEntityC().getEntityF().getId(),e.getValue().getEntityC().getEntityFID());
                Assert.assertNull(e.getValue().getEntityC().getEntityE());
                Assert.assertEquals(e.getValue().getEntityD().getId(),e.getValue().getEntityDID());
            }
        }
    }


    @Test
    public void testAssociationWithGroupBy_5() {
        List<TestEntityA> testEntities = queryEntityA().prepare().selectList();

        List<TestEventWithAssociation> testEvents = new ArrayList<TestEventWithAssociation>(testEntities.size());

        for (TestEntityA testEntity : testEntities) {
            testEvents.add(new TestEventWithAssociation(testEntity.getId()));
        }

        em.fetch(TestEventWithAssociation.class, testEvents, "entitiesByACEId{entityA,entityB,entityC,entityD}");

        for (TestEventWithAssociation testEvent : testEvents) {
            final Map<TestEntityA, Map<Long, List<TestEntity>>> entitiesByACEId = testEvent.getEntitiesByACEId();
            for (Map.Entry<TestEntityA, Map<Long, List<TestEntity>>> e : entitiesByACEId.entrySet()) {
                for (Map.Entry<Long, List<TestEntity>> entry : e.getValue().entrySet()) {
                    for (TestEntity testEntity : entry.getValue()) {
                        Assert.assertNotNull(testEntity.getEntityC());
                        Assert.assertEquals(entry.getKey(), testEntity.getEntityC().getEntityE().getId());
                        Assert.assertNotNull(testEntity.getEntityC().getEntityE());

                        Assert.assertEquals(testEntity.getEntityA().getId(),testEntity.getEntityAID());
                        Assert.assertEquals(testEntity.getEntityB().getId(),testEntity.getEntityBID());
                        Assert.assertEquals(testEntity.getEntityC().getId(),testEntity.getEntityCID());
                        Assert.assertEquals(testEntity.getEntityD().getId(),testEntity.getEntityDID());
                        
                        
                    }
                }
            }
        }
    }


    @Test
    public void testAssociationWithGroupBy_6() {
        List<TestEntityA> testEntities = queryEntityA().prepare().selectList();

        List<TestEventWithAssociation> testEvents = new ArrayList<TestEventWithAssociation>(testEntities.size());

        for (TestEntityA testEntity : testEntities) {
            testEvents.add(new TestEventWithAssociation(testEntity.getId()));
        }

        em.fetchLazy(TestEventWithAssociation.class, testEvents);


        for (TestEventWithAssociation testEvent : testEvents) {
            final Map<Long, TestEntity> entitiesById = testEvent.getEntitiesById();
            for (Map.Entry<Long, TestEntity> e : entitiesById.entrySet()) {
                Assert.assertEquals(e.getKey(), e.getValue().getId());
                Assert.assertEquals(e.getValue().getEntityAID(),e.getValue().getEntityA().getId());
                Assert.assertEquals(e.getValue().getEntityBID(),e.getValue().getEntityB().getId());
                Assert.assertEquals(e.getValue().getEntityCID(),e.getValue().getEntityC().getId());
                Assert.assertEquals(e.getValue().getEntityC().getEntityEID(),e.getValue().getEntityC().getEntityE().getId());
                Assert.assertEquals(e.getValue().getEntityDID(),e.getValue().getEntityD().getId());
            }
        }
    }


    @Test
    public void testAssociationWithGroupBy_7() {
        List<TestEntityA> testEntities = queryEntityA().prepare().selectList();

        List<TestEventWithAssociation> testEvents = new ArrayList<TestEventWithAssociation>(testEntities.size());

        for (TestEntityA testEntity : testEntities) {
            testEvents.add(new TestEventWithAssociation(testEntity.getId()));
        }

        em.fetchLazy(TestEventWithAssociation.class, testEvents);

        for (TestEventWithAssociation testEvent : testEvents) {
            final Map<TestEntityA, Map<Long, List<TestEntity>>> entitiesByACEId = testEvent.getEntitiesByACEId();
            for (Map.Entry<TestEntityA, Map<Long, List<TestEntity>>> e : entitiesByACEId.entrySet()) {
                for (Map.Entry<Long, List<TestEntity>> entry : e.getValue().entrySet()) {
                    for (TestEntity testEntity : entry.getValue()) {
                        Assert.assertEquals(e.getKey(), testEntity.getEntityA());                                                
                        Assert.assertEquals(entry.getKey(), testEntity.getEntityC().getEntityE().getId());

                        Assert.assertEquals(testEntity.getEntityAID(),testEntity.getEntityA().getId());
                        Assert.assertEquals(testEntity.getEntityBID(),testEntity.getEntityB().getId());
                        Assert.assertEquals(testEntity.getEntityCID(),testEntity.getEntityC().getId());
                        Assert.assertEquals(testEntity.getEntityC().getEntityEID(),testEntity.getEntityC().getEntityE().getId());
                        Assert.assertEquals(testEntity.getEntityDID(),testEntity.getEntityD().getId());
                        
                    }
                }
            }
        }
    }

}
