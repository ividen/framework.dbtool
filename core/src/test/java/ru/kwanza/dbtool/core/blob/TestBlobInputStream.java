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

import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.core.ConnectionConfigListener;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.KeyValue;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * @author: Ivan Baluk
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class TestBlobInputStream extends AbstractJUnit4SpringContextTests {

    @Resource(name = "dbtool.DBTool")
    private DBTool dbTool;
    private BlobInputStream blobIS;


    @Component
    public static class InitDB {
        @Resource(name = "dbTester")
        private IDatabaseTester dbTester;

        @Resource(name = "dbtool.DBTool")
        private DBTool dbTool;

        private IDataSet getDataSet() throws Exception {
            return
                    new FlatXmlDataSetBuilder().build(this.getClass().getResourceAsStream("../data/blob_input_stream_test.xml"));
        }

        @PostConstruct
        protected void init() throws Exception {
            dbTester.setDataSet(getDataSet());
            dbTester.setOperationListener(new ConnectionConfigListener(dbTool.getDbType()));
            dbTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
            dbTester.onSetup();
        }
    }

    @After
    public void tearDown() throws Exception {
        dbTool.closeResources(blobIS);
    }

    @Test
    public void testRead() throws Exception {
        blobIS = dbTool.getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        assertEquals("hello", inputStreamToString(blobIS));
    }

    @Test
    public void testReadFail() throws Exception {
        try {
            blobIS =
                    dbTool.getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 3)));
            fail("Expected " + IOException.class);
        } catch (IOException e) {
        }
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
