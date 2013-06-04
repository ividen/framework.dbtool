package ru.kwanza.dbtool.core.blob;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.KeyValue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
class MySQLBlobOutputStream extends BlobOutputStream {
    MySQLBlobOutputStream(DBTool dbTool, String tableName, String fieldName,
                          Collection<KeyValue<String, Object>> keyValues) throws IOException {
        super(dbTool, tableName, fieldName, keyValues);
    }

    @Override
    protected void dbFlush(long position, byte[] array) throws SQLException {
    }

    @Override
    protected void dbReset() throws SQLException {
    }
}
