package ru.kwanza.dbtool;

import org.dbunit.Assertion;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.TransactionStatus;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Guzanov Alexander
 */
public abstract class TestUpdateUtil extends AbstractTestUpdateUtil {

    public void testSuccessUpdate() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = getDBTool().selectList("select xkey,name,version from test_table", LIST_ROW_MAPPER);
            for (TestEntity e : testEntities) {
                e.incrementVersion();
            }
            try {
                assertEquals(11, getDBTool().update("update test_table set version=? where xkey=?", testEntities, TEST_BATCHER_1));
            } catch (UpdateException e) {
                fail("Must never throw!");
                e.printStackTrace();
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
            List<TestEntity> testEntities = getDBTool().selectList("select xkey,name,version from test_table order by xkey",
                    LIST_ROW_MAPPER);
            for (TestEntity e : testEntities) {
                e.incrementVersion();
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(0).setName(new String(buff));


            try {
                assertEquals(10, getDBTool().update("update test_table set version=?, name=? where xkey=?", testEntities, TEST_BATCHER_2));
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 0, e.<TestEntity>getConstrainted().get(0).getKey());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_1.xml"));
    }

    //
    public void testUpdateConstrained_2() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = getDBTool().selectList("select xkey,name,version from test_table order by xkey",
                    LIST_ROW_MAPPER);
            for (TestEntity e : testEntities) {
                e.incrementVersion();
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(testEntities.size() - 1).setName(new String(buff));


            try {
                assertEquals(10, getDBTool().update("update test_table set version=?, name=? where xkey=?", testEntities, TEST_BATCHER_2));
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 10, e.<TestEntity>getConstrainted().get(0).getKey());
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
            List<TestEntity> testEntities = getDBTool().selectList("select xkey,name,version from test_table order by xkey",
                    LIST_ROW_MAPPER);
            for (TestEntity e : testEntities) {
                e.incrementVersion();
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(testEntities.size() - 1).setName(new String(buff));
            testEntities.get(0).setName(new String(buff));


            try {
                assertEquals(9, getDBTool().update("update test_table set version=?, name=? where xkey=?", testEntities, TEST_BATCHER_2));
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 2, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 0, e.<TestEntity>getConstrainted().get(0).getKey());
                assertEquals("Wrong key ", 10, e.<TestEntity>getConstrainted().get(1).getKey());
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
            List<TestEntity> testEntities = getDBTool().selectList("select xkey,name,version from test_table order by xkey",
                    LIST_ROW_MAPPER);
            for (TestEntity e : testEntities) {
                e.incrementVersion();
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(5).setName(new String(buff));

            try {
                assertEquals(10, getDBTool().update("update test_table set version=?, name=? where xkey=?", testEntities, TEST_BATCHER_2));
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 5, e.<TestEntity>getConstrainted().get(0).getKey());
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
            List<TestEntity> testEntities = getDBTool().selectList("select xkey,name,version from test_table order by xkey",
                    LIST_ROW_MAPPER);
            for (TestEntity e : testEntities) {
                e.incrementVersion();
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(testEntities.size() - 1).setName(new String(buff));
            testEntities.get(0).setName(new String(buff));
            testEntities.get(5).setName(new String(buff));


            try {
                assertEquals(9, getDBTool().update("update test_table set version=?, name=? where xkey=?", testEntities, TEST_BATCHER_2));
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 3, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 0, e.<TestEntity>getConstrainted().get(0).getKey());
                assertEquals("Wrong key ", 5, e.<TestEntity>getConstrainted().get(1).getKey());
                assertEquals("Wrong key ", 10, e.<TestEntity>getConstrainted().get(2).getKey());
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
            List<TestEntity> testEntities = getDBTool().selectList("select xkey,name,version from test_table order by xkey",
                    LIST_ROW_MAPPER);
            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            for (TestEntity e : testEntities) {
                e.incrementVersion();
                e.setName(new String(buff));
            }

            try {
                assertEquals(0, getDBTool().update("update test_table set version=?, name=? where xkey=?", testEntities, TEST_BATCHER_2));
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                List<TestEntity> list = e.<TestEntity>getConstrainted();
                assertEquals("Wrong size ", 11, list.size());
                for (int i = 0; i < list.size(); i++) {
                    assertEquals(i, list.get(i).getKey());
                }
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_6.xml"));
    }


    public void testInsertSuccess() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>(10);
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            try {
                assertEquals(10, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities, TEST_BATCHER_3));

            } catch (UpdateException e) {
                fail("Must never throw Exception!");
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertSuccess.xml"));
    }

    public void testInsertConstrained_1() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>();
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(0).setName(new String(buff));

            try {
                assertEquals(9, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities, TEST_BATCHER_3));

                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 11, e.<TestEntity>getConstrainted().get(0).getKey());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_1.xml"));
    }


    public void testInsertConstrained_2() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>();
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(testEntities.size() - 1).setName(new String(buff));

            try {
                assertEquals(9, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities, TEST_BATCHER_3));

                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 20, e.<TestEntity>getConstrainted().get(0).getKey());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_2.xml"));
    }

    public void testInsertConstrained_3() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>();
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(4).setName(new String(buff));

            try {
                assertEquals(9, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities, TEST_BATCHER_3));

                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 15, e.<TestEntity>getConstrainted().get(0).getKey());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }
        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_3.xml"));
    }

    public void testInsertConstrained_5() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>();
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(testEntities.size() - 1).setName(new String(buff));
            testEntities.get(0).setName(new String(buff));

            try {
                assertEquals(8, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities, TEST_BATCHER_3));

                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 2, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 11, e.<TestEntity>getConstrainted().get(0).getKey());
                assertEquals("Wrong key ", 20, e.<TestEntity>getConstrainted().get(1).getKey());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_5.xml"));
    }

    public void testInsertConstrained_6() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>();
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(testEntities.size() - 1).setName(new String(buff));
            testEntities.get(4).setName(new String(buff));
            testEntities.get(0).setName(new String(buff));

            try {
                assertEquals(7, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities, TEST_BATCHER_3));

                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 3, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 11, e.<TestEntity>getConstrainted().get(0).getKey());
                assertEquals("Wrong key ", 15, e.<TestEntity>getConstrainted().get(1).getKey());
                assertEquals("Wrong key ", 20, e.<TestEntity>getConstrainted().get(2).getKey());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_6.xml"));
    }

    public void testInsertConstrained_ByKey() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>();
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(21 + i, "Test_" + (11 + i), 0));
            }

            testEntities.add(new TestEntity(0, "Test_" + (11), 0));
            testEntities.add(new TestEntity(8, "Test_" + (11), 0));
            testEntities.add(new TestEntity(6, "Test_" + (11), 0));

            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(51 + i, "Test_" + (11 + i), 0));
            }

            try {
                assertEquals(20, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities, TEST_BATCHER_3));

                fail("Must throw Exception!");
            } catch (DuplicateKeyException e) {
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }
    }

    public void testInsertConstrained_ByNullable() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>();
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            try {
                assertEquals(9, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities, new UpdateSetter<TestEntity>() {
                    public boolean setValues(PreparedStatement pst, TestEntity object) throws SQLException {
                        if (object.getKey() == 11) {
                            pst.setNull(3, java.sql.Types.INTEGER);
                        } else {
                            pst.setInt(3, object.getVersion());
                        }
                        pst.setString(2, object.getName());
                        pst.setInt(1, object.getKey());
                        return true;
                    }
                }));

                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 11, e.<TestEntity>getConstrainted().get(0).getKey());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_ByNullable.xml"));
    }


    public void testUpdateConstrained_ByNulableConstrained() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = getDBTool().selectList("select xkey,name,version from test_table order by xkey",
                    LIST_ROW_MAPPER);
            for (TestEntity e : testEntities) {
                e.incrementVersion();
            }

            try {
                assertEquals(9, getDBTool().update("update test_table set version=?, name=?, xkey=? where xkey=?", testEntities, new UpdateSetter<TestEntity>() {
                    public boolean setValues(PreparedStatement pst, TestEntity object) throws SQLException {
                        if (object.getKey() == 0) {
                            pst.setNull(1, java.sql.Types.INTEGER);
                        } else {
                            pst.setInt(1, object.getVersion());
                        }
                        pst.setString(2, object.getName());
                        pst.setInt(3, object.getKey());
                        pst.setInt(4, object.getKey());
                        return true;
                    }
                }));
                fail("Must throw Exception!");
            } catch (UpdateException e) {
                List<TestEntity> list = e.<TestEntity>getConstrainted();
                assertEquals("Wrong size ", 1, list.size());
                assertEquals("Wrong size ", 0, list.get(0).getKey());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testUpdateConstrained_ByNulableConstrained.xml"));
    }


    public void testInsertSuccess_Skip_1() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>(10);
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            try {
                assertEquals(9, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities,
                        new TestEntityUpdateSetter4(testEntities, 0)));

            } catch (UpdateException e) {
                fail("Must never throw Exception!");
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertSuccess_Skip_1.xml"));
    }

    public void testInsertSuccess_Skip_2() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>(10);
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            try {
                assertEquals(9, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities,
                        new TestEntityUpdateSetter4(testEntities, 9)));

            } catch (UpdateException e) {
                fail("Must never throw Exception!");
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertSuccess_Skip_2.xml"));
    }


    public void testInsertSuccess_Skip_3() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>(10);
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            try {
                assertEquals(9, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities,
                        new TestEntityUpdateSetter4(testEntities, 5)));

            } catch (UpdateException e) {
                fail("Must never throw Exception!");
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertSuccess_Skip_3.xml"));
    }

    public void testInsertSuccess_Skip_4() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>(10);
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            try {
                assertEquals(8, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities,
                        new TestEntityUpdateSetter4(testEntities, 0, 9)));

            } catch (UpdateException e) {
                fail("Must never throw Exception!");
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertSuccess_Skip_4.xml"));
    }


    public void testInsertSuccess_Skip_5() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>(10);
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            try {
                assertEquals(7, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities,
                        new TestEntityUpdateSetter4(testEntities, 0, 5, 9)));

            } catch (UpdateException e) {
                fail("Must never throw Exception!");
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertSuccess_Skip_5.xml"));
    }

    public void testInsertSuccess_Skip_6() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>(10);
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            try {
                assertEquals(0, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities,
                        new TestEntityUpdateSetter4(testEntities, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9)));

            } catch (UpdateException e) {
                fail("Must never throw Exception!");
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertSuccess_Skip_6.xml"));
    }

    public void testInsertSuccess_Skip_7() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>(10);
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            try {
                assertEquals(1, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities,
                        new TestEntityUpdateSetter4(testEntities, 0, 1, 2, 3, 4, 5, 6, 7, 8)));

            } catch (UpdateException e) {
                fail("Must never throw Exception!");
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertSuccess_Skip_7.xml"));
    }

    public void testInsertSuccess_Skip_8() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>(10);
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            try {
                assertEquals(1, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities,
                        new TestEntityUpdateSetter4(testEntities, 1, 2, 3, 4, 5, 6, 7, 8, 9)));

            } catch (UpdateException e) {
                fail("Must never throw Exception!");
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertSuccess_Skip_8.xml"));
    }

    public void testInsertSuccess_Skip_9() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>(10);
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            try {
                assertEquals(2, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities,
                        new TestEntityUpdateSetter4(testEntities, 1, 2, 3, 4, 5, 6, 7, 8)));

            } catch (UpdateException e) {
                fail("Must never throw Exception!");
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertSuccess_Skip_9.xml"));
    }

    public void testInsertSuccess_Skip_10() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>(10);
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            try {
                assertEquals(4, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities,
                        new TestEntityUpdateSetter4(testEntities, 0, 1, 5, 6, 8, 9)));

            } catch (UpdateException e) {
                fail("Must never throw Exception!");
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertSuccess_Skip_10.xml"));
    }


    public void testInsertSuccess_Skip_11() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>(10);
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            try {
                assertEquals(6, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities,
                        new TestEntityUpdateSetter4(testEntities, 1, 5, 6, 8)));

            } catch (UpdateException e) {
                fail("Must never throw Exception!");
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertSuccess_Skip_11.xml"));
    }


    public void testInsertConstrained_Skip_1() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>();
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(0).setName(new String(buff));

            try {
                assertEquals(8, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities,
                        new TestEntityUpdateSetter4(testEntities, 9)));

                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 11, e.<TestEntity>getConstrainted().get(0).getKey());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_Skip_1.xml"));
    }

    //
    public void testInsertConstrained_Skip_2() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>();
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(5).setName(new String(buff));


            assertEquals(9, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities,
                    new TestEntityUpdateSetter4(testEntities, 5)));

            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_Skip_2.xml"));
    }


    public void testInsertConstrained_Skip_3() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>();
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(5).setName(new String(buff));

            try {
                assertEquals(7, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities,
                        new TestEntityUpdateSetter4(testEntities, 0, 9)));

                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 16, e.<TestEntity>getConstrainted().get(0).getKey());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_Skip_3.xml"));
    }

    public void testInsertConstrained_Skip_4() throws Throwable {
        TransactionStatus status = getTxManager().getTransaction(getTxDef());
        try {
            List<TestEntity> testEntities = new ArrayList<TestEntity>();
            for (int i = 0; i < 10; i++) {
                testEntities.add(new TestEntity(11 + i, "Test_" + (11 + i), 0));
            }

            char[] buff = new char[1024];
            Arrays.fill(buff, 'S');
            testEntities.get(1).setName(new String(buff));

            try {
                assertEquals(8, getDBTool().update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities,
                        new TestEntityUpdateSetter4(testEntities, 0)));

                fail("Must throw Exception!");
            } catch (UpdateException e) {
                assertEquals("Wrong size ", 1, e.<TestEntity>getConstrainted().size());
                assertEquals("Wrong key ", 12, e.<TestEntity>getConstrainted().get(0).getKey());
            }
            getTxManager().commit(status);
        } catch (Throwable e) {
            getTxManager().rollback(status);
            throw e;
        }

        Assertion.assertEquals(getActualDataSet(), getResourceSet("./data/testInsertConstrained_Skip_4.xml"));
    }


}
