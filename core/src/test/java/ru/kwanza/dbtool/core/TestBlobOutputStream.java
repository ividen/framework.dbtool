package ru.kwanza.dbtool.core;

import org.dbunit.Assertion;
import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
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

    public void testWrite() throws Exception {
        BlobOutputStream blobOS =
                getDBTool().getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)),false);
        blobOS.write(new byte[1024*1024]);
        blobOS.close();


        blobOS =
                getDBTool().getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)),true);
        blobOS.write(new byte[1024*1024]);
        blobOS.close();
//        IDataSet tempDataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResourceAsStream("./data/blob_output_stream_test.xml"));
//        ReplacementDataSet expDataSet = new ReplacementDataSet(tempDataSet);
//        expDataSet.addReplacementObject("[DATA]", "hello".getBytes("UTF-8"));
//
//        IDataSet actDataSet = getConnection().createDataSet(new String[]{"test_blob"});
//        Assertion.assertEquals(expDataSet.getTable("test_blob"), actDataSet.getTable("test_blob"));
    }

    public DBTool getDBTool() {
        return ctx.getBean(DBTool.class);
    }
}
