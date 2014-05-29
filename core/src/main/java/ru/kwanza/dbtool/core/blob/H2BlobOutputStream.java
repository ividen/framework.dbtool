package ru.kwanza.dbtool.core.blob;

import org.h2.jdbc.JdbcBlob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.KeyValue;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Ivan Baluk
 */
class H2BlobOutputStream extends BlobOutputStream {
    private static final Logger log = LoggerFactory.getLogger(H2BlobOutputStream.class);
    private JdbcBlob blob;
    private String sqlUpdate;


    H2BlobOutputStream(DBTool dbTool, String tableName, String fieldName,
                       Collection<KeyValue<String, Object>> keyValues) throws IOException {
        super(dbTool, tableName, fieldName, keyValues);
        final String whereCondition = getCondition().getWhereClause();
        final String sqlQuerySize =
                "SELECT " + getFieldName() + " FROM " + getTableName() + " WHERE " + whereCondition;
        this.sqlUpdate = "UPDATE " + getTableName() + " SET " + getFieldName() + "=? WHERE " + whereCondition;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {

            pst = connection.prepareStatement(sqlQuerySize);
            rs = getCondition().installParams(pst).executeQuery();

            if (!rs.next()) {
                throw new SQLException("Record not found!");
            }


            this.blob = (JdbcBlob) rs.getBlob(1);
            if (blob == null) {
                this.blob = (JdbcBlob) dbTool.getJDBCConnection().createBlob();
            }
            setUpSize(blob.length());
        } catch (IOException e) {
            close();
            log.error(e.getMessage(), e);
            throw e;
        } catch (SQLException e) {
            close();
            log.error(e.getMessage(), e);
            throw new IOException(e);
        } finally {
            dbTool.closeResources(rs, pst);
        }
    }

    @Override
    protected void dbFlush(long position, byte[] buffer) throws SQLException {
        if (blob != null) {
            byte[] bytes = blob.getBytes(1, (int) position);
            byte[] result = new byte[(int) position + buffer.length - 1];
            System.arraycopy(bytes, 0, result, 0, bytes.length);
            System.arraycopy(buffer, 0, result, (int) position - 1, buffer.length);
            blob.setBytes(1, result);
            updateBlob();
        }
    }

    private void updateBlob() throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sqlUpdate);
            ps.setBlob(1, blob);
            getCondition().installParams(2, ps);
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Can't update blob field!");
            }
        } finally {
            getDbTool().closeResources(ps);
        }
    }

    @Override
    protected void dbReset() throws SQLException {
        if (blob != null) {
            blob.setBytes(1, new byte[0]);
            updateBlob();
        }

    }

    @Override
    public void close() throws IOException {
        flush();
        if (blob != null) {
            blob.free();
            blob = null;
        }
        super.close();
    }
}

