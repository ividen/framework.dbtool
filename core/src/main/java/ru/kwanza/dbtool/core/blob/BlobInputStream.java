package ru.kwanza.dbtool.core.blob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.KeyValue;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Ivan Baluk
 */
public abstract class BlobInputStream extends InputStream implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(BlobInputStream.class);

    private DBTool dbTool;

    private final String tableName;
    private final String fieldName;
    private final KeyValueCondition condition;

    protected final Connection connection;
    protected ResultSet resultSet;
    protected InputStream inputStream;

    protected BlobInputStream(DBTool dbTool, String tableName, String fieldName, Collection<KeyValue<String, Object>> keyValues)
            throws IOException {
        this.dbTool = dbTool;
        try {
            this.connection = dbTool == null ? null : dbTool.getDataSource().getConnection();
        } catch (SQLException e) {
            throw new IOException(e);
        }
        this.fieldName = fieldName;
        this.tableName = tableName;
        this.condition = new KeyValueCondition(keyValues);

        this.resultSet = null;
        this.inputStream = null;
    }

    public static BlobInputStream create(DBTool dbTool, String tableName, String fieldName, Collection<KeyValue<String, Object>> keyValues)
            throws IOException {
        try {
            if (dbTool.getDbType().equals(DBTool.DBType.MSSQL)) {
                return new MSSQLBlobInputStream(dbTool, tableName, fieldName, keyValues);
            } else if (dbTool.getDbType().equals(DBTool.DBType.ORACLE)) {
                return new OracleBlobInputStream(dbTool, tableName, fieldName, keyValues);
            } else {
                throw new RuntimeException("Unsupported type of database");
            }
        } catch (StreamException.RecordNotFoundException e) {
            log.error(e.getMessage(), e);
            return null;
        } catch (StreamException.EmptyFieldException e) {
            log.info(e.getMessage(), e);
            return new NullBlobInputStream(tableName, fieldName, keyValues);
        }
    }

    protected DBTool getDbTool() {
        return dbTool;
    }

    protected String getTableName() {
        return tableName;
    }

    protected String getFieldName() {
        return fieldName;
    }

    protected String getWhereCondition() {
        return condition.getStringCondition();
    }

    public abstract long getPosition();

    public abstract long getSize();

    public int available() throws IOException {
        return (int) (getSize() - getPosition());
    }

    @Override
    public void close() throws IOException {
        try {
            dbTool.closeResources(inputStream, resultSet, connection);
        } finally {
            try {
                super.close();
            } catch (IOException e) {
                log.error("Error closing " + this.toString(), e);
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append("(").append(tableName).append(".").append(fieldName).append(" where ");
        sb.append(getWhereCondition());
        sb.append(')');
        return sb.toString();
    }
}
