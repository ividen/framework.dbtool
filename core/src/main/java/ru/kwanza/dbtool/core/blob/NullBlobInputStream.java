package ru.kwanza.dbtool.core.blob;

import ru.kwanza.dbtool.core.KeyValue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Ivan Baluk
 */
public final class NullBlobInputStream extends BlobInputStream {
    NullBlobInputStream(String tableName, String fieldName, Collection<KeyValue<String, Object>> keyValues) throws IOException {
        super(null, tableName, fieldName, keyValues);
        setUpSize(0);
    }

    @Override
    public byte[] dbRead(long position, int blockSize) throws SQLException {
        return new byte[0];
    }
}
