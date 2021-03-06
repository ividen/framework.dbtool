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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.FieldSetter;
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

    private static final Logger log = LoggerFactory.getLogger(MSSQLBlobOutputStream.class);
    private final String sqlUpdateTextQuery;
    private final String sqlUpdateQuery;

    private ByteArrayOutputStream outputStreamCache;

    public MSSQLBlobOutputStream(final DBTool dbTool, String tableName, String fieldName, Collection<KeyValue<String, Object>> keyValues)
            throws IOException {
        super(dbTool, tableName, fieldName, keyValues);

        final String whereCondition = getCondition().getWhereClause();

        this.sqlUpdateQuery = "UPDATE " + tableName + " SET " + fieldName + " = ? WHERE " + whereCondition;
        this.sqlUpdateTextQuery =
                "DECLARE @ptrval VARBINARY(16);\n" + "SELECT @ptrval = TEXTPTR(" + getFieldName() + ") FROM " + getTableName()
                        + " WHERE "
                        + whereCondition + ";\n" + "UPDATETEXT " + getTableName() + "." + getFieldName() + " @ptrval ? null ?";

        try {
            long size = selectSize();
            if (size == 0) {
                updateField(new byte[0]);
            }
            setUpSize(size);
        } catch (SQLException e) {
            close();
            throw new IOException(e);
        }

    }

    @Override
    protected void dbFlush(long position, byte[] array) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = getCondition().installParams(connection.prepareStatement(sqlUpdateTextQuery));
            pst.setInt(getCondition().getParamsCount() + 1, (int) position - 1);
            pst.setBytes(getCondition().getParamsCount() + 2, array);
            pst.executeUpdate();
        } finally {
            getDbTool().closeResources(pst);
        }
    }

    @Override
    protected void dbReset() throws SQLException {
        updateField(null);
    }

    private void updateField(byte[] value) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = getCondition().installParams(2, connection.prepareStatement(sqlUpdateQuery));
            FieldSetter.setValue(pst, 1, byte[].class, value);
            if (pst.executeUpdate() != 1) {
                throw new SQLException("Can't clear record!");
            }
        } finally {
            getDbTool().closeResources(pst);
        }
    }

    private long selectSize() throws SQLException {
        final String nameSize = "nameSize";
        final String whereCondition = getCondition().getWhereClause();
        final String sqlQuerySize = "SELECT DATALENGTH(" + getFieldName() + ") AS " + nameSize + "  FROM " + getTableName() +
                " WHERE " + whereCondition;

        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            pst = getCondition().installParams(connection.prepareStatement(sqlQuerySize));
            rs = pst.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Record not found! Can't select size!");
            }
            final int size = rs.getInt(nameSize);
            return size;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new SQLException(e);
        } finally {
            getDbTool().closeResources(rs, pst);
        }
    }

    @Override
    public void close() throws IOException {
        //todo aguzanov порефакторить это должно вызфываться только у родительского метода
        flush();
        super.close();
    }
}