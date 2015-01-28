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
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import ru.kwanza.dbtool.core.UpdateException;
import ru.kwanza.dbtool.core.VersionGenerator;
import ru.kwanza.dbtool.orm.api.IEntityManager;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * @author Kiryl Karatsetski
 */
public abstract class UpdateOperationWithOptimisticTest extends AbstractOperationTest {
    @Resource(name = "dbtool.VersionGenerator")
    protected VersionGenerator vg;


    //cleanup version for test purpose . It's dirty hack!
    @Before
    public void setUp() throws Exception {

        Field counters = vg.getClass().getDeclaredField("counters");
        counters.setAccessible(true);
        counters.set(vg, new ConcurrentHashMap());
    }

    @Test
    @Rollback
    public void testSuccessUpdate() throws Throwable {
        final List<TestEntityVersion> testEntities = selectTestEntities();
        try {
            em.update(TestEntityVersion.class, testEntities);
        } catch (UpdateException e) {
            e.printStackTrace();
            fail("Must never throw!");
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testSuccessUpdate_version.xml"));
    }

    @Test
    public void testUpdateConstrained_1() throws Throwable {
        final List<TestEntityVersion> testEntities = selectTestEntities();
        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(0).setName(new String(buff));
        try {
            em.update(TestEntityVersion.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 1, e.<TestEntityVersion>getConstrainted().size());
            assertEquals("Wrong key ", 0, e.<TestEntityVersion>getConstrainted().get(0).getKey().longValue());
        }

        int version = 0;
        for (TestEntityVersion TestEntityVersion : testEntities) {
            version += 100;
            if (TestEntityVersion == testEntities.get(0)) {
                assertEquals("Wrong version", 0, TestEntityVersion.getVersion().longValue());
            } else {
                assertEquals("Wrong version", version, TestEntityVersion.getVersion().longValue());
            }
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_1_version.xml"));
    }

    @Test
    public void testUpdateConstrained_2() throws Throwable {
        final List<TestEntityVersion> testEntities = selectTestEntities();
        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(testEntities.size() - 1).setName(new String(buff));
        try {
            em.update(TestEntityVersion.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 1, e.<TestEntityVersion>getConstrainted().size());
            assertEquals("Wrong key ", 10, e.<TestEntityVersion>getConstrainted().get(0).getKey().longValue());
        }

        int version = 0;
        for (TestEntityVersion TestEntityVersion : testEntities) {
            version += 100;
            if (TestEntityVersion == testEntities.get(10)) {
                assertEquals("Wrong version", 0, TestEntityVersion.getVersion().longValue());
            } else {
                assertEquals("Wrong version", version, TestEntityVersion.getVersion().longValue());
            }
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_2_version.xml"));
    }

    @Test
    public void testUpdateConstrained_3() throws Throwable {
        final List<TestEntityVersion> testEntities = selectTestEntities();
        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(testEntities.size() - 1).setName(new String(buff));
        testEntities.get(0).setName(new String(buff));
        try {
            em.update(TestEntityVersion.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 2, e.<TestEntityVersion>getConstrainted().size());
            assertEquals("Wrong key ", 0, e.<TestEntityVersion>getConstrainted().get(0).getKey().longValue());
            assertEquals("Wrong key ", 10, e.<TestEntityVersion>getConstrainted().get(1).getKey().longValue());
        }

        int version = 0;
        for (TestEntityVersion TestEntityVersion : testEntities) {
            version += 100;
            if (TestEntityVersion == testEntities.get(0) || TestEntityVersion == testEntities.get(10)) {
                assertEquals("Wrong version", 0, TestEntityVersion.getVersion().longValue());
            } else {
                assertEquals("Wrong version", version, TestEntityVersion.getVersion().longValue());
            }
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_3_version.xml"));
    }

    @Test
    public void testUpdateConstrained_4() throws Throwable {
        final List<TestEntityVersion> testEntities = selectTestEntities();
        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(5).setName(new String(buff));
        try {
            em.update(TestEntityVersion.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 1, e.<TestEntityVersion>getConstrainted().size());
            assertEquals("Wrong key ", 5, e.<TestEntityVersion>getConstrainted().get(0).getKey().longValue());
        }

        int version = 0;
        for (TestEntityVersion TestEntityVersion : testEntities) {
            version += 100;
            if (TestEntityVersion == testEntities.get(5)) {
                assertEquals("Wrong version", 0, TestEntityVersion.getVersion().longValue());
            } else {
                assertEquals("Wrong version", version, TestEntityVersion.getVersion().longValue());
            }
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_4_version.xml"));
    }

    @Test
    public void testUpdateConstrained_5() throws Throwable {
        final List<TestEntityVersion> testEntities =
                em.queryBuilder(TestEntityVersion.class).orderBy("key").create().prepare().selectList();
        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(testEntities.size() - 1).setName(new String(buff));
        testEntities.get(0).setName(new String(buff));
        testEntities.get(5).setName(new String(buff));

        try {
            em.update(TestEntityVersion.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 3, e.<TestEntityVersion>getConstrainted().size());
            assertEquals("Wrong key ", 0, e.<TestEntityVersion>getConstrainted().get(0).getKey().longValue());
            assertEquals("Wrong key ", 5, e.<TestEntityVersion>getConstrainted().get(1).getKey().longValue());
            assertEquals("Wrong key ", 10, e.<TestEntityVersion>getConstrainted().get(2).getKey().longValue());
        }

        int version = 0;
        for (TestEntityVersion TestEntityVersion : testEntities) {
            version += 100;
            if (TestEntityVersion == testEntities.get(0) || TestEntityVersion == testEntities.get(10) || TestEntityVersion == testEntities.get(5)) {
                assertEquals("Wrong version", 0, TestEntityVersion.getVersion().longValue());
            } else {
                assertEquals("Wrong version", version, TestEntityVersion.getVersion().longValue());
            }
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_5_version.xml"));
    }

    @Test
    public void testUpdateConstrained_6() throws Throwable {
        final List<TestEntityVersion> testEntities = selectTestEntities();
        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        for (TestEntityVersion e : testEntities) {
            e.setName(new String(buff));
        }

        try {
            em.update(TestEntityVersion.class, testEntities);
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            List<TestEntityVersion> list = e.getConstrainted();
            assertEquals("Wrong size ", 11, list.size());
            for (int i = 0; i < list.size(); i++) {
                assertEquals(i, list.get(i).getKey().longValue());
            }
        }

        for (TestEntityVersion TestEntityVersion : testEntities) {
            assertEquals("Wrong version", 0, TestEntityVersion.getVersion().longValue());
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_6_version.xml"));
    }

    @Test
    public void testUpdateOptimistic_1() throws Throwable {
        final List<TestEntityVersion> testEntities;
        testEntities = selectTestEntities();


        final TestEntityVersion optimistic = testEntities.get(0);
        dbTool.getJdbcTemplate()
                .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic.getName(),
                        optimistic.getKey(), optimistic.getVersion());

        try {
            em.update(TestEntityVersion.class, testEntities);
            fail("Must be update Exception");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 0, e.<TestEntityVersion>getConstrainted().size());
            assertEquals("Wrong size ", 1, e.<TestEntityVersion>getOptimistic().size());
            assertEquals("Wrong key ", 0, e.<TestEntityVersion>getOptimistic().get(0).getKey().longValue());
        }

        int version = 0;
        for (TestEntityVersion TestEntityVersion : testEntities) {
            version += 100;
            if (TestEntityVersion == testEntities.get(0)) {
                assertEquals("Wrong version", 0, TestEntityVersion.getVersion().longValue());
            } else {
                assertEquals("Wrong version", version, TestEntityVersion.getVersion().longValue());
            }
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimistic_1_version.xml"));
    }

    @Test
    public void testUpdateOptimistic_2() throws Throwable {
        List<TestEntityVersion> testEntities = null;
        testEntities = selectTestEntities();

        final TestEntityVersion optimistic = testEntities.get(testEntities.size() - 1);
        dbTool.getJdbcTemplate()
                .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic.getName(),
                        optimistic.getKey(), optimistic.getVersion());

        try {
            em.update(TestEntityVersion.class, testEntities);
            fail("Must be update Exception");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 0, e.<TestEntityVersion>getConstrainted().size());
            assertEquals("Wrong size ", 1, e.<TestEntityVersion>getOptimistic().size());
            assertEquals("Wrong key ", 10, e.<TestEntityVersion>getOptimistic().get(0).getKey().longValue());
        }

        int version = 0;
        for (TestEntityVersion TestEntityVersion : testEntities) {
            version += 100;
            if (TestEntityVersion == testEntities.get(10)) {
                assertEquals("Wrong version", 0, TestEntityVersion.getVersion().longValue());
            } else {
                assertEquals("Wrong version", version, TestEntityVersion.getVersion().longValue());
            }
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimistic_2_version.xml"));
    }

    @Test
    public void testUpdateOptimistic_3() throws Throwable {
        List<TestEntityVersion> testEntities = null;
        testEntities = selectTestEntities();

        final TestEntityVersion optimistic = testEntities.get(4);
        dbTool.getJdbcTemplate()
                .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic.getName(),
                        optimistic.getKey(), optimistic.getVersion());

        try {
            em.update(TestEntityVersion.class, testEntities);
            fail("Must be update Exception");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 0, e.<TestEntityVersion>getConstrainted().size());
            assertEquals("Wrong size ", 1, e.<TestEntityVersion>getOptimistic().size());
            assertEquals("Wrong key ", 4, e.<TestEntityVersion>getOptimistic().get(0).getKey().longValue());
        }
        int version = 0;
        for (TestEntityVersion TestEntityVersion : testEntities) {
            version += 100;
            if (TestEntityVersion == testEntities.get(4)) {
                assertEquals("Wrong version", 0, TestEntityVersion.getVersion().longValue());
            } else {
                assertEquals("Wrong version", version, TestEntityVersion.getVersion().longValue());
            }
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimistic_3_version.xml"));
    }

    @Test
    public void testUpdateOptimistic_4() throws Throwable {
        final List<TestEntityVersion> testEntities;
        testEntities = selectTestEntities();

        TestEntityVersion optimistic1 = testEntities.get(0);
        TestEntityVersion optimistic2 = testEntities.get(4);
        TestEntityVersion optimistic3 = testEntities.get(10);
        dbTool.getJdbcTemplate()
                .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic1.getName(),
                        optimistic1.getKey(), optimistic1.getVersion());
        dbTool.getJdbcTemplate()
                .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic2.getName(),
                        optimistic2.getKey(), optimistic2.getVersion());
        dbTool.getJdbcTemplate()
                .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic3.getName(),
                        optimistic3.getKey(), optimistic3.getVersion());


        try {
            em.update(TestEntityVersion.class, testEntities);
            fail("Must be update Exception");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 0, e.<TestEntityVersion>getConstrainted().size());
            assertEquals("Wrong size ", 3, e.<TestEntityVersion>getOptimistic().size());
            assertEquals("Wrong key ", 0, e.<TestEntityVersion>getOptimistic().get(0).getKey().longValue());
            assertEquals("Wrong key ", 4, e.<TestEntityVersion>getOptimistic().get(1).getKey().longValue());
            assertEquals("Wrong key ", 10, e.<TestEntityVersion>getOptimistic().get(2).getKey().longValue());
        }

        int version = 0;
        for (TestEntityVersion TestEntityVersion : testEntities) {
            version += 100;
            if (TestEntityVersion == testEntities.get(0) || TestEntityVersion == testEntities.get(10) || TestEntityVersion == testEntities.get(4)) {
                assertEquals("Wrong version", 0, TestEntityVersion.getVersion().longValue());
            } else {
                assertEquals("Wrong version", version, TestEntityVersion.getVersion().longValue());
            }
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimistic_4_version.xml"));
    }

    @Test
    public void testUpdateOptimisticAndContrainted_1() throws Throwable {
        final List<TestEntityVersion> testEntities;
        testEntities = selectTestEntities();

        TestEntityVersion optimistic1 = testEntities.get(0);
        TestEntityVersion optimistic2 = testEntities.get(4);
        TestEntityVersion optimistic3 = testEntities.get(10);

        assertEquals(1, dbTool.getJdbcTemplate()
                .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic1.getName(),
                        optimistic1.getKey(), optimistic1.getVersion()));
        assertEquals(1, dbTool.getJdbcTemplate()
                .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic2.getName(),
                        optimistic2.getKey(), optimistic2.getVersion()));
        assertEquals(1, dbTool.getJdbcTemplate()
                .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic3.getName(),
                        optimistic3.getKey(), optimistic3.getVersion()));

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(5).setName(new String(buff));

        try {
            em.update(TestEntityVersion.class, testEntities);
            fail("Must be update Exception");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 1, e.<TestEntityVersion>getConstrainted().size());
            assertEquals("Wrong key ", 5, e.<TestEntityVersion>getConstrainted().get(0).getKey().longValue());
            assertEquals("Wrong size ", 3, e.<TestEntityVersion>getOptimistic().size());
            assertEquals("Wrong key ", 0, e.<TestEntityVersion>getOptimistic().get(0).getKey().longValue());
            assertEquals("Wrong key ", 4, e.<TestEntityVersion>getOptimistic().get(1).getKey().longValue());
            assertEquals("Wrong key ", 10, e.<TestEntityVersion>getOptimistic().get(2).getKey().longValue());
        }

        int version = 0;
        for (TestEntityVersion testEntity : testEntities) {
            version += 100;
            if (testEntity == testEntities.get(0) || testEntity == testEntities.get(10) || testEntity == testEntities.get(5)
                    || testEntity == testEntities.get(4)) {
                assertEquals("Wrong version", 0, testEntity.getVersion().longValue());
            } else {
                assertEquals("Wrong version", version, testEntity.getVersion().longValue());
            }
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimisticAndContrainted_1_version.xml"));
    }

    @Test
    public void testUpdateOptimisticAndContrainted_2() throws Throwable {
        final List<TestEntityVersion> testEntities;
        testEntities = selectTestEntities();
        TestEntityVersion optimistic1 = testEntities.get(0);
        TestEntityVersion optimistic2 = testEntities.get(4);
        TestEntityVersion optimistic3 = testEntities.get(10);

        assertEquals(1, dbTool.getJdbcTemplate()
                .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic1.getName(),
                        optimistic1.getKey(), optimistic1.getVersion()));
        assertEquals(1, dbTool.getJdbcTemplate()
                .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic2.getName(),
                        optimistic2.getKey(), optimistic2.getVersion()));
        assertEquals(1, dbTool.getJdbcTemplate()
                .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic3.getName(),
                        optimistic3.getKey(), optimistic3.getVersion()));
        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(3).setName(new String(buff));

        try {
            em.update(TestEntityVersion.class, testEntities);
            fail("Must be update Exception");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 1, e.<TestEntityVersion>getConstrainted().size());
            assertEquals("Wrong key ", 3, e.<TestEntityVersion>getConstrainted().get(0).getKey().longValue());
            assertEquals("Wrong size ", 3, e.<TestEntityVersion>getOptimistic().size());
            assertEquals("Wrong key ", 0, e.<TestEntityVersion>getOptimistic().get(0).getKey().longValue());
            assertEquals("Wrong key ", 4, e.<TestEntityVersion>getOptimistic().get(1).getKey().longValue());
            assertEquals("Wrong key ", 10, e.<TestEntityVersion>getOptimistic().get(2).getKey().longValue());
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimisticAndContrainted_2_version.xml"));
    }

    @Test
    public void testUpdateOptimisticAndContrainted_3() throws Throwable {
        final List<TestEntityVersion> testEntities;
        testEntities = selectTestEntities();
        TestEntityVersion optimistic1 = testEntities.get(4);


        assertEquals(1, dbTool.getJdbcTemplate()
                .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic1.getName(),
                        optimistic1.getKey(), optimistic1.getVersion()));

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(0).setName(new String(buff));
        testEntities.get(5).setName(new String(buff));
        testEntities.get(10).setName(new String(buff));

        try {
            em.update(TestEntityVersion.class, testEntities);
            fail("Must be update Exception");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 3, e.<TestEntityVersion>getConstrainted().size());
            assertEquals("Wrong size ", 1, e.<TestEntityVersion>getOptimistic().size());
            assertEquals("Wrong key ", 4, e.<TestEntityVersion>getOptimistic().get(0).getKey().longValue());
            assertEquals("Wrong key ", 0, e.<TestEntityVersion>getConstrainted().get(0).getKey().longValue());
            assertEquals("Wrong key ", 5, e.<TestEntityVersion>getConstrainted().get(1).getKey().longValue());
            assertEquals("Wrong key ", 10, e.<TestEntityVersion>getConstrainted().get(2).getKey().longValue());
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimisticAndContrainted_3_version.xml"));
    }

    private List<TestEntityVersion> selectTestEntities() {
        return em.queryBuilder(TestEntityVersion.class).orderBy("key").create().prepare().selectList();
    }
}
