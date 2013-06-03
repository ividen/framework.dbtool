package ru.kwanza.dbtool.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import ru.kwanza.dbtool.core.blob.BlobInputStream;
import ru.kwanza.dbtool.core.blob.BlobOutputStream;
import ru.kwanza.dbtool.core.lock.AppLock;
import ru.kwanza.dbtool.core.util.SelectUtil;
import ru.kwanza.dbtool.core.util.UpdateUtil;
import ru.kwanza.toolbox.fieldhelper.FieldHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Guzanov Alexander
 */
public class DBTool extends JdbcDaoSupport {

    private static final Logger logger = LoggerFactory.getLogger(DBTool.class);
    private DBType dbType;
    private int dbVersion;

    @Override
    protected void initDao() throws Exception {
        try {
            Connection conn = getJDBCConnection();
            String databaseProductName = conn.getMetaData().getDatabaseProductName();
            if ("Microsoft SQL Server".equalsIgnoreCase(databaseProductName)) {
                dbType = DBType.MSSQL;
            } else if ("Oracle".equalsIgnoreCase(databaseProductName)) {
                dbType = DBType.ORACLE;
            } else if("MySQL".equalsIgnoreCase(databaseProductName)){
                dbType = DBType.MYSQL;
            }else {
                dbType = DBType.OTHER;
            }
            dbVersion = conn.getMetaData().getDatabaseMajorVersion();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error in taking the type and version of database", e);
        }
    }

    public Connection getJDBCConnection(){
        return new ConnectionWrapper(getDataSource());
    }

    public <T> List<T> selectList(String selectSQL, RowMapper<T> rowMapper, Object... inValues) {
        return SelectUtil.selectList(getJdbcTemplate(), selectSQL, rowMapper, inValues);
    }

    public <T> List<T> selectList(String selectSQL, Class<T> cls, Object... inValues) {
        return SelectUtil.selectList(getJdbcTemplate(), selectSQL, cls, inValues);
    }

    public <T> Set<T> selectSet(String selectSQL, RowMapper<T> rowMapper, Object... inValues) {
        return SelectUtil.selectSet(getJdbcTemplate(), selectSQL, rowMapper, inValues);
    }

    public <T> Set<T> selectSet(String selectSQL, Class<T> cls, Object... inValues) {
        return SelectUtil.selectSet(getJdbcTemplate(), selectSQL, cls, inValues);
    }

    public <K, V> Map<K, List<V>> selectMapList(String selectSQL, RowMapper<KeyValue<K, V>> rowMapper, Object... inValues) {
        return SelectUtil.selectMapList(getJdbcTemplate(), selectSQL, rowMapper, inValues);
    }

    public <K0, K, V> Map<K0, Map<K, V>> selectMapOfMaps(String selectSQL, RowMapper<KeyValue<K0, KeyValue<K, V>>> rowMapper,
                                                         Object... inValues) {
        return SelectUtil.selectMapOfMaps(getJdbcTemplate(), selectSQL, rowMapper, inValues);
    }

    public <K, V> Map<K, V> selectMap(String selectSQL, RowMapper<KeyValue<K, V>> rowMapper, Object... inValues) {
        return SelectUtil.selectMap(getJdbcTemplate(), selectSQL, rowMapper, inValues);
    }

    // supress optimistic lock checking
    public <T> long update(String updateSQL, final Collection<T> objects, final UpdateSetter<T> updateSetter) throws UpdateException {
        return UpdateUtil.batchUpdate(getJdbcTemplate(), updateSQL, objects, updateSetter);
    }

    public <T, K extends Comparable, V> long update(String updateSQL, Collection<T> objects, UpdateSetterWithVersion<T, V> updateSetter,
                                                    String checkSQL, RowMapper<KeyValue<K, V>> keyVersionMapper,
                                                    FieldHelper.Field<T, K> keyField, FieldHelper.VersionField<T, V> versionField)
            throws UpdateException {
        return UpdateUtil
                .batchUpdate(getJdbcTemplate(), updateSQL, objects, updateSetter, checkSQL, keyVersionMapper, keyField, versionField);
    }

    public AppLock getLock(String lockName) {
        try {
            return AppLock.defineLock(this, lockName, dbType, dbVersion);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public AppLock getDefaultLock(String lockName) {
        try {
            return AppLock.defineLock(this, lockName, DBType.OTHER, dbVersion);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public BlobInputStream getBlobInputStream(String tableName, String fieldName, Collection<KeyValue<String, Object>> conditions)
            throws IOException {
        return BlobInputStream.create(this, tableName, fieldName, conditions);
    }

    public BlobOutputStream getBlobOutputStream(String tableName, String fieldName, Collection<KeyValue<String, Object>> conditions)
            throws IOException {
        return BlobOutputStream.create(this, tableName, fieldName, conditions);
    }

    public DBType getDbType() {
        return dbType;
    }

    public int getDbVersion() {
        return dbVersion;
    }

    public static enum DBType {
        MSSQL, ORACLE, MYSQL, OTHER
    }

    public void closeResources(Object... objects) {
        for (Object o : objects) {
            try {
                if (o != null) {
                    if (o instanceof Connection) {
                        ((Connection) o).close();
                    } else if (o instanceof ResultSet) {
                        ((ResultSet) o).close();
                    } else if (o instanceof Statement) {
                        ((Statement) o).close();
                    } else if (o instanceof AppLock) {
                        ((AppLock) o).close();
                    } else if (o instanceof InputStream) {
                        ((InputStream) o).close();
                    } else if (o instanceof OutputStream) {
                        ((OutputStream) o).close();
                    }
                }
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
