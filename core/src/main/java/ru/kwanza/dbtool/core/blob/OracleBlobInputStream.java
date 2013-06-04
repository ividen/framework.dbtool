package ru.kwanza.dbtool.core.blob;

import oracle.sql.BLOB;
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
class OracleBlobInputStream extends BlobInputStream {

    private static final Logger log = LoggerFactory.getLogger(OracleBlobInputStream.class);
    private final BLOB blobField;

    public OracleBlobInputStream(final DBTool dbTool, String tableName, String fieldName, Collection<KeyValue<String, Object>> keyValues)
            throws IOException {
        super(dbTool, tableName, fieldName, keyValues);

        final String whereCondition = getCondition().getWhereClause();
        final String nameSize = "nameSize";
        final String sqlQuerySize =
                "SELECT LENGTH(" + getFieldName() + ") AS " + nameSize + " FROM " + getTableName() + " WHERE " + whereCondition;
        final String sqlQuery = "SELECT " + getFieldName() + " FROM " + getTableName() + " WHERE " + whereCondition;

        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            pst = connection.prepareStatement(sqlQuerySize);
            rs = getCondition().installParams(pst).executeQuery();

            if (!rs.next()) {
                throw new SQLException("Record not found!");
            }

            setUpSize(rs.getLong(nameSize));

            rs = getCondition().installParams(connection.prepareStatement(sqlQuery)).executeQuery();
            if (!rs.next()) {
                throw new SQLException("Record not found!");
            }

            this.blobField = (BLOB) rs.getBlob(getFieldName());
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
    public byte[] dbRead(long position, int blockSize) throws SQLException {
        return blobField.getBytes(position + 1, blockSize);
    }

}
