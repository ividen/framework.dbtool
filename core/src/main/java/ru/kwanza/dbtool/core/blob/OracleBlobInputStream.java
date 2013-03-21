package ru.kwanza.dbtool.core.blob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.KeyValue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Ivan Baluk
 */
class OracleBlobInputStream extends BlobInputStream {

    private static final Logger log = LoggerFactory.getLogger(OracleBlobInputStream.class);
    private final long size;

    private int position;

    public OracleBlobInputStream(final DBTool dbTool, String tableName, String fieldName, Collection<KeyValue<String, Object>> keyValues)
            throws IOException, StreamException.EmptyFieldException, StreamException.RecordNotFoundException {
        super(dbTool, tableName, fieldName, keyValues);

        final String whereCondition = getWhereCondition();
        final String nameSize = "nameSize";
        final String sqlQuerySize =
                "SELECT LENGTH(" + getFieldName() + ") AS " + nameSize + " FROM " + getTableName() + " WHERE " + whereCondition;
        final String sqlQuery = "SELECT " + getFieldName() + " FROM " + getTableName() + " WHERE " + whereCondition;
        try {
            resultSet = connection.prepareCall(sqlQuerySize).executeQuery();
            if (!resultSet.next()) {
                throw new StreamException.RecordNotFoundException(sqlQuerySize);
            }

            size = resultSet.getLong(nameSize);
            dbTool.closeResources(resultSet);
            if (size <= 0) {
                throw new StreamException.EmptyFieldException("No data. Size = " + size);
            }

            resultSet = connection.prepareCall(sqlQuery).executeQuery();
            if (!resultSet.next()) {
                throw new StreamException.RecordNotFoundException(sqlQuery);
            }

            inputStream = resultSet.getBinaryStream(getFieldName());
            if (inputStream == null) {
                throw new IOException("Stream is null");
            }
            position = 0;
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
        long result = inputStream.skip(n);
        if (result >= 0) {
            position += result;
        }
        return result;
    }

    @Override
    public long getPosition() {
        return position;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public int read() throws IOException {
        final int result = inputStream.read();
        if (result != -1) {
            position++;
        }
        return result;
    }

    @Override
    public int read(byte b[]) throws IOException {
        final int result = inputStream.read(b);
        if (result != -1) {
            position += result;
        }
        return result;
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {
        final int result = inputStream.read(b, off, len);
        if (result != -1) {
            position += result;
        }
        return result;
    }
}
