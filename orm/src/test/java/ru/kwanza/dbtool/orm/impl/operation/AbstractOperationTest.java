package ru.kwanza.dbtool.orm.impl.operation;

/*
 * #%L
 * dbtool-orm
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
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.SortedDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import ru.kwanza.dbtool.core.ConnectionConfigListener;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingRegistry;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author Kiryl Karatsetski
 */


@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TransactionConfiguration(defaultRollback = true)
public abstract class AbstractOperationTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Resource(name = "dbtool.DBTool")
    protected DBTool dbTool;


    @Resource(name = "dbtool.IEntityManager")
    protected IEntityManager em;


    @Component
    public static class InitDB {
        @Resource(name = "dbTester")
        private IDatabaseTester dbTester;
        @Resource(name = "dbtool.IEntityMappingRegistry")
        private EntityMappingRegistry registry;

        private IDataSet getDataSet() throws Exception {
            return new FlatXmlDataSetBuilder().build(this.getClass().getResourceAsStream("./data/data_set.xml"));
        }

        @PostConstruct
        protected void init() throws Exception {
            registry.registerEntityClass(TestEntity.class);
            registry.registerEntityClass(TestEntityVersion.class);
            dbTester.setDataSet(getDataSet());
            dbTester.setOperationListener(new ConnectionConfigListener());
            dbTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
            dbTester.onSetup();
        }
    }

    protected IDataSet getResourceSet(String fileName) throws DataSetException {
        return new SortedDataSet(new FlatXmlDataSetBuilder().build(this.getClass().getResourceAsStream(fileName)));
    }

    protected IDataSet getActualDataSet() throws Exception {
        return new SortedDataSet(new DatabaseConnection(dbTool.getJDBCConnection()).createDataSet(new String[]{"test_table"}));
    }

}

