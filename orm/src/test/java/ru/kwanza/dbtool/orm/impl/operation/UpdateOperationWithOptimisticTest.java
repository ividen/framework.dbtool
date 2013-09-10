package ru.kwanza.dbtool.orm.impl.operation;

import org.dbunit.Assertion;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.transaction.TransactionStatus;
import ru.kwanza.dbtool.core.UpdateException;

import java.util.Arrays;
import java.util.List;

/**
 * @author Kiryl Karatsetski
 */
public abstract class UpdateOperationWithOptimisticTest extends AbstractOperationTest {

    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(this.getClass().getResourceAsStream("./data/data_set_version.xml"));
    }

    public void testSuccessUpdate() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            final List<TestEntityVersion> testEntities = selectTestEntities();
            try {
                getEntityManager().update(TestEntityVersion.class, testEntities);
            } catch (UpdateException e) {
                e.printStackTrace();
                fail("Must never throw!");
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testSuccessUpdate_version.xml"));
    }

    public void testUpdateConstrained_1() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            final List<TestEntityVersion> testEntities = selectTestEntities();
            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(0).setName(new String(buff));
            try {
                getEntityManager().update(TestEntityVersion.class, testEntities);
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntityVersion>getConstrainted().size());
                assertEquals("Wrong key ", 0, e.<TestEntityVersion>getConstrainted().get(0).getKey().longValue());
            }

            int version = 0;
            for (TestEntityVersion TestEntityVersion : testEntities) {
                version += 100;
                if (TestEntityVersion == testEntities.get(0)) {
                    assertEquals("Wrong version", 1, TestEntityVersion.getVersion().longValue());
                } else {
                    assertEquals("Wrong version", version, TestEntityVersion.getVersion().longValue());
                }
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_1_version.xml"));
    }

    public void testUpdateConstrained_2() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            final List<TestEntityVersion> testEntities = selectTestEntities();
            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(testEntities.size() - 1).setName(new String(buff));
            try {
                getEntityManager().update(TestEntityVersion.class, testEntities);
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntityVersion>getConstrainted().size());
                assertEquals("Wrong key ", 10, e.<TestEntityVersion>getConstrainted().get(0).getKey().longValue());
            }

            int version = 0;
            for (TestEntityVersion TestEntityVersion : testEntities) {
                version += 100;
                if (TestEntityVersion == testEntities.get(10)) {
                    assertEquals("Wrong version", 1, TestEntityVersion.getVersion().longValue());
                } else {
                    assertEquals("Wrong version", version, TestEntityVersion.getVersion().longValue());
                }
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_2_version.xml"));
    }

    public void testUpdateConstrained_3() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            final List<TestEntityVersion> testEntities = selectTestEntities();
            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(testEntities.size() - 1).setName(new String(buff));
            testEntities.get(0).setName(new String(buff));
            try {
                getEntityManager().update(TestEntityVersion.class, testEntities);
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
                    assertEquals("Wrong version", 1, TestEntityVersion.getVersion().longValue());
                } else {
                    assertEquals("Wrong version", version, TestEntityVersion.getVersion().longValue());
                }
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_3_version.xml"));
    }

    public void testUpdateConstrained_4() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            final List<TestEntityVersion> testEntities = selectTestEntities();
            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(5).setName(new String(buff));
            try {
                getEntityManager().update(TestEntityVersion.class, testEntities);
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntityVersion>getConstrainted().size());
                assertEquals("Wrong key ", 5, e.<TestEntityVersion>getConstrainted().get(0).getKey().longValue());
            }

            int version = 0;
            for (TestEntityVersion TestEntityVersion : testEntities) {
                version += 100;
                if (TestEntityVersion == testEntities.get(5)) {
                    assertEquals("Wrong version", 1, TestEntityVersion.getVersion().longValue());
                } else {
                    assertEquals("Wrong version", version, TestEntityVersion.getVersion().longValue());
                }
            }

            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_4_version.xml"));
    }

    public void testUpdateConstrained_5() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            final List<TestEntityVersion> testEntities =
                    getEntityManager().queryBuilder(TestEntityVersion.class).orderBy("key").create().prepare().selectList();
            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(testEntities.size() - 1).setName(new String(buff));
            testEntities.get(0).setName(new String(buff));
            testEntities.get(5).setName(new String(buff));

            try {
                getEntityManager().update(TestEntityVersion.class, testEntities);
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
                    assertEquals("Wrong version", 1, TestEntityVersion.getVersion().longValue());
                } else {
                    assertEquals("Wrong version", version, TestEntityVersion.getVersion().longValue());
                }
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_5_version.xml"));
    }

    public void testUpdateConstrained_6() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            final List<TestEntityVersion> testEntities = selectTestEntities();
            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            for (TestEntityVersion e : testEntities) {
                e.setName(new String(buff));
            }

            try {
                getEntityManager().update(TestEntityVersion.class, testEntities);
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                List<TestEntityVersion> list = e.getConstrainted();
                assertEquals("Wrong size ", 11, list.size());
                for (int i = 0; i < list.size(); i++) {
                    assertEquals(i, list.get(i).getKey().longValue());
                }
            }

            for (TestEntityVersion TestEntityVersion : testEntities) {
                assertEquals("Wrong version", 1, TestEntityVersion.getVersion().longValue());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_6_version.xml"));
    }

    public void testUpdateOptimistic_1() throws Throwable {
        final List<TestEntityVersion> testEntities;
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            testEntities = selectTestEntities();
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        final TestEntityVersion optimistic = testEntities.get(0);
        status = getTxManager().getTransaction(getTxDef());
        try {
            getDBTool().getJdbcTemplate()
                    .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic.getName(),
                            optimistic.getKey(), optimistic.getVersion());
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        status = getTxManager().getTransaction(getTxDef());
        try {
            try {
                getEntityManager().update(TestEntityVersion.class, testEntities);
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
                    assertEquals("Wrong version", 1, TestEntityVersion.getVersion().longValue());
                } else {
                    assertEquals("Wrong version", version, TestEntityVersion.getVersion().longValue());
                }
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimistic_1_version.xml"));
    }

    public void testUpdateOptimistic_2() throws Throwable {
        List<TestEntityVersion> testEntities = null;
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            testEntities = selectTestEntities();
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        final TestEntityVersion optimistic = testEntities.get(testEntities.size() - 1);
        status = getTxManager().getTransaction(getTxDef());
        try {
            getDBTool().getJdbcTemplate()
                    .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic.getName(),
                            optimistic.getKey(), optimistic.getVersion());
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        status = getTxManager().getTransaction(getTxDef());
        try {
            try {
                getEntityManager().update(TestEntityVersion.class, testEntities);
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
                    assertEquals("Wrong version", 1, TestEntityVersion.getVersion().longValue());
                } else {
                    assertEquals("Wrong version", version, TestEntityVersion.getVersion().longValue());
                }
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimistic_2_version.xml"));
    }

    public void testUpdateOptimistic_3() throws Throwable {
        List<TestEntityVersion> testEntities = null;
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            testEntities = selectTestEntities();
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        final TestEntityVersion optimistic = testEntities.get(4);
        status = getTxManager().getTransaction(getTxDef());
        try {
            getDBTool().getJdbcTemplate()
                    .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic.getName(),
                            optimistic.getKey(), optimistic.getVersion());
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        status = getTxManager().getTransaction(getTxDef());
        try {
            try {
                getEntityManager().update(TestEntityVersion.class, testEntities);
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
                    assertEquals("Wrong version", 1, TestEntityVersion.getVersion().longValue());
                } else {
                    assertEquals("Wrong version", version, TestEntityVersion.getVersion().longValue());
                }
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimistic_3_version.xml"));
    }

    public void testUpdateOptimistic_4() throws Throwable {
        final List<TestEntityVersion> testEntities;
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            testEntities = selectTestEntities();
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        TestEntityVersion optimistic1 = testEntities.get(0);
        TestEntityVersion optimistic2 = testEntities.get(4);
        TestEntityVersion optimistic3 = testEntities.get(10);
        status = getTxManager().getTransaction(getTxDef());
        try {
            getDBTool().getJdbcTemplate()
                    .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic1.getName(),
                            optimistic1.getKey(), optimistic1.getVersion());
            getDBTool().getJdbcTemplate()
                    .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic2.getName(),
                            optimistic2.getKey(), optimistic2.getVersion());
            getDBTool().getJdbcTemplate()
                    .update("update test_table set version=version+2, name=? where xkey=? and version=?", optimistic3.getName(),
                            optimistic3.getKey(), optimistic3.getVersion());
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        status = getTxManager().getTransaction(getTxDef());
        try {
            try {
                getEntityManager().update(TestEntityVersion.class, testEntities);
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
                    assertEquals("Wrong version", 1, TestEntityVersion.getVersion().longValue());
                } else {
                    assertEquals("Wrong version", version, TestEntityVersion.getVersion().longValue());
                }
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimistic_4_version.xml"));
    }

    public void testUpdateOptimisticAndContrainted_1() throws Throwable {
        final List<TestEntityVersion> testEntities;
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            testEntities = selectTestEntities();
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }
        TestEntityVersion optimistic1 = testEntities.get(0);
        TestEntityVersion optimistic2 = testEntities.get(4);
        TestEntityVersion optimistic3 = testEntities.get(10);

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
                getEntityManager().update(TestEntityVersion.class, testEntities);
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
                    assertEquals("Wrong version", 1, testEntity.getVersion().longValue());
                } else {
                    assertEquals("Wrong version", version, testEntity.getVersion().longValue());
                }
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimisticAndContrainted_1_version.xml"));
    }

    public void testUpdateOptimisticAndContrainted_2() throws Throwable {
        final List<TestEntityVersion> testEntities;
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            testEntities = selectTestEntities();
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }
        TestEntityVersion optimistic1 = testEntities.get(0);
        TestEntityVersion optimistic2 = testEntities.get(4);
        TestEntityVersion optimistic3 = testEntities.get(10);

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
                getEntityManager().update(TestEntityVersion.class, testEntities);
                fail("Must be update Exception");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntityVersion>getConstrainted().size());
                assertEquals("Wrong key ", 3, e.<TestEntityVersion>getConstrainted().get(0).getKey().longValue());
                assertEquals("Wrong size ", 3, e.<TestEntityVersion>getOptimistic().size());
                assertEquals("Wrong key ", 0, e.<TestEntityVersion>getOptimistic().get(0).getKey().longValue());
                assertEquals("Wrong key ", 4, e.<TestEntityVersion>getOptimistic().get(1).getKey().longValue());
                assertEquals("Wrong key ", 10, e.<TestEntityVersion>getOptimistic().get(2).getKey().longValue());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimisticAndContrainted_2_version.xml"));
    }

    public void testUpdateOptimisticAndContrainted_3() throws Throwable {
        final List<TestEntityVersion> testEntities;
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            testEntities = selectTestEntities();
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }
        TestEntityVersion optimistic1 = testEntities.get(4);

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
                getEntityManager().update(TestEntityVersion.class, testEntities);
                fail("Must be update Exception");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 3, e.<TestEntityVersion>getConstrainted().size());
                assertEquals("Wrong size ", 1, e.<TestEntityVersion>getOptimistic().size());
                assertEquals("Wrong key ", 4, e.<TestEntityVersion>getOptimistic().get(0).getKey().longValue());
                assertEquals("Wrong key ", 0, e.<TestEntityVersion>getConstrainted().get(0).getKey().longValue());
                assertEquals("Wrong key ", 5, e.<TestEntityVersion>getConstrainted().get(1).getKey().longValue());
                assertEquals("Wrong key ", 10, e.<TestEntityVersion>getConstrainted().get(2).getKey().longValue());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateOptimisticAndContrainted_3_version.xml"));
    }

    private List<TestEntityVersion> selectTestEntities() {
        return getEntityManager().queryBuilder(TestEntityVersion.class).orderBy("key").create().prepare().selectList();
    }
}
