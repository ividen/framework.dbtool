package ru.kwanza.dbtool.orm.impl.fetcher;

import junit.framework.Assert;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.api.IQuery;

import java.util.List;

/**
 * @author Alexander Guzanov
 */
public class PerformanceFetcherTest {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("oracle-config.xml", PerformanceFetcherTest.class);

        IEntityManager em = (IEntityManager) context.getBean("dbtool.IEntityManager");
        final IQuery<TestEntity> query = em.queryBuilder(TestEntity.class).orderBy("id ASC").create();
        for (int i = 0; i < 100; i++) {

            List<TestEntity> testEntities = query.prepare().selectList();
            em.fetchLazy(TestEntity.class, testEntities);

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

    }
}
