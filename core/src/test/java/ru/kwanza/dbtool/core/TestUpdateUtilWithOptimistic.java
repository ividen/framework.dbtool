package ru.kwanza.dbtool.core;

import org.dbunit.Assertion;
import org.springframework.transaction.TransactionStatus;

import java.util.Arrays;
import java.util.List;

/**
 * @author Guzanov Alexander
 */
public abstract class TestUpdateUtilWithOptimistic extends AbstractTestUpdateUtil {

    public void testSuccessUpdate() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = getDBTool().selectList("select xkey,name,version from test_table", LIST_ROW_MAPPER);

            try {
                assertEquals(11, getDBTool()
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
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testSuccessUpdate.xml"));

    }

    public void testUpdateConstrained_1() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities =
                    getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(0).setName(new String(buff));

            try {
                assertEquals(10, getDBTool()
                        .update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                                "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                                TestEntity.VERSION));
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 0, e.<TestEntity>getConstrainted().get(0).getKey());
            }

            for (TestEntity t : testEntities) {
                if (t == testEntities.get(0)) {
                    assertEquals("Wrong version", 0, t.getVersion());
                } else {
                    assertEquals("Wrong version", 1, t.getVersion());
                }
            }

            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_1.xml"));
    }

    public void testUpdateConstrained_2() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities =
                    getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(testEntities.size() - 1).setName(new String(buff));

            try {
                assertEquals(10, getDBTool()
                        .update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                                "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                                TestEntity.VERSION));
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 10, e.<TestEntity>getConstrainted().get(0).getKey());
            }

            for (TestEntity t : testEntities) {
                if (t == testEntities.get(10)) {
                    assertEquals("Wrong version", 0, t.getVersion());
                } else {
                    assertEquals("Wrong version", 1, t.getVersion());
                }
            }

            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_2.xml"));
    }

    public void testUpdateConstrained_3() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities =
                    getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(testEntities.size() - 1).setName(new String(buff));
            testEntities.get(0).setName(new String(buff));

            try {
                assertEquals(9, getDBTool()
                        .update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                                "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                                TestEntity.VERSION));
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 2, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 0, e.<TestEntity>getConstrainted().get(0).getKey());
                assertEquals("Wrong key ", 10, e.<TestEntity>getConstrainted().get(1).getKey());
            }

            for (TestEntity t : testEntities) {
                if (t == testEntities.get(0) || t == testEntities.get(10)) {
                    assertEquals("Wrong version", 0, t.getVersion());
                } else {
                    assertEquals("Wrong version", 1, t.getVersion());
                }
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_3.xml"));
    }

    public void testUpdateConstrained_4() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities =
                    getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(5).setName(new String(buff));

            try {
                assertEquals(10, getDBTool()
                        .update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                                "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                                TestEntity.VERSION));

                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 5, e.<TestEntity>getConstrainted().get(0).getKey());
            }
            for (TestEntity t : testEntities) {
                if (t == testEntities.get(5)) {
                    assertEquals("Wrong version", 0, t.getVersion());
                } else {
                    assertEquals("Wrong version", 1, t.getVersion());
                }
            }

            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_4.xml"));
    }

    public void testUpdateConstrained_5() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities =
                    getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(testEntities.size() - 1).setName(new String(buff));
            testEntities.get(0).setName(new String(buff));
            testEntities.get(5).setName(new String(buff));

            try {
                assertEquals(9, getDBTool()
                        .update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                                "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                                TestEntity.VERSION));
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 3, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 0, e.<TestEntity>getConstrainted().get(0).getKey());
                assertEquals("Wrong key ", 5, e.<TestEntity>getConstrainted().get(1).getKey());
                assertEquals("Wrong key ", 10, e.<TestEntity>getConstrainted().get(2).getKey());
            }
            for (TestEntity t : testEntities) {
                if (t == testEntities.get(0) || t == testEntities.get(10) || t == testEntities.get(5)) {
                    assertEquals("Wrong version", 0, t.getVersion());
                } else {
                    assertEquals("Wrong version", 1, t.getVersion());
                }
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_5.xml"));
    }

    public void testUpdateConstrained_6() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities =
                    getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            for (TestEntity e : testEntities) {
                e.setName(new String(buff));
            }

            try {
                assertEquals(0, getDBTool()
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
            }

            for (TestEntity t : testEntities) {
                assertEquals("Wrong version", 0, t.getVersion());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_6.xml"));
    }

    public void testUpdate_Skip_1() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities =
                    getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

            try {
                assertEquals(10, getDBTool().update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
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

            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdate_Skip_1.xml"));
    }

    public void testUpdate_Skip_2() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities =
                    getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
            try {
                assertEquals(10, getDBTool().update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
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
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdate_Skip_2.xml"));
    }

    public void testUpdate_Skip_3() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities =
                    getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
            try {
                assertEquals(10, getDBTool().update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
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
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdate_Skip_3.xml"));
    }

    public void testUpdate_Skip_4() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities =
                    getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
            try {
                assertEquals(9, getDBTool().update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
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
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdate_Skip_4.xml"));
    }

    public void testUpdate_Skip_5() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities =
                    getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
            try {
                assertEquals(8, getDBTool().update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
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
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdate_Skip_5.xml"));
    }

    public void testUpdate_Skip_6() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities =
                    getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
            try {
                assertEquals(0, getDBTool().update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
                        new TestEntityUpdateSetter5(testEntities, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY, TestEntity.VERSION));

            } catch (UpdateException e) {
                fail("Must never throw Exception!");
            }
            for (TestEntity t : testEntities) {
                assertEquals("Wrong version", 0, t.getVersion());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdate_Skip_6.xml"));
    }

    public void testUpdate_Skip_7() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities =
                    getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

            try {
                assertEquals(1, getDBTool().update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
                        new TestEntityUpdateSetter5(testEntities, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                        "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY, TestEntity.VERSION));

            } catch (UpdateException e) {
                fail("Must never throw Exception!");
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdate_Skip_7.xml"));
    }

    public void testUpdate_Skip_8() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities =
                    getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

            try {
                assertEquals(1, getDBTool().update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
                        new TestEntityUpdateSetter5(testEntities, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY, TestEntity.VERSION));

            } catch (UpdateException e) {
                fail("Must never throw Exception!");
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdate_Skip_8.xml"));
    }

    public void testUpdate_Skip_9() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities =
                    getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

            try {
                assertEquals(2, getDBTool().update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
                        new TestEntityUpdateSetter5(testEntities, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                        "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY, TestEntity.VERSION));

            } catch (UpdateException e) {
                fail("Must never throw Exception!");
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdate_Skip_9.xml"));
    }

    public void testUpdate_Skip_10() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities =
                    getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
            try {
                assertEquals(5, getDBTool().update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
                        new TestEntityUpdateSetter5(testEntities, 0, 1, 5, 6, 9, 10),
                        "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY, TestEntity.VERSION));

            } catch (UpdateException e) {
                fail("Must never throw Exception!");
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdate_Skip_10.xml"));
    }

    public void testUpdate_Skip_11() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities =
                    getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);

            try {
                assertEquals(7, getDBTool().update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities,
                        new TestEntityUpdateSetter5(testEntities, 1, 5, 6, 9), "select xkey,version from test_table where xkey in(?)",
                        KEY_VERSION_MAPPER, TestEntity.KEY, TestEntity.VERSION));

            } catch (UpdateException e) {
                fail("Must never throw Exception!");
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdate_Skip_11.xml"));
    }

    public void testUpdateOptimistic_1() throws Throwable {
        List<TestEntity> testEntities = null;
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {

            testEntities = getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        TestEntity optimistic = testEntities.get(0);
        status = getTxManager().getTransaction(getTxDef());
        try {
            assertEquals(1, getDBTool().getJdbcTemplate()
                    .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic.getName(),
                            optimistic.getKey(), optimistic.getVersion()));
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        status = getTxManager().getTransaction(getTxDef());
        try {
            try {
                assertEquals(10, getDBTool()
                        .update("update test_table set version=?, name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                                "select xkey, version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                                TestEntity.VERSION));
                fail("Must be update Exception");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 0, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong size ", 1, e.<TestEntity>getOptimistic().size());
                assertEquals("Wrong key ", 0, e.<TestEntity>getOptimistic().get(0).getKey());
            }

            for (TestEntity t : testEntities) {
                if (t == testEntities.get(0)) {
                    assertEquals("Wrong version", 0, t.getVersion());
                } else {
                    assertEquals("Wrong version", 1, t.getVersion());
                }
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimistic_1.xml"));
    }

    public void testUpdateOptimistic_2() throws Throwable {
        List<TestEntity> testEntities = null;
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {

            testEntities = getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        TestEntity optimistic = testEntities.get(testEntities.size() - 1);
        status = getTxManager().getTransaction(getTxDef());
        try {
            assertEquals(1, getDBTool().getJdbcTemplate()
                    .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic.getName(),
                            optimistic.getKey(), optimistic.getVersion()));
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        status = getTxManager().getTransaction(getTxDef());
        try {
            try {
                assertEquals(10, getDBTool()
                        .update("update test_table set version=?, name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                                "select xkey, version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                                TestEntity.VERSION));
                fail("Must be update Exception");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 0, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong size ", 1, e.<TestEntity>getOptimistic().size());
                assertEquals("Wrong key ", 10, e.<TestEntity>getOptimistic().get(0).getKey());
            }
            for (TestEntity t : testEntities) {
                if (t == testEntities.get(10)) {
                    assertEquals("Wrong version", 0, t.getVersion());
                } else {
                    assertEquals("Wrong version", 1, t.getVersion());
                }
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimistic_2.xml"));
    }

    public void testUpdateOptimistic_3() throws Throwable {
        List<TestEntity> testEntities = null;
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {

            testEntities = getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        TestEntity optimistic = testEntities.get(4);
        status = getTxManager().getTransaction(getTxDef());
        try {
            assertEquals(1, getDBTool().getJdbcTemplate()
                    .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic.getName(),
                            optimistic.getKey(), optimistic.getVersion()));
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        status = getTxManager().getTransaction(getTxDef());
        try {
            try {
                assertEquals(10, getDBTool()
                        .update("update test_table set version=?, name=? where xkey=? and version=?", testEntities, TEST_BATCHER_6,
                                "select xkey, version from test_table where xkey in(?)", KEY_VERSION_MAPPER, TestEntity.KEY,
                                TestEntity.VERSION));
                fail("Must be update Exception");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 0, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong size ", 1, e.<TestEntity>getOptimistic().size());
                assertEquals("Wrong key ", 4, e.<TestEntity>getOptimistic().get(0).getKey());
            }
            for (TestEntity t : testEntities) {
                if (t == testEntities.get(4)) {
                    assertEquals("Wrong version", 0, t.getVersion());
                } else {
                    assertEquals("Wrong version", 1, t.getVersion());
                }
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimistic_3.xml"));
    }

    public void testUpdateOptimistic_4() throws Throwable {
        List<TestEntity> testEntities = null;
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {

            testEntities = getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        TestEntity optimistic1 = testEntities.get(0);
        TestEntity optimistic2 = testEntities.get(4);
        TestEntity optimistic3 = testEntities.get(10);
        status = getTxManager().getTransaction(getTxDef());
        try {
            assertEquals(1, getDBTool().getJdbcTemplate()
                    .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic1.getName(),
                            optimistic1.getKey(), optimistic1.getVersion()));
            assertEquals(1, getDBTool().getJdbcTemplate()
                    .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic2.getName(),
                            optimistic2.getKey(), optimistic2.getVersion()));
            assertEquals(1, getDBTool().getJdbcTemplate()
                    .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic3.getName(),
                            optimistic3.getKey(), optimistic3.getVersion()));
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        status = getTxManager().getTransaction(getTxDef());
        try {
            try {
                assertEquals(10, getDBTool()
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
            }
            for (TestEntity t : testEntities) {
                if (t == testEntities.get(0) || t == testEntities.get(10) || t == testEntities.get(4)) {
                    assertEquals("Wrong version", 0, t.getVersion());
                } else {
                    assertEquals("Wrong version", 1, t.getVersion());
                }
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimistic_4.xml"));
    }

    public void testUpdateOptimisticAndContrainted_1() throws Throwable {
        List<TestEntity> testEntities = null;
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {

            testEntities = getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }
        TestEntity optimistic1 = testEntities.get(0);
        TestEntity optimistic2 = testEntities.get(4);
        TestEntity optimistic3 = testEntities.get(10);

        status = getTxManager().getTransaction(getTxDef());
        try {
            assertEquals(1, getDBTool().getJdbcTemplate()
                    .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic1.getName(),
                            optimistic1.getKey(), optimistic1.getVersion()));
            assertEquals(1, getDBTool().getJdbcTemplate()
                    .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic2.getName(),
                            optimistic2.getKey(), optimistic2.getVersion()));
            assertEquals(1, getDBTool().getJdbcTemplate()
                    .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic3.getName(),
                            optimistic3.getKey(), optimistic3.getVersion()));
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        status = getTxManager().getTransaction(getTxDef());
        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(5).setName(new String(buff));

        try {
            try {
                assertEquals(10, getDBTool()
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
            }
            for (TestEntity t : testEntities) {
                if (t == testEntities.get(0) || t == testEntities.get(10) || t == testEntities.get(5) || t == testEntities.get(4)) {
                    assertEquals("Wrong version", 0, t.getVersion());
                } else {
                    assertEquals("Wrong version", 1, t.getVersion());
                }
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimisticAndContrainted_1.xml"));
    }

    public void testUpdateOptimisticAndContrainted_2() throws Throwable {
        List<TestEntity> testEntities = null;
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {

            testEntities = getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }
        TestEntity optimistic1 = testEntities.get(0);
        TestEntity optimistic2 = testEntities.get(4);
        TestEntity optimistic3 = testEntities.get(10);

        status = getTxManager().getTransaction(getTxDef());
        try {
            assertEquals(1, getDBTool().getJdbcTemplate()
                    .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic1.getName(),
                            optimistic1.getKey(), optimistic1.getVersion()));
            assertEquals(1, getDBTool().getJdbcTemplate()
                    .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic2.getName(),
                            optimistic2.getKey(), optimistic2.getVersion()));
            assertEquals(1, getDBTool().getJdbcTemplate()
                    .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic3.getName(),
                            optimistic3.getKey(), optimistic3.getVersion()));
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        status = getTxManager().getTransaction(getTxDef());
        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(3).setName(new String(buff));

        try {
            try {
                assertEquals(10, getDBTool()
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
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimisticAndContrainted_2.xml"));
    }

    public void testUpdateOptimisticAndContrainted_3() throws Throwable {
        List<TestEntity> testEntities = null;
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {

            testEntities = getDBTool().selectList("select xkey,name,version from test_table order by xkey", LIST_ROW_MAPPER);
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }
        TestEntity optimistic1 = testEntities.get(4);

        status = getTxManager().getTransaction(getTxDef());
        try {

            assertEquals(1, getDBTool().getJdbcTemplate()
                    .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic1.getName(),
                            optimistic1.getKey(), optimistic1.getVersion()));
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        status = getTxManager().getTransaction(getTxDef());
        char[] buff = new char[1024];
        Arrays.fill(buff, 'S');
        testEntities.get(0).setName(new String(buff));
        testEntities.get(5).setName(new String(buff));
        testEntities.get(10).setName(new String(buff));

        try {
            try {
                assertEquals(10, getDBTool()
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
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimisticAndContrainted_3.xml"));
    }
}
