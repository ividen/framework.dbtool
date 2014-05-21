package ru.kwanza.dbtool.core.fieldhelper;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.FieldGetter;
import ru.kwanza.dbtool.core.FieldSetter;
import ru.kwanza.dbtool.core.UpdateSetter;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * @author Ivan Baluk
 */

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractTestFieldHelper extends AbstractJUnit4SpringContextTests {
    private static String DELETE_SQL = "delete from test_table1";
    private static String INSERT_SQL =
            "insert into test_table1 (xbool, xint, xbigint, xstring, xts1, xts2, xblob, xbigdecimal) " + "values (?, ?, ?, ?, ?, ?, ?, ?)";
    private static String ORACLE_SELECT_SQL =
            "select xbool, xint, xbigint, xstring, xts1, xts2, xblob, xbigdecimal from test_table1 where rownum > ?";
    private static String MSSQL_SELECT_SQL =
            "select top 10 xbool, xint, xbigint, xstring, xts1, xts2, xblob, xbigdecimal from test_table1 where not(xint = ?) ";

    private static String MYSQL_SELECT_SQL =
            "select xbool, xint, xbigint, xstring, xts1, xts2, xblob, xbigdecimal from test_table1 where not(xint = ?) LIMIT 0,10";

    private static String POSTGRESQL_SELECT_SQL = "select xbool, xint, xbigint, xstring, xts1, xts2, xblob, xbigdecimal from test_table1 where not(xint = ?) LIMIT 10";

    @Resource(name="dbtool.DBTool")
    private  DBTool dbTool;


    @Before
    public void setUp() throws Exception {
        dbTool.getDataSource().getConnection().prepareStatement(DELETE_SQL).execute();
    }

    @Test
    public void testSetters() throws Exception {
        dbTool.update(INSERT_SQL, new ArrayList<Object>(Arrays.asList(1, 2, 3, 4, 5, 6)), new UpdateSetter<Object>() {
            public boolean setValues(PreparedStatement pst, Object object) throws SQLException {
                FieldSetter.setBoolean(pst, 1, true);
                FieldSetter.setInt(pst, 2, 2);
                FieldSetter.setLong(pst, 3, 3l);
                FieldSetter.setString(pst, 4, "4");
                FieldSetter.setTimestamp(pst, 5, new java.util.Date(1000l));
                FieldSetter.setTimestamp(pst, 6, new Timestamp(new java.util.Date(1000l).getTime()));
                FieldSetter.setBlob(pst, 7, "blob".getBytes());
                FieldSetter.setBigDecimal(pst, 8, new BigDecimal(8));
                return true;
            }
        });

        String sql;

        if (dbTool.getDbType() == DBTool.DBType.ORACLE) {
            sql = ORACLE_SELECT_SQL;
        } else if (dbTool.getDbType() == DBTool.DBType.MSSQL) {
            sql = MSSQL_SELECT_SQL;
        } else if (dbTool.getDbType() == DBTool.DBType.MYSQL) {
            sql = MYSQL_SELECT_SQL;
        } else if (dbTool.getDbType() == DBTool.DBType.POSTGRESQL) {
            sql = POSTGRESQL_SELECT_SQL;
        } else {
            throw new RuntimeException("Unsupported database type!");
        }

        dbTool.selectList(sql, new RowMapper<Object>() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                assertEquals((Boolean) true, FieldGetter.getBoolean(rs, "xbool"));
                assertEquals((Integer) 2, FieldGetter.getInteger(rs, "xint"));
                assertEquals((Long) 3l, FieldGetter.getLong(rs, "xbigint"));
                assertEquals("4", FieldGetter.getString(rs, "xstring"));
                assertEquals(new Date(1000l).getTime(), FieldGetter.getTimestamp(rs, "xts1").getTime());
                assertEquals(new Date(1000l).getTime(), FieldGetter.getTimestamp(rs, "xts2").getTime());
                assertEquals("blob", new String(FieldGetter.getBytes(rs, "xblob")));
                assertEquals(new BigDecimal(8), FieldGetter.getBigDecimal(rs, "xbigdecimal"));
                return null;
            }
        }, 0);
    }

    @Test
    public void testSettersNull() throws Exception {
        dbTool.update(INSERT_SQL, new ArrayList<Object>(Arrays.asList(1, 2, 3, 4, 5, 6)), new UpdateSetter<Object>() {
            public boolean setValues(PreparedStatement pst, Object object) throws SQLException {
                FieldSetter.setBoolean(pst, 1, null);
                FieldSetter.setInt(pst, 2, null);
                FieldSetter.setLong(pst, 3, null);
                FieldSetter.setString(pst, 4, null);
                FieldSetter.setTimestamp(pst, 5, (Timestamp) null);
                FieldSetter.setTimestamp(pst, 6, (Date) null);
                FieldSetter.setBlob(pst, 7, (byte[]) null);
                FieldSetter.setBigDecimal(pst, 8, null);
                return true;
            }
        });

        String sql;

        if (dbTool.getDbType() == DBTool.DBType.ORACLE) {
            sql = ORACLE_SELECT_SQL;
        } else if (dbTool.getDbType() == DBTool.DBType.MSSQL) {
            sql = MSSQL_SELECT_SQL;
        } else if (dbTool.getDbType() == DBTool.DBType.MYSQL) {
            sql = MYSQL_SELECT_SQL;
        } else if (dbTool.getDbType() == DBTool.DBType.POSTGRESQL) {
            sql = POSTGRESQL_SELECT_SQL;
        } else {
            throw new RuntimeException("Unsupported database type!");
        }
        dbTool.selectList(sql, new RowMapper<Object>() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                assertNull(FieldGetter.getBoolean(rs, "xbool"));
                assertNull(FieldGetter.getInteger(rs, "xint"));
                assertNull(FieldGetter.getLong(rs, "xbigint"));
                assertNull(FieldGetter.getString(rs, "xstring"));
                assertNull(FieldGetter.getTimestamp(rs, "xts1"));
                assertNull(FieldGetter.getTimestamp(rs, "xts2"));
                assertNull(FieldGetter.getBytes(rs, "xblob"));
                assertNull(FieldGetter.getBigDecimal(rs, "xbigdecimal"));
                return null;
            }
        }, 0);
    }
}
