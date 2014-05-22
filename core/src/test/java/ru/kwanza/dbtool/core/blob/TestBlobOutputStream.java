package ru.kwanza.dbtool.core.blob;

import junit.framework.Assert;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import ru.kwanza.dbtool.core.ConnectionConfigListener;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.KeyValue;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

/**
 * @author: Ivan Baluk
 */

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class TestBlobOutputStream extends AbstractTransactionalJUnit4SpringContextTests {

    @Resource(name = "dbtool.DBTool")
    private DBTool dbTool;

    @Component
    public static class InitDB {
        @Resource(name = "dbTester")
        private IDatabaseTester dbTester;

        private IDataSet getDataSet() throws Exception {
            IDataSet tmpExpDataSet =
                    new FlatXmlDataSetBuilder().build(this.getClass().getResourceAsStream("../data/blob_output_stream_test_init.xml"));
            ReplacementDataSet rds = new ReplacementDataSet(tmpExpDataSet);
            byte[] bytes = "hello".getBytes("UTF-8");
            rds.addReplacementObject("[blob1]", bytes);
            rds.addReplacementObject("[null]", null);
            return rds;
        }

        @PostConstruct
        protected void init() throws Exception {
            dbTester.setDataSet(getDataSet());
            dbTester.setOperationListener(new ConnectionConfigListener());
            dbTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
            dbTester.onSetup();
        }

    }

    @Test
    public void testReadAndWrite() throws Exception {
        BlobOutputStream blobOS;

        blobOS = dbTool.getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        for (int i = 0; i < 5000; i++) {

            blobOS.write("hello".getBytes());

        }
        blobOS.close();
        BlobInputStream blob =
                dbTool.getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        long size = blob.getSize();

        for (int i = 0; i < 5000; i++) {
            byte[] bytes = new byte[5];
            blob.read(bytes);
            assertEquals(new String(bytes), "hello");

        }

        blob.close();

        assertEquals(size, 5000 * "hello".getBytes().length);

    }

    @Test
    public void testWrite_1() throws Exception {
        BlobOutputStream blobOS;

        for (int i = 0; i < 10; i++) {
            blobOS = dbTool.getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
            blobOS.write(new byte[1000]);
            blobOS.close();
        }

        BlobInputStream blob =
                dbTool.getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        long size = blob.getSize();
        blob.close();

        assertEquals(size, 10000);

    }

    @Test
    public void testWrite_2() throws Exception {
        BlobOutputStream blobOS;

        blobOS = dbTool.getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        blobOS.write("hello".getBytes());
        blobOS.close();

        BlobInputStream blob =
                dbTool.getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        long size = blob.getSize();
        byte[] b = new byte[5];
        blob.read(b);
        blob.close();

        assertEquals(size, 5);
        assertEquals(new String(b), "hello");
    }
    @Test
    public void testWrite_3() throws Exception {
        BlobOutputStream blobOS;

        blobOS = dbTool.getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        blobOS.write("hello".getBytes());
        blobOS.close();

        blobOS = dbTool.getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        blobOS.setPosition(4);
        blobOS.write("OOOO".getBytes());
        blobOS.close();

        BlobInputStream blob =
                dbTool.getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        long size = blob.getSize();
        byte[] b = new byte[8];
        blob.read(b);
        blob.close();

        assertEquals(size, 8);
        assertEquals(new String(b), "hellOOOO");
    }
    
    @Test
    public void testWrite_4() throws Exception {
        BlobOutputStream blobOS;

        blobOS = dbTool.getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        blobOS.write("hello".getBytes());
        blobOS.close();

        BlobInputStream blob =
                dbTool.getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        long size = blob.getSize();
        byte[] b = new byte[5];
        blob.read(b);
        blob.close();

        assertEquals(size, 5);
        assertEquals(new String(b), "hello");

        blobOS = dbTool.getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        blobOS.reset();
        blobOS.close();

        blob =
                dbTool.getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        size = blob.getSize();
        blob.close();

        assertEquals(size, 0);
    }
    
    @Test
    public void testWrite_5() throws Exception {
        BlobOutputStream blobOS;

        blobOS = dbTool.getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        blobOS.write("hello".getBytes());
        blobOS.reset();
        blobOS.close();

        BlobInputStream blob =
                dbTool.getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        long size = blob.getSize();
        blob.close();

        assertEquals(size, 0);

    }
}
