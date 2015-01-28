package ru.kwanza.dbtool.orm.impl.operation;

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

import org.dbunit.Assertion;
import org.junit.Test;
import ru.kwanza.dbtool.core.UpdateException;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * @author Kiryl Karatsetski
 */
public abstract class UpdateOperationTest extends AbstractOperationTest {

    @Test
    public void testSuccessUpdate() throws Throwable {
        final List<TestEntity> testEntities = em.queryBuilder(TestEntity.class).create().prepare().selectList();
        for (TestEntity testEntity : testEntities) {
            testEntity.incrementVersion();
        }
        try {
            em.update(TestEntity.class, testEntities);
        } catch (UpdateException e) {
            fail("Must never throw!");
            e.printStackTrace();
        }

        Assertion.assertEquals(getResourceSet("./data/testSuccessUpdate.xml"), getActualDataSet());
    }

    @Test
    public void testUpdateConstrained_1() throws Throwable {
        final List<TestEntity> testEntities =
                em.queryBuilder(TestEntity.class).orderBy("key").create().prepare().selectList();
        for (TestEntity testEntity : testEntities) {
            testEntity.incrementVersion();
        }

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(0).setName(new String(buff));

        try {
            em.update(TestEntity.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 0, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_1.xml"));
    }

    @Test
    public void testUpdateConstrained_2() throws Throwable {
        final List<TestEntity> testEntities =
                em.queryBuilder(TestEntity.class).orderBy("key").create().prepare().selectList();
        for (TestEntity e : testEntities) {
            e.incrementVersion();
        }

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(testEntities.size() - 1).setName(new String(buff));

        try {
            em.update(TestEntity.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 10, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_2.xml"));
    }

    @Test
    public void testUpdateConstrained_3() throws Throwable {
        final List<TestEntity> testEntities =
                em.queryBuilder(TestEntity.class).orderBy("key").create().prepare().selectList();
        for (TestEntity e : testEntities) {
            e.incrementVersion();
        }

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(testEntities.size() - 1).setName(new String(buff));
        testEntities.get(0).setName(new String(buff));

        try {
            em.update(TestEntity.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 2, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 0, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
            assertEquals("Wrong key ", 10, e.<TestEntity>getConstrainted().get(1).getKey().longValue());
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_3.xml"));
    }

    @Test
    public void testUpdateConstrained_4() throws Throwable {
        final List<TestEntity> testEntities =
                em.queryBuilder(TestEntity.class).orderBy("key").create().prepare().selectList();
        for (TestEntity e : testEntities) {
            e.incrementVersion();
        }

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(5).setName(new String(buff));

        try {
            em.update(TestEntity.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 5, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_4.xml"));
    }

    @Test
    public void testUpdateConstrained_5() throws Throwable {
        final List<TestEntity> testEntities =
                em.queryBuilder(TestEntity.class).orderBy("key").create().prepare().selectList();
        for (TestEntity e : testEntities) {
            e.incrementVersion();
        }

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(testEntities.size() - 1).setName(new String(buff));
        testEntities.get(0).setName(new String(buff));
        testEntities.get(5).setName(new String(buff));

        try {
            em.update(TestEntity.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 3, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 0, e.<TestEntity>getConstrainted().get(0).getKey().longValue());
            assertEquals("Wrong key ", 5, e.<TestEntity>getConstrainted().get(1).getKey().longValue());
            assertEquals("Wrong key ", 10, e.<TestEntity>getConstrainted().get(2).getKey().longValue());
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_5.xml"));
    }

    @Test
    public void testUpdateConstrained_6() throws Throwable {
        final List<TestEntity> testEntities =
                em.queryBuilder(TestEntity.class).orderBy("key").create().prepare().selectList();
        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        for (TestEntity e : testEntities) {
            e.incrementVersion();
            e.setName(new String(buff));
        }

        try {
            em.update(TestEntity.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            List<TestEntity> list = e.<TestEntity>getConstrainted();
            assertEquals("Wrong size ", 11, list.size());
            for (int i = 0; i < list.size(); i++) {
                assertEquals(i, list.get(i).getKey().longValue());
            }
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_6.xml"));
    }
}
