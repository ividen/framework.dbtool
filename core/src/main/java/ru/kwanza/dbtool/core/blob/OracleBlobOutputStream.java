package ru.kwanza.dbtool.core.blob;

import oracle.jdbc.driver.OracleConnection;
import oracle.sql.BLOB;
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
    private BLOB blobField;
    private ResultSet rs;

    public OracleBlobOutputStream(final DBTool dbTool, String tableName, String fieldName, Collection<KeyValue<String, Object>> keyValues)
            throws IOException, StreamException.RecordNotFoundException {
        super(dbTool, tableName, fieldName, keyValues);

        PreparedStatement ps;
        try {

            ps = connection
                    .prepareStatement("SELECT " + fieldName + ", LENGTH(" + fieldName + ") FROM " + tableName + " WHERE "
                            + getCondition().getWhereClause() + " FOR UPDATE",
                            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = getCondition().installParams(ps).executeQuery();
            if (rs.next()) {
                blobField = (BLOB) rs.getBlob(1);

                if (blobField == null) {
                    dbReset();
                } else {
                    setUpSize(rs.getLong(2));
                }

            } else {
                throw new StreamException.RecordNotFoundException("Record not found!");
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    protected void dbFlush(long position, byte[] buffer) throws SQLException {
        blobField.setBytes(position, buffer);
    }

    @Override
    protected void dbReset() throws SQLException {
        if (blobField != null && blobField.isOpen()) {
            blobField.close();
        }

        blobField = BLOB.createTemporary(connection.isWrapperFor(Connection.class) ?
                connection.unwrap(OracleConnection.class) : connection, true, BLOB.DURATION_SESSION);
        String whereCondition = getCondition().getWhereClause();
        final String sqlQueryClear = "UPDATE " + getTableName() + " SET " + getFieldName() + "=null WHERE " + whereCondition;

        final int count = getCondition().installParams(connection.prepareStatement(sqlQueryClear)).executeUpdate();

        if (count != 1) {
            throw new SQLException("Record not found!");
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
