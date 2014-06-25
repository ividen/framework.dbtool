package ru.kwanza.dbtool.orm.impl.abstractentity;

import junit.framework.Assert;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.core.ConnectionConfigListener;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.api.If;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.fetcher.proxy.Proxy;
import ru.kwanza.dbtool.orm.impl.querybuilder.AbstractQuery;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author Alexander Guzanov
 */

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractEntityTest extends AbstractJUnit4SpringContextTests {
    @Resource(name = "dbtool.IEntityManager")
    private IEntityManager em;


    @Autowired
    private InitDB initDB;


    @Component
    public static class InitDB {
        @Resource(name = "dbTester")
        private IDatabaseTester dbTester;

        public InitDB(IEntityMappingRegistry registry) {
            registry.registerEntityClass(TestEntityA.class);
            registry.registerEntityClass(TestEntityE.class);
            registry.registerEntityClass(TestEntityF.class);
            registry.registerEntityClass(TestEntityB.class);
            registry.registerEntityClass(TestEntityC.class);
            registry.registerEntityClass(TestEntityD.class);
            registry.registerEntityClass(TestEntity.class);
        }

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
    public void testQuieryBuilder_1() {
        final AbstractQuery<AbstractTestEntity> query =
                (AbstractQuery<AbstractTestEntity>) em.queryBuilder(AbstractTestEntity.class).create();
        Assert.assertEquals(query.getConfig().getSql(),
                "SELECT clazz_,id,version,f_0,f_1,f_2,f_3,f_4,f_5 FROM (SELECT id,version,0 clazz_,title f_0,null f_1,null f_2,null f_3,null f_4,null f_5 FROM test_entity_a UNION ALL SELECT id,version,1 clazz_,null f_0,title f_1,entity_gid f_2,null f_3,null f_4,null f_5 FROM test_entity_e UNION ALL SELECT id,version,2 clazz_,null f_0,null f_1,null f_2,title f_3,null f_4,null f_5 FROM test_entity_f UNION ALL SELECT id,version,3 clazz_,null f_0,null f_1,null f_2,null f_3,title f_4,null f_5 FROM test_entity_b UNION ALL SELECT id,version,4 clazz_,null f_0,null f_1,null f_2,null f_3,null f_4,title f_5 FROM test_entity_c) AbstractTestEntity_");
        query.prepare().selectList();
    }

    @Test
    public void testQuieryBuilder_2() {
        final AbstractQuery<TestEntityD> query = (AbstractQuery<TestEntityD>) em.queryBuilder(TestEntityD.class).create();
        Assert.assertEquals(query.getConfig().getSql(),
                "SELECT clazz_,id,version,title,f_0 FROM (SELECT id,version,title,0 clazz_,entity_gid f_0 FROM test_entity_e UNION ALL SELECT id,version,title,1 clazz_,null f_0 FROM test_entity_f) TestEntityD_");
        query.prepare().selectList();
    }

    @Test
    public void testQuieryBuilder_3() {
        final AbstractQuery<TestEntity> query =
                (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).where(If.isEqual("entity.version")).create();
        Assert.assertEquals(query.getConfig().getSql(),
                "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,t1.clazz_ t1_clazz_,t1.id t1_id,t1.version t1_version,t1.f_0 t1_f_0,t1.f_1 t1_f_1,t1.f_2 t1_f_2,t1.f_3 t1_f_3,t1.f_4 t1_f_4,t1.f_5 t1_f_5 FROM test_entity INNER JOIN (SELECT id,version,0 clazz_,title f_0,null f_1,null f_2,null f_3,null f_4,null f_5 FROM test_entity_a UNION ALL SELECT id,version,1 clazz_,null f_0,title f_1,entity_gid f_2,null f_3,null f_4,null f_5 FROM test_entity_e UNION ALL SELECT id,version,2 clazz_,null f_0,null f_1,null f_2,title f_3,null f_4,null f_5 FROM test_entity_f UNION ALL SELECT id,version,3 clazz_,null f_0,null f_1,null f_2,null f_3,title f_4,null f_5 FROM test_entity_b UNION ALL SELECT id,version,4 clazz_,null f_0,null f_1,null f_2,null f_3,null f_4,title f_5 FROM test_entity_c)  t1 ON test_entity.entity_aid=t1.id  WHERE t1.version = ?");
        query.prepare().setParameter(1,0).selectList();

    }

    @Test
    public void testQuieryBuilder_4() {
        final AbstractQuery<TestEntity> query = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).join("&entity").create();
        Assert.assertEquals(query.getConfig().getSql(),
                "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,t1.clazz_ t1_clazz_,t1.id t1_id,t1.version t1_version,t1.f_0 t1_f_0,t1.f_1 t1_f_1,t1.f_2 t1_f_2,t1.f_3 t1_f_3,t1.f_4 t1_f_4,t1.f_5 t1_f_5 FROM test_entity LEFT JOIN (SELECT id,version,0 clazz_,title f_0,null f_1,null f_2,null f_3,null f_4,null f_5 FROM test_entity_a UNION ALL SELECT id,version,1 clazz_,null f_0,title f_1,entity_gid f_2,null f_3,null f_4,null f_5 FROM test_entity_e UNION ALL SELECT id,version,2 clazz_,null f_0,null f_1,null f_2,title f_3,null f_4,null f_5 FROM test_entity_f UNION ALL SELECT id,version,3 clazz_,null f_0,null f_1,null f_2,null f_3,title f_4,null f_5 FROM test_entity_b UNION ALL SELECT id,version,4 clazz_,null f_0,null f_1,null f_2,null f_3,null f_4,title f_5 FROM test_entity_c)  t1 ON test_entity.entity_aid=t1.id ");
        query.prepare().selectList();
    }

    @Test
    public void testQuieryBuilder_5() {
        final AbstractQuery<TestEntity> query =
                (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).join("&entity,&otherEntity").create();
        Assert.assertEquals(query.getConfig().getSql(),
                "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,t1.clazz_ t1_clazz_,t1.id t1_id,t1.version t1_version,t1.f_0 t1_f_0,t1.f_1 t1_f_1,t1.f_2 t1_f_2,t1.f_3 t1_f_3,t1.f_4 t1_f_4,t1.f_5 t1_f_5,t2.clazz_ t2_clazz_,t2.id t2_id,t2.version t2_version,t2.f_0 t2_f_0,t2.f_1 t2_f_1,t2.f_2 t2_f_2,t2.f_3 t2_f_3,t2.f_4 t2_f_4,t2.f_5 t2_f_5 FROM test_entity LEFT JOIN (SELECT id,version,0 clazz_,title f_0,null f_1,null f_2,null f_3,null f_4,null f_5 FROM test_entity_a UNION ALL SELECT id,version,1 clazz_,null f_0,title f_1,entity_gid f_2,null f_3,null f_4,null f_5 FROM test_entity_e UNION ALL SELECT id,version,2 clazz_,null f_0,null f_1,null f_2,title f_3,null f_4,null f_5 FROM test_entity_f UNION ALL SELECT id,version,3 clazz_,null f_0,null f_1,null f_2,null f_3,title f_4,null f_5 FROM test_entity_b UNION ALL SELECT id,version,4 clazz_,null f_0,null f_1,null f_2,null f_3,null f_4,title f_5 FROM test_entity_c)  t1 ON test_entity.entity_aid=t1.id  LEFT JOIN (SELECT id,version,0 clazz_,title f_0,null f_1,null f_2,null f_3,null f_4,null f_5 FROM test_entity_a UNION ALL SELECT id,version,1 clazz_,null f_0,title f_1,entity_gid f_2,null f_3,null f_4,null f_5 FROM test_entity_e UNION ALL SELECT id,version,2 clazz_,null f_0,null f_1,null f_2,title f_3,null f_4,null f_5 FROM test_entity_f UNION ALL SELECT id,version,3 clazz_,null f_0,null f_1,null f_2,null f_3,title f_4,null f_5 FROM test_entity_b UNION ALL SELECT id,version,4 clazz_,null f_0,null f_1,null f_2,null f_3,null f_4,title f_5 FROM test_entity_c)  t2 ON test_entity.entity_bid=t2.id ");
        query.prepare().selectList();
    }

    @Test
    public void testQuieryBuilder_6() {
        final AbstractQuery<TestEntity> query =
                (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).join("&entityWithCondition").create();
        Assert.assertEquals(query.getConfig().getSql(),
                "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,t1.clazz_ t1_clazz_,t1.id t1_id,t1.version t1_version,t1.f_0 t1_f_0,t1.f_1 t1_f_1,t1.f_2 t1_f_2,t1.f_3 t1_f_3,t1.f_4 t1_f_4,t1.f_5 t1_f_5 FROM test_entity LEFT JOIN (SELECT id,version,0 clazz_,title f_0,null f_1,null f_2,null f_3,null f_4,null f_5 FROM test_entity_a UNION ALL SELECT id,version,1 clazz_,null f_0,title f_1,entity_gid f_2,null f_3,null f_4,null f_5 FROM test_entity_e UNION ALL SELECT id,version,2 clazz_,null f_0,null f_1,null f_2,title f_3,null f_4,null f_5 FROM test_entity_f UNION ALL SELECT id,version,3 clazz_,null f_0,null f_1,null f_2,null f_3,title f_4,null f_5 FROM test_entity_b UNION ALL SELECT id,version,4 clazz_,null f_0,null f_1,null f_2,null f_3,null f_4,title f_5 FROM test_entity_c)  t1 ON test_entity.entity_aid=t1.id  AND (t1.version >= ?)");
        query.prepare().selectList();
    }

    @Test
    public void testSelect_1() {
        final List<AbstractTestEntity> result = em.queryBuilder(AbstractTestEntity.class).create().prepare().selectList();
        Assert.assertEquals(result.size(), 7500);

        int entityA = 0;
        int entityB = 0;
        int entityC = 0;
        int entityF = 0;
        int entityE = 0;

        for (AbstractTestEntity entity : result) {
            if (entity instanceof TestEntityA) {
                entityA++;
                final TestEntityA e = (TestEntityA) entity;
                Assert.assertEquals(e.getTitle(), "test_entity_a" + e.getId());
            } else if (entity instanceof TestEntityB) {
                entityB++;
                final TestEntityB e = (TestEntityB) entity;
                Assert.assertEquals(e.getTitle(), "test_entity_b" + e.getId());
            } else if (entity instanceof TestEntityC) {
                entityC++;
                final TestEntityC e = (TestEntityC) entity;
                Assert.assertEquals(e.getTitle(), "test_entity_c" + e.getId());
            } else if (entity instanceof TestEntityE) {
                entityE++;
                final TestEntityE e = (TestEntityE) entity;
                Assert.assertEquals(e.getTitle(), "test_entity_e" + e.getId());
            } else if (entity instanceof TestEntityF) {
                entityF++;
                final TestEntityF e = (TestEntityF) entity;
                Assert.assertEquals(e.getTitle(), "test_entity_f" + e.getId());
            }
        }

        Assert.assertEquals(entityA, 1500);
        Assert.assertEquals(entityB, 1500);
        Assert.assertEquals(entityC, 1500);
        Assert.assertEquals(entityE, 1500);
        Assert.assertEquals(entityF, 1500);
    }

    @Test
    public void testSelect_2() {
        final List<TestEntityD> result = em.queryBuilder(TestEntityD.class).create().prepare().selectList();
        Assert.assertEquals(result.size(), 3000);

        int entityF = 0;
        int entityE = 0;

        for (AbstractTestEntity entity : result) {
            if (entity instanceof TestEntityE) {
                entityE++;
                final TestEntityE e = (TestEntityE) entity;
                Assert.assertEquals(e.getTitle(), "test_entity_e" + e.getId());
            } else if (entity instanceof TestEntityF) {
                entityF++;
                final TestEntityF e = (TestEntityF) entity;
                Assert.assertEquals(e.getTitle(), "test_entity_f" + e.getId());
            }
        }

        Assert.assertEquals(entityE, 1500);
        Assert.assertEquals(entityF, 1500);
    }

    @Test
    public void testSelect_3() {
        final List<AbstractTestEntity> result =
                em.queryBuilder(AbstractTestEntity.class).where(If.isGreaterOrEqual("version", If.valueOf(0l))).create().prepare()
                        .selectList();
        Assert.assertEquals(result.size(), 7500);

        int entityA = 0;
        int entityB = 0;
        int entityC = 0;
        int entityF = 0;
        int entityE = 0;

        for (AbstractTestEntity entity : result) {
            if (entity instanceof TestEntityA) {
                entityA++;
                final TestEntityA e = (TestEntityA) entity;
                Assert.assertEquals(e.getTitle(), "test_entity_a" + e.getId());
            } else if (entity instanceof TestEntityB) {
                entityB++;
                final TestEntityB e = (TestEntityB) entity;
                Assert.assertEquals(e.getTitle(), "test_entity_b" + e.getId());
            } else if (entity instanceof TestEntityC) {
                entityC++;
                final TestEntityC e = (TestEntityC) entity;
                Assert.assertEquals(e.getTitle(), "test_entity_c" + e.getId());
            } else if (entity instanceof TestEntityE) {
                entityE++;
                final TestEntityE e = (TestEntityE) entity;
                Assert.assertEquals(e.getTitle(), "test_entity_e" + e.getId());
            } else if (entity instanceof TestEntityF) {
                entityF++;
                final TestEntityF e = (TestEntityF) entity;
                Assert.assertEquals(e.getTitle(), "test_entity_f" + e.getId());
            }
        }

        Assert.assertEquals(entityA, 1500);
        Assert.assertEquals(entityB, 1500);
        Assert.assertEquals(entityC, 1500);
        Assert.assertEquals(entityE, 1500);
        Assert.assertEquals(entityF, 1500);
    }

    @Test
    public void tesJoin_1() {
        final IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .join("&entity,&otherEntity").create();

        final List<TestEntity> entities = query.prepare().selectList();

        Assert.assertEquals(entities.size(), 3000);

        for (TestEntity entity : entities) {
            Assert.assertEquals(entity.getEntityAID(), entity.getEntity().getId());
            Assert.assertEquals(entity.getEntityBID(), entity.getOtherEntity().getId());
        }

    }

    @Test
    public void tesJoin_2() {
        final IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).join("&entity").create();

        final List<TestEntity> entities = query.prepare().selectList();

        Assert.assertEquals(entities.size(), 3000);

        for (TestEntity entity : entities) {
            Assert.assertEquals(entity.getEntityAID(), entity.getEntity().getId());
            Assert.assertNull(entity.getOtherEntity());
        }
    }

    @Test
    public void testJoin_3() {
        final IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).where(If.isEqual("entity.version")).create();

        final List<TestEntity> entities = query.prepare().setParameter(1, 0).selectList();
        Assert.assertEquals(entities.size(), 3000);

        for (TestEntity entity : entities) {
            Assert.assertEquals(entity.getEntityAID(), entity.getEntity().getId());
            Assert.assertNull(entity.getOtherEntity());
        }
    }

    @Test
    public void testJoin_4() {
        final IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).join("!entityWithCondition").create();

        final List<TestEntity> entities = query.prepare().selectList();
        Assert.assertEquals(entities.size(), 3000);

        for (TestEntity entity : entities) {
            Assert.assertEquals(entity.getEntityAID(), entity.getEntityWithCondition().getId());
            Assert.assertNull(entity.getOtherEntity());
            Assert.assertNull(entity.getEntity());
        }
    }

    @Test
    public void testFetch_1() {
        final IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).create();

        final List<TestEntity> testEntities = query.prepare().selectList();

        em.fetch(TestEntity.class, testEntities, "entity, otherEntity, entityWithCondition");

        for (TestEntity testEntity : testEntities) {
            Assert.assertEquals(testEntity.getEntityAID(), testEntity.getEntity().getId());
            Assert.assertEquals(testEntity.getEntity().getClass(), TestEntityA.class);
            Assert.assertEquals(testEntity.getEntityAID(), testEntity.getEntityWithCondition().getId());
            Assert.assertEquals(testEntity.getEntityWithCondition().getClass(), TestEntityA.class);
            Assert.assertEquals(testEntity.getEntityBID(), testEntity.getOtherEntity().getId());
            Assert.assertEquals(testEntity.getOtherEntity().getClass(), TestEntityB.class);
        }
    }

    @Test
    public void testLazyFetch() {
        final IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).create();

        final List<TestEntity> testEntities = query.prepare().selectList();

        em.fetchLazy(TestEntity.class, testEntities);

        for (TestEntity testEntity : testEntities) {
            Assert.assertEquals(testEntity.getEntityAID(), testEntity.getEntity().getId());
            Assert.assertTrue(Proxy.isProxy(testEntity.getEntity()));
            Assert.assertEquals(((TestEntityA) Proxy.getDelegate(testEntity.getEntity())).getTitle(),
                    "test_entity_a" + testEntity.getEntityAID());

            Assert.assertEquals(testEntity.getEntityAID(), testEntity.getEntityWithCondition().getId());
            Assert.assertTrue(Proxy.isProxy(testEntity.getEntityWithCondition()));
            Assert.assertEquals(((TestEntityA) Proxy.getDelegate(testEntity.getEntityWithCondition())).getTitle(),
                    "test_entity_a" + testEntity.getEntityAID());

            Assert.assertEquals(testEntity.getEntityBID(), testEntity.getOtherEntity().getId());
            Assert.assertTrue(Proxy.isProxy(testEntity.getOtherEntity()));
            Assert.assertEquals(((TestEntityB) Proxy.getDelegate(testEntity.getOtherEntity())).getTitle(),
                    "test_entity_b" + testEntity.getEntityBID());
        }
    }
}
