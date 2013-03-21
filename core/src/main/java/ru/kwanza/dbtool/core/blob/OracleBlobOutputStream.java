package ru.kwanza.dbtool.core.blob;

import oracle.jdbc.driver.OracleConnection;
import oracle.sql.BLOB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.KeyValue;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Ivan Baluk
 */
class OracleBlobOutputStream extends BlobOutputStream {

    private static final Logger log = LoggerFactory.getLogger(OracleBlobOutputStream.class);

    private BLOB tempBlob;
    private OutputStream tempOutputStream;

    public OracleBlobOutputStream(final DBTool dbTool, String tableName, String fieldName, Collection<KeyValue<String, Object>> keyValues)
            throws IOException, StreamException.RecordNotFoundException {
        super(dbTool, tableName, fieldName, keyValues);

        final String whereCondition = getWhereCondition();
        final String sqlQueryClear = "UPDATE " + tableName + " SET " + fieldName + "=null WHERE " + whereCondition;
        try {
            final int count = connection.prepareStatement(sqlQueryClear).executeUpdate();

            if (count != 1) {
                throw new StreamException.RecordNotFoundException("Message with " + whereCondition + " not updated [" + count + "]");
            }


            tempBlob = BLOB.createTemporary(connection.isWrapperFor(Connection.class) ?
                    connection.unwrap(OracleConnection.class) : connection, true, BLOB.DURATION_SESSION);
            tempOutputStream = tempBlob.setBinaryStream(1);

            if (tempOutputStream == null) {
                throw new IOException("Stream is null");
            }
        } catch (IOException e) {
            try {
                close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            throw e;
        } catch (SQLException e0) {
            try {
                close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException(e0);
        }
    }

    public void write(int b) throws IOException {
        tempOutputStream.write(b);
    }

    @Override
    public void write(final byte b[], final int off, final int len) throws IOException {
        tempOutputStream.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        tempOutputStream.flush();
    }

    @Override
    public void close() throws IOException {
        IOException exception = null;
        try {
            if (tempOutputStream != null) {
                flush();
            }
        } catch (IOException e) {
            log.error("Error flushing " + this.toString(), e);
            //noinspection ConstantConditions
            if (exception == null) {
                exception = e;
            }
        }
        getDbTool().closeResources(tempOutputStream);

        if (exception == null && tempBlob != null) {
            String whereCondition = getWhereCondition();
            final String sqlQuery = "UPDATE " + getTableName() + " SET " + getFieldName() + "=? WHERE " + whereCondition;
            try {
                final Object[] params = new Object[]{tempBlob};
                int count;
                try {
                    count = executeUpdate(sqlQuery, params);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                if (count != 1) {
                    throw new IOException("Message with " + whereCondition + " not found");
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                exception = e;
            }
        }

        if (tempBlob != null) {
            try {
                if (tempBlob.isOpen()) {
                    tempBlob.close();
                }
            } catch (SQLException e) {
                log.error("Error closing temporary blob", e);
                if (exception == null) {
                    exception = new IOException("Error closing temporary blob", e);
                }
            } finally {
                tempBlob = null;
            }
        }
        try {
            super.close();
        } catch (IOException e) {
            log.error("Error closing " + this.toString(), e);
            if (exception == null) {
                exception = e;
            }
        }

        getDbTool().closeResources(connection);
        if (exception != null) {
            throw exception;
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
}
