package ru.kwanza.dbtool.core.util;

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

import org.postgresql.jdbc2.AbstractJdbc2Statement;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import ru.kwanza.dbtool.core.DBTool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Michael Yeskov
 */
public abstract class AbstractBatchPreparedStatementCallable implements PreparedStatementCallback, PreparedStatementCreator {

    protected final String sql;
    DBTool.DBType dbType;

    public AbstractBatchPreparedStatementCallable(String sql, DBTool.DBType dbType) {
        this.sql = sql;
        this.dbType = dbType;
    }

    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        if (DBTool.DBType.POSTGRESQL.equals(dbType)) {
            (ps.unwrap(AbstractJdbc2Statement.class)).setContinueTrxOnErrors(true);
        }
        return ps;
    }
}
