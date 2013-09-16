package ru.kwanza.dbtool.orm.impl.fetcher;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.orm.api.Condition;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingRegistryImpl;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
    private EntityMappingRegistryImpl registry;
    @Resource(name = "initTests")
    private InitRegistryAndDB initTests;

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
        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).where(Condition.isLess("id"))
                .create();
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
            final TestEvent testEvent = testEvents.get(i+1500);
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
            Assert.assertEquals(testEvent.getEntities().size(),2);
            for (TestEntity testEntity : testEvent.getEntities()) {
                Assert.assertEquals(testEntity.getEntityBID(),testEntity.getEntityB().getId());
                Assert.assertEquals(testEntity.getEntityCID(),testEntity.getEntityC().getId());
                Assert.assertEquals(testEntity.getEntityDID(),testEntity.getEntityD().getId());

                Assert.assertEquals(testEntity.getEntityC().getEntityEID(),testEntity.getEntityC().getEntityE().getId());
                Assert.assertEquals(testEntity.getEntityC().getEntityFID(),testEntity.getEntityC().getEntityF().getId());

                Assert.assertEquals(testEntity.getEntityC().getEntityE().getEntityGID(),testEntity.getEntityC().getEntityE().getEntityG().getId());
            }
        }

    }

}
