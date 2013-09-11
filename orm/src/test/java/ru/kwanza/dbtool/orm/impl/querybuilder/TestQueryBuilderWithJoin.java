package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.impl.fetcher.*;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingRegistryImpl;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Alexander Guzanov
 */

@ContextConfiguration(locations = "oracle-config.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TestQueryBuilderWithJoin extends AbstractJUnit4SpringContextTests {
    @Resource(name = "dbtool.IEntityManager")
    private IEntityManager em;
    @Resource(name = "dbtool.IEntityMappingRegistry")
    private EntityMappingRegistryImpl registry;

    @Before
    public void init() {
        registry.registerEntityClass(TestEntity.class);
        registry.registerEntityClass(TestEntityA.class);
        registry.registerEntityClass(TestEntityB.class);
        registry.registerEntityClass(TestEntityC.class);
        registry.registerEntityClass(TestEntityD.class);
        registry.registerEntityClass(TestEntityE.class);
        registry.registerEntityClass(TestEntityF.class);
        registry.registerEntityClass(TestEntityG.class);
    }

    @Test
    public void test1() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("entityA, entityB, entityC {entityE{entityG},entityF} ,entityD").create();

        System.out.println(query1.getConfig().getSql());
    }

    @Test
    public void test2() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("#entityA, #entityB, #entityC {#entityE{#entityG},#entityF} ,#entityD").create();

        System.out.println(query1.getConfig().getSql());


        AbstractQuery<TestEntity> query2 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .usePaging(true)
                .join("#entityA, #entityB, #entityC {#entityE{#entityG},#entityF} ,#entityD").create();

        final List<TestEntity> result = query2.prepare().paging(0, 10000).selectList();
        System.out.println(result.size());
    }

    @Test
    public void test3() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("entityA, entityB, entityC {#entityE{#entityG},#entityF} ,entityD").create();

        System.out.println(query1.getConfig().getSql());
    }

    @Test
    public void test4() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("#entityA, #entityB, #entityC {entityE{entityG},entityF} ,#entityD").create();

        System.out.println(query1.getConfig().getSql());
    }

    @Test
    public void test5() {
        AbstractQuery<TestEntity> query1 =
                (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).join(Join.left("entityA")).join(Join.left("entityB"))
                        .join(Join.left("entityC", Join.inner("entityE", Join.inner("entityG")), Join.inner("entityF")))
                        .join(Join.left("entityD")).create();

        System.out.println(query1.getConfig().getSql());
    }


    @Test
    public void test6() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("#entityA, #entityB, #entityC {entityE{entityG},entityF} ,#entityD").usePaging(true).create();

        System.out.println(query1.getConfig().getSql());
    }

}
