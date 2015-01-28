package ru.kwanza.dbtool.core.updateutil;

/*
 * #%L
 * dbtool-core
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
import ru.kwanza.dbtool.core.TestEntity;
import ru.kwanza.dbtool.core.UpdateException;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * @author Guzanov Alexander
 */
public abstract class TestUpdateUtilWithOptimistic extends AbstractTestUpdateUtil {

    @Test
    public void testSuccessUpdate() throws Throwable {
        List<TestEntity> testEntities = dbTool.selectList("select xkey,name,version from test_table", LIST_ROW_MAPPER);

        try {
            assertEquals(11, dbTool
                    .update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                            "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                            TestEntity.VERSION));
        } catch (UpdateException e) {
            e.printStackTrace();
            fail("Must never throw!");
        }
        for (TestEntity t : testEntities) {
            assertEquals("Wrong version", 1, t.getVersion());
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testSuccessUpdate.xml"));

    }

    @Test
    public void testUpdateConstrained_1() throws Throwable {
        List<TestEntity> testEntities =
                dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(0).setName(new String(buff));

        try {
            assertEquals(10, dbTool
                    .update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                            "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                            TestEntity.VERSION));
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 0, e.<TestEntity>getConstrainted().get(0).getKey());

            assertEquals("Wrong update size ", 10, e.<TestEntity>getUpdated().size());
        }

        for (TestEntity t : testEntities) {
            if (t == testEntities.get(0)) {
                assertEquals("Wrong version", 0, t.getVersion());
            } else {
                assertEquals("Wrong version", 1, t.getVersion());
            }
        }


        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdateConstrained_1.xml"));
    }

    @Test
    public void testUpdateConstrained_2() throws Throwable {
        List<TestEntity> testEntities =
                dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(testEntities.size() - 1).setName(new String(buff));

        try {
            assertEquals(10, dbTool
                    .update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                            "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                            TestEntity.VERSION));
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 10, e.<TestEntity>getConstrainted().get(0).getKey());

            assertEquals("Wrong update size ", 10, e.<TestEntity>getUpdated().size());
        }

        for (TestEntity t : testEntities) {
            if (t == testEntities.get(10)) {
                assertEquals("Wrong version", 0, t.getVersion());
            } else {
                assertEquals("Wrong version", 1, t.getVersion());
            }
        }


        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdateConstrained_2.xml"));
    }

    @Test
    public void testUpdateConstrained_3() throws Throwable {
        List<TestEntity> testEntities =
                dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(testEntities.size() - 1).setName(new String(buff));
        testEntities.get(0).setName(new String(buff));

        try {
            assertEquals(9, dbTool
                    .update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                            "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                            TestEntity.VERSION));
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 2, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 0, e.<TestEntity>getConstrainted().get(0).getKey());
            assertEquals("Wrong key ", 10, e.<TestEntity>getConstrainted().get(1).getKey());

            assertEquals("Wrong update size ", 9, e.<TestEntity>getUpdated().size());
        }

        for (TestEntity t : testEntities) {
            if (t == testEntities.get(0) || t == testEntities.get(10)) {
                assertEquals("Wrong version", 0, t.getVersion());
            } else {
                assertEquals("Wrong version", 1, t.getVersion());
            }
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdateConstrained_3.xml"));
    }

    @Test
    public void testUpdateConstrained_4() throws Throwable {
        List<TestEntity> testEntities =
                dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(5).setName(new String(buff));

        try {
            assertEquals(10, dbTool
                    .update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                            "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                            TestEntity.VERSION));

            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 5, e.<TestEntity>getConstrainted().get(0).getKey());

            assertEquals("Wrong update size ", 10, e.<TestEntity>getUpdated().size());
        }
        for (TestEntity t : testEntities) {
            if (t == testEntities.get(5)) {
                assertEquals("Wrong version", 0, t.getVersion());
            } else {
                assertEquals("Wrong version", 1, t.getVersion());
            }
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdateConstrained_4.xml"));
    }

    @Test
    public void testUpdateConstrained_5() throws Throwable {
        List<TestEntity> testEntities =
                dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(testEntities.size() - 1).setName(new String(buff));
        testEntities.get(0).setName(new String(buff));
        testEntities.get(5).setName(new String(buff));

        try {
            assertEquals(9, dbTool
                    .update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                            "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                            TestEntity.VERSION));
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 3, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 0, e.<TestEntity>getConstrainted().get(0).getKey());
            assertEquals("Wrong key ", 5, e.<TestEntity>getConstrainted().get(1).getKey());
            assertEquals("Wrong key ", 10, e.<TestEntity>getConstrainted().get(2).getKey());

            assertEquals("Wrong update size ", 8, e.<TestEntity>getUpdated().size());
        }
        for (TestEntity t : testEntities) {
            if (t == testEntities.get(0) || t == testEntities.get(10) || t == testEntities.get(5)) {
                assertEquals("Wrong version", 0, t.getVersion());
            } else {
                assertEquals("Wrong version", 1, t.getVersion());
            }
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdateConstrained_5.xml"));
    }

    @Test
    public void testUpdateConstrained_6() throws Throwable {
        List<TestEntity> testEntities =
                dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        for (TestEntity e : testEntities) {
            e.setName(new String(buff));
        }

        try {
            assertEquals(0, dbTool
                    .update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                            "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                            TestEntity.VERSION));
            fail("Must throw Exception!");
        } catch (UpdateException e) {
            List<TestEntity> list = e.<TestEntity>getConstrainted();
            assertEquals("Wrong size ", 11, list.size());
            for (int i = 0; i < list.size(); i++) {
                assertEquals(i, list.get(i).getKey());
            }

            assertEquals("Wrong update size ", 0, e.<TestEntity>getUpdated().size());
        }

        for (TestEntity t : testEntities) {
            assertEquals("Wrong version", 0, t.getVersion());
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdateConstrained_6.xml"));
    }

    @Test
    public void testUpdate_Skip_1() throws Throwable {
        List<TestEntity> testEntities =
                dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

        try {
            assertEquals(10, dbTool.update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
                    new TestEntityUpdateSetter5(testEntities, 0), "select xkey,version from test_table where xkey in(?)",
                    KEY_VERSION_MAPPER, TestEntity.KEY, TestEntity.VERSION));

        } catch (UpdateException e) {
            fail("Must never throw Exception!");
        }
        for (TestEntity t : testEntities) {
            if (t == testEntities.get(0)) {
                assertEquals("Wrong version", 0, t.getVersion());
            } else {
                assertEquals("Wrong version", 1, t.getVersion());
            }
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdate_Skip_1.xml"));
    }

    @Test
    public void testUpdate_Skip_2() throws Throwable {
        List<TestEntity> testEntities =
                dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
        try {
            assertEquals(10, dbTool.update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
                    new TestEntityUpdateSetter5(testEntities, 10), "select xkey,version from test_table where xkey in(?)",
                    KEY_VERSION_MAPPER, TestEntity.KEY, TestEntity.VERSION));

        } catch (UpdateException e) {
            fail("Must never throw Exception!");
        }
        for (TestEntity t : testEntities) {
            if (t == testEntities.get(10)) {
                assertEquals("Wrong version", 0, t.getVersion());
            } else {
                assertEquals("Wrong version", 1, t.getVersion());
            }
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdate_Skip_2.xml"));
    }

    @Test
    public void testUpdate_Skip_3() throws Throwable {
        List<TestEntity> testEntities =
                dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
        try {
            assertEquals(10, dbTool.update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
                    new TestEntityUpdateSetter5(testEntities, 5), "select xkey,version from test_table where xkey in(?)",
                    KEY_VERSION_MAPPER, TestEntity.KEY, TestEntity.VERSION));

        } catch (UpdateException e) {
            fail("Must never throw Exception!");
        }
        for (TestEntity t : testEntities) {
            if (t == testEntities.get(5)) {
                assertEquals("Wrong version", 0, t.getVersion());
            } else {
                assertEquals("Wrong version", 1, t.getVersion());
            }
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdate_Skip_3.xml"));
    }

    @Test
    public void testUpdate_Skip_4() throws Throwable {
        List<TestEntity> testEntities =
                dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
        try {
            assertEquals(9, dbTool.update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
                    new TestEntityUpdateSetter5(testEntities, 0, 10), "select xkey,version from test_table where xkey in(?)",
                    KEY_VERSION_MAPPER, TestEntity.KEY, TestEntity.VERSION));

        } catch (UpdateException e) {
            fail("Must never throw Exception!");
        }
        for (TestEntity t : testEntities) {
            if (t == testEntities.get(0) || t == testEntities.get(10)) {
                assertEquals("Wrong version", 0, t.getVersion());
            } else {
                assertEquals("Wrong version", 1, t.getVersion());
            }
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdate_Skip_4.xml"));
    }

    @Test
    public void testUpdate_Skip_5() throws Throwable {
        List<TestEntity> testEntities =
                dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
        try {
            assertEquals(8, dbTool.update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
                    new TestEntityUpdateSetter5(testEntities, 0, 5, 10), "select xkey,version from test_table where xkey in(?)",
                    KEY_VERSION_MAPPER, TestEntity.KEY, TestEntity.VERSION));

        } catch (UpdateException e) {
            fail("Must never throw Exception!");
        }
        for (TestEntity t : testEntities) {
            if (t == testEntities.get(0) || t == testEntities.get(10) || t == testEntities.get(5)) {
                assertEquals("Wrong version", 0, t.getVersion());
            } else {
                assertEquals("Wrong version", 1, t.getVersion());
            }
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdate_Skip_5.xml"));
    }

    @Test
    public void testUpdate_Skip_6() throws Throwable {
        List<TestEntity> testEntities =
                dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
        try {
            assertEquals(0, dbTool.update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
                    new TestEntityUpdateSetter5(testEntities, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                    "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY, TestEntity.VERSION));

        } catch (UpdateException e) {
            fail("Must never throw Exception!");
        }
        for (TestEntity t : testEntities) {
            assertEquals("Wrong version", 0, t.getVersion());
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdate_Skip_6.xml"));
    }

    @Test
    public void testUpdate_Skip_7() throws Throwable {
        List<TestEntity> testEntities =
                dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

        try {
            assertEquals(1, dbTool.update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
                    new TestEntityUpdateSetter5(testEntities, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                    "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY, TestEntity.VERSION));

        } catch (UpdateException e) {
            fail("Must never throw Exception!");
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdate_Skip_7.xml"));
    }

    @Test
    public void testUpdate_Skip_8() throws Throwable {
        List<TestEntity> testEntities =
                dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

        try {
            assertEquals(1, dbTool.update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
                    new TestEntityUpdateSetter5(testEntities, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                    "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY, TestEntity.VERSION));

        } catch (UpdateException e) {
            fail("Must never throw Exception!");
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdate_Skip_8.xml"));
    }

    @Test
    public void testUpdate_Skip_9() throws Throwable {
        List<TestEntity> testEntities =
                dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

        try {
            assertEquals(2, dbTool.update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
                    new TestEntityUpdateSetter5(testEntities, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                    "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY, TestEntity.VERSION));

        } catch (UpdateException e) {
            fail("Must never throw Exception!");
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdate_Skip_9.xml"));
    }

    @Test
    public void testUpdate_Skip_10() throws Throwable {
        List<TestEntity> testEntities =
                dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
        try {
            assertEquals(5, dbTool.update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
                    new TestEntityUpdateSetter5(testEntities, 0, 1, 5, 6, 9, 10),
                    "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY, TestEntity.VERSION));

        } catch (UpdateException e) {
            fail("Must never throw Exception!");
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdate_Skip_10.xml"));
    }

    @Test
    public void testUpdate_Skip_11() throws Throwable {
        List<TestEntity> testEntities =
                dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

        try {
            assertEquals(7, dbTool.update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
                    new TestEntityUpdateSetter5(testEntities, 1, 5, 6, 9), "select xkey,version from test_table where xkey in(?)",
                    KEY_VERSION_MAPPER, TestEntity.KEY, TestEntity.VERSION));

        } catch (UpdateException e) {
            fail("Must never throw Exception!");
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdate_Skip_11.xml"));
    }


    @Test
    public void testUpdateOptimistic_1() throws Throwable {

        List<TestEntity> testEntities = null;
        testEntities = dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

        TestEntity optimistic = testEntities.get(0);

        assertEquals(1, dbTool.getJdbcTemplate()
                .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic.getName(),
                        optimistic.getKey(), optimistic.getVersion()));

        try {
            assertEquals(10, dbTool
                    .update("update test_table set version=?, name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                            "select xkey, version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                            TestEntity.VERSION));
            fail("Must be update Exception");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 0, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong size ", 1, e.<TestEntity>getOptimistic().size());
            assertEquals("Wrong key ", 0, e.<TestEntity>getOptimistic().get(0).getKey());

            assertEquals("Wrong update size ", 10, e.<TestEntity>getUpdated().size());
        }

        for (TestEntity t : testEntities) {
            if (t == testEntities.get(0)) {
                assertEquals("Wrong version", 0, t.getVersion());
            } else {
                assertEquals("Wrong version", 1, t.getVersion());
            }
        }


        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdateOptimistic_1.xml"));
    }

    @Test
    public void testUpdateOptimistic_2() throws Throwable {
        List<TestEntity> testEntities = null;
        testEntities = dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

        TestEntity optimistic = testEntities.get(testEntities.size() - 1);

        assertEquals(1, dbTool.getJdbcTemplate()
                .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic.getName(),
                        optimistic.getKey(), optimistic.getVersion()));

        try {
            assertEquals(10, dbTool
                    .update("update test_table set version=?, name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                            "select xkey, version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                            TestEntity.VERSION));
            fail("Must be update Exception");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 0, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong size ", 1, e.<TestEntity>getOptimistic().size());
            assertEquals("Wrong key ", 10, e.<TestEntity>getOptimistic().get(0).getKey());

            assertEquals("Wrong update size ", 10, e.<TestEntity>getUpdated().size());
        }
        for (TestEntity t : testEntities) {
            if (t == testEntities.get(10)) {
                assertEquals("Wrong version", 0, t.getVersion());
            } else {
                assertEquals("Wrong version", 1, t.getVersion());
            }
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdateOptimistic_2.xml"));
    }

    @Test
    public void testUpdateOptimistic_3() throws Throwable {

        List<TestEntity> testEntities = null;
        testEntities = dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

        TestEntity optimistic = testEntities.get(4);

        assertEquals(1, dbTool.getJdbcTemplate()
                .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic.getName(),
                        optimistic.getKey(), optimistic.getVersion()));

        try {
            assertEquals(10, dbTool
                    .update("update test_table set version=?, name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                            "select xkey, version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                            TestEntity.VERSION));
            fail("Must be update Exception");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 0, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong size ", 1, e.<TestEntity>getOptimistic().size());
            assertEquals("Wrong key ", 4, e.<TestEntity>getOptimistic().get(0).getKey());

            assertEquals("Wrong update size ", 10, e.<TestEntity>getUpdated().size());
        }
        for (TestEntity t : testEntities) {
            if (t == testEntities.get(4)) {
                assertEquals("Wrong version", 0, t.getVersion());
            } else {
                assertEquals("Wrong version", 1, t.getVersion());
            }
        }


        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdateOptimistic_3.xml"));
    }

    @Test
    public void testUpdateOptimistic_4() throws Throwable {
        List<TestEntity> testEntities = null;

        testEntities = dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

        TestEntity optimistic1 = testEntities.get(0);
        TestEntity optimistic2 = testEntities.get(4);
        TestEntity optimistic3 = testEntities.get(10);
        assertEquals(1, dbTool.getJdbcTemplate()
                .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic1.getName(),
                        optimistic1.getKey(), optimistic1.getVersion()));
        assertEquals(1, dbTool.getJdbcTemplate()
                .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic2.getName(),
                        optimistic2.getKey(), optimistic2.getVersion()));
        assertEquals(1, dbTool.getJdbcTemplate()
                .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic3.getName(),
                        optimistic3.getKey(), optimistic3.getVersion()));

        try {
            assertEquals(10, dbTool
                    .update("update test_table set version=?, name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                            "select xkey, version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                            TestEntity.VERSION));
            fail("Must be update Exception");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 0, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong size ", 3, e.<TestEntity>getOptimistic().size());
            assertEquals("Wrong key ", 0, e.<TestEntity>getOptimistic().get(0).getKey());
            assertEquals("Wrong key ", 4, e.<TestEntity>getOptimistic().get(1).getKey());
            assertEquals("Wrong key ", 10, e.<TestEntity>getOptimistic().get(2).getKey());

            assertEquals("Wrong update size ", 8, e.<TestEntity>getUpdated().size());
        }
        for (TestEntity t : testEntities) {
            if (t == testEntities.get(0) || t == testEntities.get(10) || t == testEntities.get(4)) {
                assertEquals("Wrong version", 0, t.getVersion());
            } else {
                assertEquals("Wrong version", 1, t.getVersion());
            }
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdateOptimistic_4.xml"));
    }

    @Test
    public void testUpdateOptimisticAndContrainted_1() throws Throwable {
        List<TestEntity> testEntities = null;


        testEntities = dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

        TestEntity optimistic1 = testEntities.get(0);
        TestEntity optimistic2 = testEntities.get(4);
        TestEntity optimistic3 = testEntities.get(10);

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
            assertEquals(10, dbTool
                    .update("update test_table set version=?, name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                            "select xkey, version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                            TestEntity.VERSION));
            fail("Must be update Exception");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 5, e.<TestEntity>getConstrainted().get(0).getKey());
            assertEquals("Wrong size ", 3, e.<TestEntity>getOptimistic().size());
            assertEquals("Wrong key ", 0, e.<TestEntity>getOptimistic().get(0).getKey());
            assertEquals("Wrong key ", 4, e.<TestEntity>getOptimistic().get(1).getKey());
            assertEquals("Wrong key ", 10, e.<TestEntity>getOptimistic().get(2).getKey());

            assertEquals("Wrong update size ", 7, e.<TestEntity>getUpdated().size());
        }
        for (TestEntity t : testEntities) {
            if (t == testEntities.get(0) || t == testEntities.get(10) || t == testEntities.get(5) || t == testEntities.get(4)) {
                assertEquals("Wrong version", 0, t.getVersion());
            } else {
                assertEquals("Wrong version", 1, t.getVersion());
            }
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdateOptimisticAndContrainted_1.xml"));
    }

    @Test
    public void testUpdateOptimisticAndContrainted_2() throws Throwable {
        List<TestEntity> testEntities = null;

        testEntities = dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

        TestEntity optimistic1 = testEntities.get(0);
        TestEntity optimistic2 = testEntities.get(4);
        TestEntity optimistic3 = testEntities.get(10);

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
            assertEquals(10, dbTool
                    .update("update test_table set version=?, name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                            "select xkey, version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                            TestEntity.VERSION));
            fail("Must be update Exception");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong key ", 3, e.<TestEntity>getConstrainted().get(0).getKey());
            assertEquals("Wrong size ", 3, e.<TestEntity>getOptimistic().size());
            assertEquals("Wrong key ", 0, e.<TestEntity>getOptimistic().get(0).getKey());
            assertEquals("Wrong key ", 4, e.<TestEntity>getOptimistic().get(1).getKey());
            assertEquals("Wrong key ", 10, e.<TestEntity>getOptimistic().get(2).getKey());

            assertEquals("Wrong update size ", 7, e.<TestEntity>getUpdated().size());
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdateOptimisticAndContrainted_2.xml"));
    }


    @Test
    public void testUpdateOptimisticAndContrainted_3() throws Throwable {
        List<TestEntity> testEntities = null;

        testEntities = dbTool.selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

        TestEntity optimistic1 = testEntities.get(4);


        assertEquals(1, dbTool.getJdbcTemplate()
                .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic1.getName(),
                        optimistic1.getKey(), optimistic1.getVersion()));

        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(0).setName(new String(buff));
        testEntities.get(5).setName(new String(buff));
        testEntities.get(10).setName(new String(buff));

        try {
            assertEquals(10, dbTool
                    .update("update test_table set version=?, name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                            "select xkey, version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                            TestEntity.VERSION));
            fail("Must be update Exception");
        } catch (UpdateException e) {
            assertEquals("Wrong size ", 3, e.<TestEntity>getConstrainted().size());
            assertEquals("Wrong size ", 1, e.<TestEntity>getOptimistic().size());
            assertEquals("Wrong key ", 4, e.<TestEntity>getOptimistic().get(0).getKey());
            assertEquals("Wrong key ", 0, e.<TestEntity>getConstrainted().get(0).getKey());
            assertEquals("Wrong key ", 5, e.<TestEntity>getConstrainted().get(1).getKey());
            assertEquals("Wrong key ", 10, e.<TestEntity>getConstrainted().get(2).getKey());

            assertEquals("Wrong update size ", 7, e.<TestEntity>getUpdated().size());
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("../data/testUpdateOptimisticAndContrainted_3.xml"));
    }
}
