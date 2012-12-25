package ru.kwanza.dbtool.core.blob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.KeyValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Ivan Baluk
 */
class MSSQLBlobOutputStream extends BlobOutputStream {

    protected static final int BLOCK_SIZE = 1000000;

    private static final Logger log = LoggerFactory.getLogger(MSSQLBlobOutputStream.class);

    private int position;

    private final String sqlQuery;

    private ByteArrayOutputStream outputStreamCache;

    public MSSQLBlobOutputStream(final DBTool dbTool, String tableName, String fieldName, Collection<KeyValue<String, Object>> keyValues)
            throws IOException, StreamException.RecordNotFoundException {
        super(dbTool, tableName, fieldName, keyValues);

        final String whereCondition = getWhereCondition();
        final String sqlQueryClear = "UPDATE " + tableName + " SET " + fieldName + " = null WHERE " + whereCondition;
        this.sqlQuery =
                "DECLARE @ptrval VARBINARY(16)\n" + "SELECT @ptrval = TEXTPTR(" + getFieldName() + ") FROM " + getTableName() + " WHERE "
                        + whereCondition + "\n" + "UPDATETEXT " + getTableName() + "." + getFieldName() + " @ptrval ? null ?";
        try {
            final int count = connection.prepareStatement(sqlQueryClear).executeUpdate();
            if (count != 1) {
                throw new StreamException.RecordNotFoundException("Record with " + whereCondition + " not updated [" + count + "]");
            }
        } catch (SQLException e) {
            close();
            throw new RuntimeException(e);
        }

        position = 0;
        outputStreamCache = new ByteArrayOutputStreamExt(BLOCK_SIZE);
    }

    public void write(int b) throws IOException {
        outputStreamCache.write(b);
        if (outputStreamCache.size() >= BLOCK_SIZE) {
            flush();
        }
    }

    @Override
    public void write(final byte b[], final int off, final int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }

        int offset = off;
        int length = len;

        if (outputStreamCache.size() >= BLOCK_SIZE) {
            flush();
        }

        while (length > 0) {
            final int currMaxBlockSize = BLOCK_SIZE - outputStreamCache.size();
            final int currBlockSize = currMaxBlockSize < length ? currMaxBlockSize : length;
            outputStreamCache.write(b, offset, currBlockSize);
            offset += currBlockSize;
            length -= currBlockSize;
            if (outputStreamCache.size() >= BLOCK_SIZE) {
                flush();
            }
        }

        assert offset == off + len;
        assert length == 0;
    }

    @Override
    public void flush() throws IOException {
        outputStreamCache.flush();
        try {
            writeNextBlock();
        } catch (StreamException.RecordNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new IOException(e);
        }
    }

    private int writenBytes = 0;

    private void writeNextBlock() throws IOException, StreamException.RecordNotFoundException {
        if (outputStreamCache.size() == 0) {
            return;
        }

        try {
            final byte[] bytes = outputStreamCache.toByteArray();
            Object[] params = new Object[]{position, bytes};
            final int count = executeUpdate(sqlQuery, params);
            writenBytes += bytes.length;
            position += bytes.length;
            outputStreamCache.reset();
            if (count != 1) {
                checkUpdate(); //MSSQL всегда возвращает ноль, дополнительная проверка.
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (OutOfMemoryError e) {
            log.error("Please use ObjectOutputStream.reset(). Writen bytes: " + writenBytes);
            throw e;
        }
    }

    private int executeUpdate(String sql, Object[] params) throws SQLException {
        PreparedStatement pst = connection.prepareStatement(sql);
        int index = 0;
        for (Object param : params) {
            pst.setObject(++index, param);
        }
        return pst.executeUpdate();
    }

    private void checkUpdate() throws IOException, StreamException.RecordNotFoundException {
        final String nameSize = "nameSize";
        final String whereCondition = getWhereCondition();
        final String sqlQuerySize = "SELECT DATALENGTH(" + getFieldName() +
                ") AS " + nameSize +
                " FROM " + getTableName() +
                " WHERE " + whereCondition;
        try {
            ResultSet resultSet = null;
            try {
                resultSet =connection.prepareStatement(sqlQuerySize).executeQuery();
                if (!resultSet.next()) {
                    throw new StreamException.RecordNotFoundException(sqlQuerySize);
                }
                final int size = resultSet.getInt(nameSize);
                if (writenBytes != size) {
                    throw new IOException(
                            "Expected: " + writenBytes + ", Actual: " + size + ". Table: " + getTableName() + ", where: " + whereCondition);
                }
            } finally {
                getDbTool().closeResources(resultSet);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private ResultSet executeQuery(String sql, Object[] params) throws SQLException {
        PreparedStatement pst =connection.prepareStatement(sql);
        int index = 0;
        for (Object param : params) {
            pst.setObject(++index, param);
        }
        return pst.executeQuery();
    }

    @Override
    public void close() throws IOException {
        IOException exception = null;
        try {
            if (outputStreamCache != null) {
                flush();
            }
        } catch (IOException e) {
            log.error("Error flushing " + this.toString(), e);
            //noinspection ConstantConditions
            if (exception == null) {
                exception = e;
            }
        }
        try {
            if (outputStreamCache != null) {
                outputStreamCache.close();
            }
        } catch (IOException e) {
            log.error("Error closing " + this.toString(), e);
            if (exception == null) {
                exception = e;
            }
        } finally {
            outputStreamCache = null;
        }
        try {
            super.close();
        } catch (IOException e) {
            log.error("Error closing " + this.toString(), e);
            if (exception == null) {
                exception = e;
            }
        }
        if (exception != null) {
            throw exception;
        }
    }
}