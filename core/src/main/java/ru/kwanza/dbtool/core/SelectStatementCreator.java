package ru.kwanza.dbtool.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Guzanov Alexander
 */
class SelectStatementCreator implements PreparedStatementCreator, ParameterDisposer {
    private String sql;
    private Object[] params;

    private static final Logger logger = LoggerFactory.getLogger(SelectStatementCreator.class);

    public SelectStatementCreator(String sql, Object[] params) {
        this.sql = sql;
        this.params = params;
    }

    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        StringBuffer buffer = new StringBuffer(sql);
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
        PreparedStatement result = con.prepareStatement(sql);
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
                    for (Object entry : entries) {
                        doSetValue(ps, parameterPosition, entry);
                        parameterPosition++;
                        nullableCount++;
                    }

                    for (int k = nullableCount; k < QuestionsHelper.getCountOfQuestions(entries.size()); k++) {
                        doSetValue(ps, parameterPosition, null);
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
