package ru.kwanza.dbtool.blob;

import java.io.ByteArrayOutputStream;

/**
 * @author Ivan Baluk
 */
class ByteArrayOutputStreamExt extends ByteArrayOutputStream {

    ByteArrayOutputStreamExt(final int maxBufferSize) {
        super(getInitialSize(maxBufferSize));
    }

    private static int getInitialSize(int maxBufferSize) {
        int result = maxBufferSize;
        while ((result & 3) == 0 && result > 0) {
            result >>= 2;
        }
        while ((result & 1) == 0 && result > 0) {
            result >>= 1;
        }
        if (result == 0) {
            result = 4;
        }
        return result;
    }

    @Override
    public synchronized byte toByteArray()[] {
        if (count == buf.length) {
            return buf;
        } else {
            return super.toByteArray();
        }
    }
}
