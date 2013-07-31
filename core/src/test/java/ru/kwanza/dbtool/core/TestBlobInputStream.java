package ru.kwanza.dbtool.core;

import org.dbunit.DBTestCase;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.kwanza.dbtool.core.blob.BlobInputStream;
import ru.kwanza.dbtool.core.blob.NullBlobInputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

/**
 * @author: Ivan Baluk
 */
public abstract class TestBlobInputStream extends DBTestCase {
    private ApplicationContext ctx;

    @Override
    protected IDataSet getDataSet() throws Exception {
        IDataSet tmpExpDataSet =
                new FlatXmlDataSetBuilder().build(this.getClass().getResourceAsStream("./data/blob_input_stream_test.xml"));
        ReplacementDataSet rds = new ReplacementDataSet(tmpExpDataSet);
        byte[] bytes = "hello".getBytes("UTF-8");
        rds.addReplacementObject("[blob1]", bytes);
        rds.addReplacementObject("[null]", null);
        return rds;
    }

    @Override
    protected void setUp() throws Exception {
        ctx = new ClassPathXmlApplicationContext(getSpringCfgFile(), TestSelectUtil.class);
        DatabaseOperation.CLEAN_INSERT.execute(getConnection(), getDataSet());
    }

    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        config.setProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);
    }

    protected abstract String getSpringCfgFile();

    public void testRead() throws Exception {
        BlobInputStream blobIS = getDBTool().getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        assertEquals("hello", inputStreamToString(blobIS));
    }

    public void testReadNull() throws Exception {
        BlobInputStream blobIS = getDBTool().getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 2)));
        assertTrue(blobIS instanceof NullBlobInputStream);
    }

    public void testReadFail() throws Exception {
        BlobInputStream blobIS = getDBTool().getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 3)));
        assertNull(blobIS);
    }

    public DBTool getDBTool() {
        return ctx.getBean(DBTool.class);
    }

    private String inputStreamToString(BlobInputStream is) throws IOException {
        final char[] buffer = new char[(int) is.getSize()];
        final StringBuilder out = new StringBuilder();

        final Reader in = new InputStreamReader(is, "UTF-8");
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0) {
                break;
            }
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}
