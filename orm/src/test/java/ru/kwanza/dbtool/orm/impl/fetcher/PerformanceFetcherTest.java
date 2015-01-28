package ru.kwanza.dbtool.orm.impl.fetcher;

/*
 * #%L
 * dbtool-orm
 * %%
 * Copyright (C) 2015 Kwanza
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
