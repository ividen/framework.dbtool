package ru.kwanza.dbtool.core.blob;

import oracle.jdbc.driver.OracleConnection;
import oracle.sql.BLOB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.KeyValue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Ivan Baluk
 */
class OracleBlobOutputStream extends BlobOutputStream {

    private static final Logger log = LoggerFactory.getLogger(OracleBlobOutputStream.class);

    private BLOB blobField;
    private ResultSet rs;

    public OracleBlobOutputStream(final DBTool dbTool, String tableName, String fieldName, Collection<KeyValue<String, Object>> keyValues)
            throws IOException, StreamException.RecordNotFoundException {
        super(dbTool, tableName, fieldName, keyValues);

        final String whereCondition = getWhereCondition();

        PreparedStatement ps;
        try {

            ps = connection
                    .prepareStatement("SELECT " + fieldName + ", LENGTH(" + fieldName + ") FROM " + tableName + " WHERE " + whereCondition
                            + " FOR UPDATE",
                            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = ps.executeQuery();
            if (rs.next()) {
                blobField = (BLOB) rs.getBlob(1);

                if (blobField == null) {
                    resetToDB();
                } else {
                    setSize(rs.getLong(2));
                }

                if (getSize() > 0) {
                    setPosition(getSize() - 1);
                }

            } else {
                throw new StreamException.RecordNotFoundException("Record not found");
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    protected void flushToDB(long position, byte[] buffer) throws SQLException {
        blobField.setBytes(position+1,buffer);
    }

    @Override
    protected void resetToDB() throws SQLException {
        if (blobField != null) {
            blobField.close();
        }

        blobField = BLOB.createTemporary(connection.isWrapperFor(Connection.class) ?
                connection.unwrap(OracleConnection.class) : connection, true, BLOB.DURATION_SESSION);
        String whereCondition = getWhereCondition();
        final String sqlQueryClear = "UPDATE " + getTableName() + " SET " + getFieldName() + "=null WHERE " + whereCondition;

        final int count = connection.prepareStatement(sqlQueryClear).executeUpdate();

        if (count != 1) {
            throw new SQLException(
                    "Table  " + getTableName() + " with condition " + whereCondition + " not updated [" + count + "]");
        }

    }

    @Override
    public void close() throws IOException {
        flush();
        try {
            rs.updateBlob(1, blobField);
            rs.updateRow();

            if (blobField.isOpen()) {
                blobField.close();
            }
        } catch (SQLException e) {
            throw new IOException("Error closing temporary blob", e);
        }

        super.close();
    }
}
