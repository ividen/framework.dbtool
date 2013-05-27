package ru.kwanza.dbtool.core.blob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.KeyValue;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Ivan Baluk
 */
public abstract class BlobOutputStream extends OutputStream implements Closeable {
    public static final int BLOCK_SIZE = 1024 * 1024;
    private static final Logger log = LoggerFactory.getLogger(BlobOutputStream.class);

    private DBTool dbTool;

    private final String tableName;
    private final String fieldName;
    private final KeyValueCondition condition;
    protected final Connection connection;
    private long position;
    private long size;
    private ByteBuffer buffer = ByteBuffer.allocate(BLOCK_SIZE);

    public BlobOutputStream(DBTool dbTool, String tableName, String fieldName, Collection<KeyValue<String, Object>> keyValues)
            throws IOException {
        this.dbTool = dbTool;
        this.connection = dbTool == null ? null : dbTool.getJDBCConnection();
        this.tableName = tableName;
        this.fieldName = fieldName;
        this.condition = new KeyValueCondition(keyValues);
        this.position = 0l;
    }

    public static BlobOutputStream create(DBTool dbTool, String tableName, String fieldName, Collection<KeyValue<String, Object>> keyValues,
                                          boolean append)
            throws IOException {
        try {
            if (dbTool.getDbType().equals(DBTool.DBType.MSSQL)) {
                return new MSSQLBlobOutputStream(dbTool, tableName, fieldName, keyValues, append);
            } else if (dbTool.getDbType().equals(DBTool.DBType.ORACLE)) {
                return new OracleBlobOutputStream(dbTool, tableName, fieldName, keyValues);
            } else {
                throw new RuntimeException("Unsupported type of database");
            }
        } catch (StreamException.RecordNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new IOException(e);
        }
    }

    protected DBTool getDbTool() {
        return dbTool;
    }

    public long getSize() {
        return size;
    }

    protected void setSize(long size) {
        this.size = size;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) throws IOException {
        if (position < 0) {
            throw new IllegalStateException("Possition can't be negative!");
        }
        if (position > getSize() + 1) {
            throw new IllegalStateException("Position must be less then size!");
        }

        flush();

        this.position = position;
    }

    @Override
    public void flush() throws IOException {
        if (buffer.position() > 0) {
            try {
                flushToDB(this.position - buffer.position() + 1, getArray());
            } catch (SQLException e) {
                throw new IOException(e);
            } finally {
                buffer.rewind();
            }
        }
    }

    private byte[] getArray() {
        int i = buffer.position();
        buffer.rewind();
        byte[] result = new byte[i];
        buffer.get(result);

        return result;
    }

    @Override
    public void write(int b) throws IOException {
        buffer.put((byte) b);
        if (size == 0) {
            size++;
        } else {
            position++;
        }

        if (position >= size) {
            size = position + 1;
        }
        if (buffer.position() >= BLOCK_SIZE) {
            try {
                flushToDB(position - BLOCK_SIZE, getArray());
            } catch (SQLException e) {
                throw new IOException(e);
            }finally {
                buffer.rewind();
            }
        }
    }

    protected abstract void flushToDB(long position, byte[] array) throws SQLException;

    protected abstract void resetToDB() throws SQLException;

    public void reset() throws IOException {
        try {
            resetToDB();
        } catch (SQLException e) {
            throw new IOException(e);
        }
        size = position = 0;
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

    @Override
    public void close() throws IOException {
        dbTool.closeResources(connection);
        super.close();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append("(").append(tableName).append(".").append(fieldName).append(" where ");
        sb.append(getWhereCondition());
        sb.append(')');
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(Integer.MAX_VALUE);
    }
}
