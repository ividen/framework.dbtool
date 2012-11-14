package ru.kwanza.dbtool.orm.impl.fetcher;

import junit.framework.TestCase;
import ru.kwanza.dbtool.orm.IEntityManager;

import java.util.List;

/**
 * Test fetching in hierarchy:  TestEntity{TestEntityA,TestEntityB,TestEntityC{TestEntityF{TestEntityG},TestEntityC},TestEntityD}
 *
 * @author Alexander Guzanov
 */
public class TestFetcherIml extends TestCase{
    IEntityManager em;

    public void testFetch1(){
        List<TestEntity> testEntities = em.queryBuilder(TestEntity.class).create().selectList();
        em.getFetcher().fetch(TestEntity.class,testEntities,"testEntityA,testEntityB,testEntityC{testEntityF,testEntityE{testEntityG}},testEntityD");
    }

    public void testFetch2(){
        List<TestEntity> testEntities = em.queryBuilder(TestEntity.class).create().selectList();
        em.getFetcher().fetch(TestEntity.class,testEntities,"testEntityA,testEntityB,testEntityC{testEntityF,testEntityE},testEntityD");
    }

    public void testFetch3(){
        List<TestEntity> testEntities = em.queryBuilder(TestEntity.class).create().selectList();
        em.getFetcher().fetch(TestEntity.class,testEntities,"testEntityA,testEntityB,testEntityC,testEntityD");
    }


    public void testFetch4(){
        List<TestEntity> testEntities = em.queryBuilder(TestEntity.class).create().selectList();
        em.getFetcher().fetch(TestEntity.class,testEntities,"testEntityC{testEntityF,testEntityE}");
    }

    public void testFetch5(){
        List<TestEntityC> testEntities = em.queryBuilder(TestEntityC.class).create().selectList();
        em.getFetcher().fetch(TestEntityC.class,testEntities,"testEntityF,testEntityE{testEntityG}}");
    }

}
