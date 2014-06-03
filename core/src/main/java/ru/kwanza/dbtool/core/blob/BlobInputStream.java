package ru.kwanza.dbtool.core.blob;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.KeyValue;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import static ru.kwanza.dbtool.core.blob.Const.BLOCK_SIZE;

/**
 * Утилита для чтения данных из blob-полей
 *
 * @author Ivan Baluk
 * @see ru.kwanza.dbtool.core.DBTool#getBlobInputStream(String, String, java.util.Collection)
 */
public abstract class BlobInputStream extends InputStream implements Closeable {
    private DBTool dbTool;

    private final String tableName;
    private final String fieldName;
    private final KeyValueCondition condition;
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
        if (dbTool.getDbType().equals(DBTool.DBType.MSSQL)) {
            return new MSSQLBlobInputStream(dbTool, tableName, fieldName, keyValues);
        } else if (dbTool.getDbType().equals(DBTool.DBType.ORACLE)) {
            return new OracleBlobInputStream(dbTool, tableName, fieldName, keyValues);
        } else if (dbTool.getDbType().equals(DBTool.DBType.MYSQL)) {
            return new MySQLBlobInputStream(dbTool, tableName, fieldName, keyValues);
        } else if (dbTool.getDbType().equals(DBTool.DBType.H2)) {
            return new H2BlobInputStream(dbTool, tableName, fieldName, keyValues);
        } else {
            throw new RuntimeException("Unsupported type of database");
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

    /**
     * Размер поля в байтах
     */
    public long getSize() {
        return size;
    }

    protected void setUpSize(long size) throws IOException {
        this.size = size;
    }

    /**
     * Текущая позиция.
     */
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
            return -1;
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

    protected abstract byte[] dbRead(long position, int blockSize) throws SQLException;

    public int available() throws IOException {
        return (int) (getSize() - getPosition());
    }

    @Override
    public void close() throws IOException {
        dbTool.closeResources(connection);
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
