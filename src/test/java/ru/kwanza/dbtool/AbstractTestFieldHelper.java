package ru.kwanza.dbtool;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.RowMapper;
import ru.kwanza.dbtool.DBTool;
import ru.kwanza.dbtool.FieldGetter;
import ru.kwanza.dbtool.FieldSetter;
import ru.kwanza.dbtool.UpdateSetter;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * @author Ivan Baluk
 */
public abstract class AbstractTestFieldHelper extends TestCase {

    private static String DELETE_SQL = "delete from test_table1";
    private static String INSERT_SQL = "insert into test_table1 (bool, int, bigint, string, ts1, ts2, blob, bigdecimal) " +
            "values (?, ?, ?, ?, ?, ?, ?, ?)";
    private static String ORACLE_SELECT_SQL = "select bool, int, bigint, string, ts1, ts2, blob, bigdecimal from test_table1 where rownum > ?";
    private static String MSSQL_SELECT_SQL = "select top 10 bool, int, bigint, string, ts1, ts2, blob, bigdecimal from test_table1 where not(int = ?) ";
    private static DBTool dbTool = null;

    @Override
    public void setUp() throws Exception {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(getContextFileName(), this.getClass());
        dbTool = ctx.getBean(DBTool.class);
        dbTool.getDataSource().getConnection().prepareStatement(DELETE_SQL).execute();
    }

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
        dbTool.selectList(dbTool.getDbType() == DBTool.DBType.ORACLE ? ORACLE_SELECT_SQL : MSSQL_SELECT_SQL,
                new RowMapper<Object>() {
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Assert.assertEquals((Boolean) true, FieldGetter.getBoolean(rs, "bool"));
                        assertEquals((Integer) 2, FieldGetter.getInteger(rs, "int"));
                        assertEquals((Long) 3l, FieldGetter.getLong(rs, "bigint"));
                        assertEquals("4", FieldGetter.getString(rs, "string"));
                        assertEquals(new Date(1000l).getTime(), FieldGetter.getTimestamp(rs, "ts1").getTime());
                        assertEquals(new Date(1000l).getTime(), FieldGetter.getTimestamp(rs, "ts2").getTime());
                        assertEquals("blob", new String(FieldGetter.getBytes(rs, "blob")));
                        assertEquals(new BigDecimal(8), FieldGetter.getBigDecimal(rs, "bigdecimal"));
                        return null;
                    }
                }, 0);
    }

    public void testSettersNull() throws Exception {
        dbTool.update(INSERT_SQL, new ArrayList<Object>(Arrays.asList(1, 2, 3, 4, 5, 6)), new UpdateSetter<Object>() {
            public boolean setValues(PreparedStatement pst, Object object) throws SQLException {
                FieldSetter.setBoolean(pst, 1, null);
                FieldSetter.setInt(pst, 2, null);
                FieldSetter.setLong(pst, 3, null);
                FieldSetter.setString(pst, 4, null);
                FieldSetter.setTimestamp(pst, 5, (Timestamp) null);
                FieldSetter.setTimestamp(pst, 6, (Date) null);
                FieldSetter.setBlob(pst, 7, null);
                FieldSetter.setBigDecimal(pst, 8, null);
                return true;
            }
        });
        dbTool.selectList(dbTool.getDbType() == DBTool.DBType.ORACLE ? ORACLE_SELECT_SQL : MSSQL_SELECT_SQL,
                new RowMapper<Object>() {
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        assertNull(FieldGetter.getBoolean(rs, "bool"));
                        assertNull(FieldGetter.getInteger(rs, "int"));
                        assertNull(FieldGetter.getLong(rs, "bigint"));
                        assertNull(FieldGetter.getString(rs, "string"));
                        assertNull(FieldGetter.getTimestamp(rs, "ts1"));
                        assertNull(FieldGetter.getTimestamp(rs, "ts2"));
                        assertNull(FieldGetter.getBytes(rs, "blob"));
                        assertNull(FieldGetter.getBigDecimal(rs, "bigdecimal"));
                        return null;
                    }
                }, 0);
    }

    protected abstract String getContextFileName();
}
