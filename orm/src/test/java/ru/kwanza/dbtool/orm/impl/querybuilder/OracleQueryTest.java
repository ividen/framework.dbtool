package ru.kwanza.dbtool.orm.impl.querybuilder;

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

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.sql.SQLException;

/**
 * @author Alexander Guzanov
 */
@ContextConfiguration(locations = "oracle-config.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class OracleQueryTest extends QueryTest {

    public IDatabaseConnection getConnection() throws SQLException, DatabaseUnitException {
        return new DatabaseConnection(dataSource.getConnection(), "DBTOOL_TEST");
    }
}
