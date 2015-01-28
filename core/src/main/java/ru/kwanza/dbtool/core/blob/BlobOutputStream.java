package ru.kwanza.dbtool.core.blob;

/*
 * #%L
 * dbtool-core
 * %%
 * Copyright (C) 2015 Kwanza
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
 * Утилита записи blob полей
 *
 * @author Ivan Baluk
 */
public abstract class BlobOutputStream extends OutputStream implements Closeable {
    private DBTool dbTool;
    private final String tableName;
    private final String fieldName;
    private final KeyValueCondition condition;
    protected final Connection connection;
    private long position;
    private long size;
    private ByteBuffer buffer = ByteBuffer.allocate(Const.BLOCK_SIZE);

    public BlobOutputStream(DBTool dbTool, String tableName, String fieldName, Collection<KeyValue<String, Object>> keyValues)
            throws IOException {
        this.dbTool = dbTool;
        this.connection = dbTool == null ? null : dbTool.getJDBCConnection();
        this.tableName = tableName;
        this.fieldName = fieldName;
        this.condition = new KeyValueCondition(keyValues);
    }

    public static BlobOutputStream create(DBTool dbTool, String tableName, String fieldName, Collection<KeyValue<String, Object>> keyValues)
            throws IOException {
        if (dbTool.getDbType().equals(DBTool.DBType.MSSQL)) {
            return new MSSQLBlobOutputStream(dbTool, tableName, fieldName, keyValues);
        } else if (dbTool.getDbType().equals(DBTool.DBType.ORACLE)) {
            return new OracleBlobOutputStream(dbTool, tableName, fieldName, keyValues);
        } else if (dbTool.getDbType().equals(DBTool.DBType.MYSQL)) {
            return new MySQLBlobOutputStream(dbTool, tableName, fieldName, keyValues);
        } else if (dbTool.getDbType().equals(DBTool.DBType.H2)) {
            return new H2BlobOutputStream(dbTool, tableName, fieldName, keyValues);
        } else {
            throw new RuntimeException("Unsupported type of database");
        }
    }

    protected DBTool getDbTool() {
        return dbTool;
    }

    /**
     * Размер поля в байтах
     */
    public long getSize() {
        return size;
    }

    protected void setUpSize(long size) throws IOException {
        this.size = size;
        this.position = size;
        if (size > 0) {
            setPosition(size);
        }
    }

    /**
     * Текущая позиция
     */
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
        if (!isEmpty()) {
            try {
                makeFlush();
            } catch (SQLException e) {
                throw new IOException(e);
            }
        }
    }

    private void makeFlush() throws SQLException {
        try {
            dbFlush(this.position - buffer.position() + 1, getArray());
        } finally {
            buffer.rewind();
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
        position++;

        if (position >= size) {
            size = position;
        }
        if (isFull()) {
            try {
                makeFlush();
            } catch (SQLException e) {
                throw new IOException(e);
            }
        }
    }

    private boolean isFull() {
        return buffer.position() >= Const.BLOCK_SIZE;
    }

    private boolean isEmpty() {
        return buffer.position() == 0;
    }

    protected abstract void dbFlush(long position, byte[] array) throws SQLException;

    protected abstract void dbReset() throws SQLException;

    public void reset() throws IOException {
        if (size != 0) {
            try {
                dbReset();
            } catch (SQLException e) {
                throw new IOException(e);
            }
            size = position = 0;
            buffer.position(0);
        }
    }

    protected String getTableName() {
        return tableName;
    }

    protected String getFieldName() {
        return fieldName;
    }

    public KeyValueCondition getCondition() {
        return condition;
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
