package ru.kwanza.dbtool.core.blob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.KeyValue;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import static ru.kwanza.dbtool.core.blob.Const.BLOCK_SIZE;

/**
 * @author Ivan Baluk
 */
public abstract class BlobInputStream extends InputStream implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(BlobInputStream.class);

    private DBTool dbTool;

    private final String tableName;
    private final String fieldName;
    private final KeyValueCondition condition;
    //todo aguzanov подумать над тем, чтобы избавиться от этого.
    protected final Connection connection;
    private long position;
    private long size;
    private ByteBuffer buffer = ByteBuffer.allocate(BLOCK_SIZE);

    protected BlobInputStream(DBTool dbTool, String tableName, String fieldName, Collection<KeyValue<String, Object>> keyValues)
            throws IOException {
        this.dbTool = dbTool;
        this.connection = dbTool == null ? null : dbTool.getJDBCConnection();
        this.fieldName = fieldName;
        this.tableName = tableName;
        this.condition = new KeyValueCondition(keyValues);
        this.position = 0;
        this.buffer.position(BLOCK_SIZE);
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

    protected KeyValueCondition getCondition() {
        return condition;
    }

    public long getSize() {
        return size;
    }

    protected void setUpSize(long size) throws IOException {
        this.size = size;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) throws IOException {
        if (position < 0) {
            throw new IllegalStateException("Possition can't be negative!");
        }
        if (position > getSize()) {
            throw new IllegalStateException("Position must be less then size!");
        }
        buffer.position(BLOCK_SIZE - 1);
        this.position = position;
    }

    @Override
    public int read() throws IOException {
        if (position == size) {
            position++;
            return -1;
        }

        if (position > size) {
            throw new EOFException();
        }

        if (buffer.position() < BLOCK_SIZE) {
            position++;
            return buffer.get();
        } else {
            buffer.rewind();
            try {
                buffer.put(dbRead(position, BLOCK_SIZE));
            } catch (SQLException e) {
                throw new IOException(e);
            }
            buffer.rewind();
            return read();
        }
    }

    public abstract byte[] dbRead(long position, int blockSize) throws SQLException;

    public int available() throws IOException {
        return (int) (getSize() - getPosition());
    }

    @Override
    public void close() throws IOException {
        try {
            dbTool.closeResources(connection);
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
        sb.append(getCondition().getWhereClause());
        sb.append(')');
        return sb.toString();
    }
}
