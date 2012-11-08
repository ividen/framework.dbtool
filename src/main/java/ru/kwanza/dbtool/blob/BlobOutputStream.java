









































package ru.kwanza.dbtool.blob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.DBTool;
import ru.kwanza.dbtool.KeyValue;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

/**
 * @author Ivan Baluk
 */
public abstract class BlobOutputStream extends OutputStream implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(BlobOutputStream.class);

    private DBTool dbTool;

    private final String tableName;
    private final String fieldName;
    private final KeyValueCondition condition;

    public BlobOutputStream(DBTool dbTool, String tableName, String fieldName, Collection<KeyValue<String, Object>> keyValues) {
        this.dbTool = dbTool;
        this.tableName = tableName;
        this.fieldName = fieldName;
        this.condition = new KeyValueCondition(keyValues);
    }

    public static BlobOutputStream create(DBTool dbTool, String tableName, String fieldName, Collection<KeyValue<String, Object>> keyValues)
            throws IOException {
        try {
            if (dbTool.getDbType().equals(DBTool.DBType.MSSQL)) {
                return new MSSQLBlobOutputStream(dbTool, tableName, fieldName, keyValues);
            } else if (dbTool.getDbType().equals(DBTool.DBType.ORACLE)) {
                return new OracleBlobOutputStream(dbTool, tableName, fieldName, keyValues);
            } else {
                throw new RuntimeException("Unsupported type of database");
            }
        } catch (StreamException.RecordNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new IOException(e);
        }
    }

    protected DBTool getDbTool() {
        return dbTool;
    }

    protected String getTableName() {
        return tableName;
    }

    protected String getFieldName() {
        return fieldName;
    }

    protected String getWhereCondition() {
        return condition.getStringCondition();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append("(").append(tableName).append(".").append(fieldName).append(" where ");
        sb.append(getWhereCondition());
        sb.append(')');
        return sb.toString();
    }
}
