package ru.kwanza.dbtool.core;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.kwanza.dbtool.core.blob.BlobInputStream;
import ru.kwanza.dbtool.core.blob.BlobOutputStream;

import java.util.Arrays;

/**
 * @author: Ivan Baluk
 */
public abstract class TestBlobOutputStream extends DBTestCase {
    private ApplicationContext ctx;

    @Override
    protected IDataSet getDataSet() throws Exception {
        IDataSet tmpExpDataSet =
                new FlatXmlDataSetBuilder().build(this.getClass().getResourceAsStream("./data/blob_output_stream_test_init.xml"));
        ReplacementDataSet rds = new ReplacementDataSet(tmpExpDataSet);
        rds.addReplacementObject("[null]", null);
        return rds;
    }

    @Override
    protected void setUp() throws Exception {
        ctx = new ClassPathXmlApplicationContext(getSpringCfgFile(), TestSelectUtil.class);
        DatabaseOperation.CLEAN_INSERT.execute(getConnection(), getDataSet());
    }

    protected abstract String getSpringCfgFile();

    public void testWrite_1() throws Exception {
        BlobOutputStream blobOS;

       for (int i = 0; i < 10; i++) {
            blobOS = getDBTool().getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
            blobOS.write(new byte[1000]);
            blobOS.close();
        }

        BlobInputStream blob =
                getDBTool().getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        long size = blob.getSize();
        blob.close();

        assertEquals(size, 10000);

    }

    public void testWrite_2() throws Exception {
        BlobOutputStream blobOS;

        blobOS = getDBTool().getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        blobOS.write("hello".getBytes());
        blobOS.close();

        BlobInputStream blob =
                getDBTool().getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        long size = blob.getSize();
        byte[] b = new byte[5];
        blob.read(b);
        blob.close();

        assertEquals(size, 5);
        assertEquals(new String(b), "hello");
    }

    public void testWrite_3() throws Exception {
        BlobOutputStream blobOS;

        blobOS = getDBTool().getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        blobOS.write("hello".getBytes());
        blobOS.close();

        blobOS = getDBTool().getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        blobOS.setPosition(4);
        blobOS.write("OOOO".getBytes());
        blobOS.close();

        BlobInputStream blob =
                getDBTool().getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        long size = blob.getSize();
        byte[] b = new byte[8];
        blob.read(b);
        blob.close();

        assertEquals(size, 8);
        assertEquals(new String(b), "hellOOOO");
    }

    public void testWrite_4() throws Exception {
        BlobOutputStream blobOS;

        blobOS = getDBTool().getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        blobOS.write("hello".getBytes());
        blobOS.close();

        BlobInputStream blob =
                getDBTool().getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        long size = blob.getSize();
        byte[] b = new byte[5];
        blob.read(b);
        blob.close();

        assertEquals(size, 5);
        assertEquals(new String(b), "hello");

        blobOS = getDBTool().getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        blobOS.reset();
        blobOS.close();

        blob =
                getDBTool().getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        size = blob.getSize();
        blob.close();

        assertEquals(size, 0);
    }

    public void testWrite_5() throws Exception {
        BlobOutputStream blobOS;

        blobOS = getDBTool().getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        blobOS.write("hello".getBytes());
        blobOS.reset();
        blobOS.close();

        BlobInputStream blob =
                getDBTool().getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        long size = blob.getSize();
        blob.close();

        assertEquals(size, 0);

    }

    public DBTool getDBTool() {
        return ctx.getBean(DBTool.class);
    }
}
