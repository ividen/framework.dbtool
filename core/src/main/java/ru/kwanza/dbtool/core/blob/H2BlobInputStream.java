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
 * @author Alexander Guzanov
 */
class H2BlobInputStream extends BlobInputStream {
    private static final Logger log = LoggerFactory.getLogger(H2BlobInputStream.class);
    private JdbcBlob blob;


    H2BlobInputStream(DBTool dbTool, String tableName, String fieldName,
                      Collection<KeyValue<String, Object>> keyValues) throws IOException {
        super(dbTool, tableName, fieldName, keyValues);
        final String whereCondition = getCondition().getWhereClause();
        final String sqlQuerySize =
                "SELECT " + getFieldName() + " FROM " + getTableName() + " WHERE " + whereCondition;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {

            pst = connection.prepareStatement(sqlQuerySize);
            rs = getCondition().installParams(pst).executeQuery();

            if (!rs.next()) {
                throw new SQLException("Record not found!");
            }


            this.blob = (JdbcBlob) rs.getBlob(1);
            setUpSize(blob == null ? 0 : blob.length());
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
    protected byte[] dbRead(long position, int blockSize) throws SQLException {
        return blob.getBytes(position, blockSize);
    }

    @Override
    public void close() throws IOException {
        if (blob != null) {
            blob.free();
            blob = null;
        }
        super.close();
    }
}

