package ru.kwanza.dbtool.orm.impl.fetcher;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit38.AbstractJUnit38SpringContextTests;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.entity.TestEntity1;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingRegistryImpl;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import javax.annotation.Resource;
import java.util.List;

/**
 * Test fetching in hierarchy:  TestEntity1{TestEntityA,TestEntityB,TestEntityC{TestEntityF{TestEntityG},TestEntityC},TestEntityD}
 *
 * @author Alexander Guzanov
 */
@ContextConfiguration(locations = "mssql-config.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TestFetcherIml extends AbstractJUnit4SpringContextTests {

    @Resource(name = "dbtool.IEntityManager")
    private IEntityManager em;
    @Resource(name = "dbtool.IEntityMappingRegistry")
    private EntityMappingRegistryImpl registry;

    @Before
    public void init() {
        registry.registerEntityClass(TestEntity1.class);
        registry.registerEntityClass(TestEntity.class);
        registry.registerEntityClass(TestEntityA.class);
        registry.registerEntityClass(TestEntityB.class);
        registry.registerEntityClass(TestEntityC.class);
        registry.registerEntityClass(TestEntityD.class);
        registry.registerEntityClass(TestEntityF.class);
        registry.registerEntityClass(TestEntityG.class);
    }

    @Test
    public void testFetch1() {
        List<TestEntity> testEntities = em.queryBuilder(TestEntity.class).create().selectList();
        em.getFetcher().fetch(TestEntity.class, testEntities, "testEntityA,testEntityB,testEntityC{testEntityF,testEntityE{testEntityG}},testEntityD");
    }

    @Test
    public void testFetch2() {
        List<TestEntity> testEntities = em.queryBuilder(TestEntity.class).create().selectList();
        em.getFetcher().fetch(TestEntity.class, testEntities, "testEntityA,testEntityB,testEntityC{testEntityF,testEntityE},testEntityD");
    }

    @Test
    public void testFetch3() {
        List<TestEntity> testEntities = em.queryBuilder(TestEntity.class).create().selectList();
        em.getFetcher().fetch(TestEntity.class, testEntities, "testEntityA,testEntityB,testEntityC,testEntityD");
    }

    @Test
    public void testFetch4() {
        List<TestEntity> testEntities = em.queryBuilder(TestEntity.class).create().selectList();
        em.getFetcher().fetch(TestEntity.class, testEntities, "testEntityC{testEntityF,testEntityE}");
    }

    @Test
    public void testFetch5() {
        List<TestEntityC> testEntities = em.queryBuilder(TestEntityC.class).create().selectList();
        em.getFetcher().fetch(TestEntityC.class, testEntities, "testEntityF,testEntityE{testEntityG}}");
    }

}
