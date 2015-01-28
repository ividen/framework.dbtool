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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.*;
import ru.kwanza.dbtool.core.SqlCollectionParameterValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Guzanov Alexander
 */
public class SelectStatementCreator implements PreparedStatementCreator, ParameterDisposer {
    private String sql;
    private Object[] params;
    private int resultSetType;

    private static final Logger logger = LoggerFactory.getLogger(SelectStatementCreator.class);


    public SelectStatementCreator(String sql, Object[] params, int resultSetType) {
        this.sql = sql;
        this.params = params;
        this.resultSetType = resultSetType;
    }

    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        StringBuilder buffer = new StringBuilder(sql);
        int lastIndex = 0;
        for (int i = 0; i < params.length; i++) {
            Object o = params[i];
            int index = buffer.indexOf("?", lastIndex);
            lastIndex = index + 1;
            if (index < 0) {
                throw new SQLException("Wrong sql expressin. Could not find parameter mapping at index =" + i);
            }
            if (o instanceof Collection) {
                Collection c = (Collection) o;
                if (c.size() > 1) {
                    String questions = QuestionsHelper.getQuestions(c.size());
                    buffer.replace(index, index + 1, questions);
                    lastIndex += questions.length() - 1;
                }
            }

        }
        String sql = buffer.toString();
        if (logger.isTraceEnabled()) {
            logger.trace("Construct select statement : {}", sql);
        }
        PreparedStatement result = con.prepareStatement(sql, resultSetType, ResultSet.CONCUR_READ_ONLY);
        try {
            setValues(result);
        } catch (SQLException e) {
            result.close();
        }

        return result;
    }

    public void setValues(PreparedStatement ps) throws SQLException {
        int parameterPosition = 1;
        if (this.params != null) {
            for (Object arg : this.params) {
                if (arg instanceof Collection) {
                    Collection entries = (Collection) arg;
                    int nullableCount = 0;
                    int type = SqlTypeValue.TYPE_UNKNOWN;
                    if (entries instanceof SqlCollectionParameterValue) {
                        type = ((SqlCollectionParameterValue) entries).getSqlType();
                    }
                    for (Object entry : entries) {
                        StatementCreatorUtils.setParameterValue(ps, parameterPosition, type, entry);
                        parameterPosition++;
                        nullableCount++;
                    }

                    for (int k = nullableCount; k < QuestionsHelper.getCountOfQuestions(entries.size()); k++) {
                        StatementCreatorUtils.setParameterValue(ps, parameterPosition, type, null);
                        parameterPosition++;

                    }
                } else {
                    doSetValue(ps, parameterPosition, arg);
                    parameterPosition++;
                }
            }
        }
    }

    protected void doSetValue(PreparedStatement ps, int parameterPosition, Object argValue) throws SQLException {
        if (argValue instanceof SqlParameterValue) {
            SqlParameterValue paramValue = (SqlParameterValue) argValue;
            StatementCreatorUtils.setParameterValue(ps, parameterPosition, paramValue, paramValue.getValue());
        } else {
            StatementCreatorUtils.setParameterValue(ps, parameterPosition, SqlTypeValue.TYPE_UNKNOWN, argValue);
        }
    }

    public void cleanupParameters() {
        StatementCreatorUtils.cleanupParameters(this.params);
    }
}
