package ru.kwanza.dbtool.blob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.DBTool;
import ru.kwanza.dbtool.KeyValue;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Ivan Baluk
 */
class MSSQLBlobInputStream extends BlobInputStream {

    protected static final int BLOCK_SIZE = 1000000;

    private static final Logger log = LoggerFactory.getLogger(MSSQLBlobInputStream.class);

    private final long size;

    private int position;
    private int readCount;

    private final String sqlRead;

    public MSSQLBlobInputStream(final DBTool dbTool, String tableName, String fieldName, Collection<KeyValue<String, Object>> keyValues)
            throws RecordNotFoundException, IOException {
        super(dbTool, tableName, fieldName, keyValues);

        final String whereCondition = getWhereCondition();
        final String nameSize = "nameSize";
        final String sqlQuerySize =
                "SELECT DATALENGTH(" + getFieldName() + ") AS " + nameSize + " FROM " + getTableName() + " WHERE " + whereCondition;
        sqlRead = "DECLARE @ptrval VARBINARY(16)\n" + "SELECT @ptrval = TEXTPTR(" + getFieldName() + ") FROM " + getTableName() + " WHERE "
                + whereCondition + "\n" + "READTEXT " + getTableName() + "." + getFieldName() + " @ptrval ? ?";
        try {
            resultSet = dbTool.getDataSource().getConnection().prepareStatement(sqlQuerySize).executeQuery();
            if (!resultSet.next()) {
                throw new RecordNotFoundException(sqlQuerySize);
            }

            size = resultSet.getLong(nameSize);
            if (size <= 0) {
                throw new RecordNotFoundException("No data. Size = " + size);
            }

            position = 0;
            readCount = 0;
            readNextBlock();
        } catch (IOException e) {
            close();
            log.error(e.getMessage(), e);
            throw e;
        } catch (SQLException e) {
            close();
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public long skip(long n) throws IOException {
        if (n <= 0) {
            return 0;
        }

        final long originalPosition = readCount;
        long newPosition = originalPosition + n;

        if (newPosition > size) {
            newPosition = size;
        }

        readCount = (int) newPosition;
        position = (int) newPosition;

        try {
            readNextBlock();
        } catch (RecordNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new IOException(e);
        }
        return newPosition - originalPosition;
    }

    @Override
    public long getPosition() {
        return readCount;
    }

    @Override
    public long getSize() {
        return size;
    }

    private boolean hasMore() {
        return position < size;
    }

    private void readNextBlock() throws IOException, RecordNotFoundException {
        if (!hasMore()) {
            return;
        }
        try {
            getDbTool().closeResources(inputStream, resultSet);
            final int currBlockSize = position + BLOCK_SIZE < size ? BLOCK_SIZE : (int) (size - position);

            int[] params = new int[]{position, currBlockSize};

            resultSet = executeQuery(sqlRead, params);
            position += currBlockSize;
            if (!resultSet.next()) {
                throw new RecordNotFoundException("Message not found");
            }
            inputStream = resultSet.getBinaryStream(getFieldName());
            if (inputStream == null) {
                throw new IOException("Stream is null");
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private ResultSet executeQuery(String sql, int[] params) throws SQLException {
        PreparedStatement pst = getDbTool().getDataSource().getConnection().prepareStatement(sql);
        int index = 0;
        for (int param : params) {
            pst.setInt(++index, param);
        }
        return pst.executeQuery();
    }

    @Override
    public int read() throws IOException {
        int result = inputStream.read();
        if (result == -1 && hasMore()) {
            try {
                readNextBlock();
            } catch (RecordNotFoundException e) {
                log.error(e.getMessage(), e);
                throw new IOException(e);
            }
            result = inputStream.read();
        }
        if (result != -1) {
            readCount++;
        }
        return result;
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        int result = 0;
        int offset = off;
        int length = len;
        do {
            final int current = inputStream.read(b, offset, length);
            if (current != -1) {
                readCount += current;
                result += current;
                offset += current;
                length -= current;
            } else {
                if (hasMore()) {
                    try {
                        readNextBlock();
                    } catch (RecordNotFoundException e) {
                        log.error(e.getMessage(), e);
                        throw new IOException(e);
                    }
                } else {
                    break;
                }
            }
        } while (length > 0);
        if (result == 0 && len > 0) {
            result = -1;
        }
        return result;
    }
}
