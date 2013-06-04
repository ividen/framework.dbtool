package ru.kwanza.dbtool.core.blob;

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
class MSSQLBlobInputStream extends BlobInputStream {
    private static final Logger log = LoggerFactory.getLogger(MSSQLBlobInputStream.class);
    private final String sqlRead;

    public MSSQLBlobInputStream(final DBTool dbTool, String tableName, String fieldName, Collection<KeyValue<String, Object>> keyValues)
            throws IOException, StreamException.EmptyFieldException, StreamException.RecordNotFoundException {
        super(dbTool, tableName, fieldName, keyValues);

        final String whereCondition = getCondition().getWhereClause();
        final String nameSize = "nameSize";
        final String sqlQuerySize =
                "SELECT DATALENGTH(" + getFieldName() + ") AS " + nameSize + " FROM " + getTableName() + " WHERE " + whereCondition;
        this.sqlRead =
                "DECLARE @ptrval VARBINARY(16)\n" + "SELECT @ptrval = TEXTPTR(" + getFieldName() + ") FROM " + getTableName() + " WHERE "
                        + whereCondition + "\n" + "READTEXT " + getTableName() + "." + getFieldName() + " @ptrval ? ?";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {

            pst = connection.prepareStatement(sqlQuerySize);
            rs = getCondition().installParams(pst).executeQuery();
            if (!rs.next()) {
                throw new SQLException("Record not found!");
            }

            setUpSize(rs.getLong(nameSize));
        } catch (IOException e) {
            close();
            log.error(e.getMessage(), e);
            throw e;
        } catch (SQLException e) {
            close();
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            dbTool.closeResources(rs,pst);
        }
    }

    @Override
    public byte[] dbRead(long position, int blockSize) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            pst = getCondition().installParams(connection.prepareStatement(this.sqlRead));
            int index = getCondition().getParamsCount();
            pst.setInt(++index, (int) position);
            pst.setInt(++index, Math.min(blockSize,(int)(getSize()-position)));
            rs = pst.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Record not found");
            }
            return rs.getBytes(getFieldName());
        } finally {
            getDbTool().closeResources(rs,pst);
        }

    }
}
