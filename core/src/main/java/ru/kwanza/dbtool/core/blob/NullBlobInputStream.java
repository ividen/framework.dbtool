package ru.kwanza.dbtool.core.blob;

import ru.kwanza.dbtool.core.KeyValue;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Ivan Baluk
 */
public final class NullBlobInputStream extends BlobInputStream {

    private boolean open;

    NullBlobInputStream(String tableName, String fieldName, Collection<KeyValue<String, Object>> keyValues) throws IOException {
        super(null, tableName, fieldName, keyValues);
        this.open = true;
    }

    @Override
    public long getPosition() {
        return 0;
    }

    @Override
    public long getSize() {
        return 0L;
    }

    @Override
    public int read() throws IOException {
        ensureOpen();
        return -1;
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {
        ensureOpen();
        return super.read(b, off, len);
    }

    private void ensureOpen() throws IOException {
        if (!open) {
            throw new IOException("Illegal state: stream is closed.");
        }
    }

    @Override
    public void close() throws IOException {
        open = false;
        super.close();
    }
}
