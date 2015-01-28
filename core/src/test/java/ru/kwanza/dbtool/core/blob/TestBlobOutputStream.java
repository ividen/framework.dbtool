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

    @Test
    public void testReadAndWrite() throws Exception {
        BlobOutputStream blobOS;

        blobOS = dbTool.getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 2)));
        for (int i = 0; i < 5000; i++) {

            blobOS.write("hello".getBytes());

        }
        blobOS.close();
        BlobInputStream blob =
                dbTool.getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 2)));
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
            blobOS = dbTool.getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 2)));
            blobOS.write(new byte[1000]);
            blobOS.close();
        }

        BlobInputStream blob =
                dbTool.getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 2)));
        long size = blob.getSize();
        blob.close();

        assertEquals(size, 10000);

    }

    @Test
    public void testWrite_2() throws Exception {
        BlobOutputStream blobOS;

        blobOS = dbTool.getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 2)));
        blobOS.write("hello".getBytes());
        blobOS.close();

        BlobInputStream blob =
                dbTool.getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 2)));
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

        blobOS = dbTool.getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 2)));
        blobOS.write("hello".getBytes());
        blobOS.close();

        blobOS = dbTool.getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 2)));
        blobOS.setPosition(4);
        blobOS.write("OOOO".getBytes());
        blobOS.close();

        BlobInputStream blob =
                dbTool.getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 2)));
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

        blobOS = dbTool.getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 2)));
        blobOS.write("hello".getBytes());
        blobOS.close();

        BlobInputStream blob =
                dbTool.getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 2)));
        long size = blob.getSize();
        byte[] b = new byte[5];
        blob.read(b);
        blob.close();

        assertEquals(size, 5);
        assertEquals(new String(b), "hello");

        blobOS = dbTool.getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 2)));
        blobOS.reset();
        blobOS.close();

        blob =
                dbTool.getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 2)));
        size = blob.getSize();
        blob.close();

        assertEquals(size, 0);
    }
    
    @Test
    public void testWrite_5() throws Exception {
        BlobOutputStream blobOS;

        blobOS = dbTool.getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 2)));
        blobOS.write("hello".getBytes());
        blobOS.reset();
        blobOS.close();

        BlobInputStream blob =
                dbTool.getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 2)));
        long size = blob.getSize();
        blob.close();

        assertEquals(size, 0);

    }
}
