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

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.UpdateSetter;

import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Guzanov Alexander
 */
public class BatchPreparedStatementCallable<T> extends AbstractBatchPreparedStatementCallable {
    private final UpdateSetter setter;
    private final SQLExceptionTranslator exeptionTranslator;
    private final Collection<T> objects;
    private ArrayList<T> constrained = new ArrayList<T>();
    private ArrayList<T> updated;
    private long skippedCount = 0;

    public BatchPreparedStatementCallable(String sql, final Collection<T> objects, UpdateSetter<T> setter,
                                          SQLExceptionTranslator exceptionTranslator, DBTool.DBType dbType) {
        super(sql, dbType);
        this.setter = setter;
        this.exeptionTranslator = exceptionTranslator;
        this.objects = objects;
        this.updated = new ArrayList<T>(objects.size());
    }

    public Object doInPreparedStatement0(PreparedStatement ps, Iterator iterator) throws SQLException, DataAccessException {
        Skip skip = null;
        Skip currSkip = null;
        int totalCount = 0;
        try {
            int i = 0;
            while (iterator.hasNext()) {
                if (setter.setValues(ps, iterator.next())) {
                    i++;
                    ps.addBatch();
                } else {
                    if (skip == null) {
                        currSkip = skip = new Skip(i, 1);
                    } else {
                        if (currSkip.index == i) {
                            currSkip.count++;
                        } else {
                            Skip newSkip = new Skip(i, 1);
                            currSkip.next = newSkip;
                            currSkip = newSkip;
                        }
                    }
                }
                totalCount++;
            }

            if (i > 0) {
                return ResultCodeUtil.processResult(ps.executeBatch(), skip, totalCount);
            } else {
                return ResultCodeUtil.processResult(null, skip, totalCount);
            }

        } catch (BatchUpdateException e) {
            UpdateUtil.logger.error("Error batching! May have constraines!", e);
            return ResultCodeUtil.processResult(e.getUpdateCounts(), skip, totalCount);
        }
    }

    public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
        int from = 0;
        int size = objects.size();
        Iterator<T> currentItr = objects.iterator();
        while (from < size) {
            int[] updateCodes = (int[]) doInPreparedStatement0(ps, currentItr);
            Iterator<T> baseItr = objects.iterator();
            int i = 0;
            while (baseItr.hasNext()) {
                T obj = baseItr.next();
                if (i >= from + updateCodes.length) {
                    constrained.add(obj);
                    break;
                } else if (i >= from) {
                    int code = updateCodes[i - from];
                    if (code == Statement.EXECUTE_FAILED) {
                        constrained.add(obj);
                    } else if (code == Skip.SKIPPED) {
                        skippedCount++;
                    } else {
                        updated.add(obj);
                    }
                }
                i++;
            }

            from += updateCodes.length + 1;
            currentItr = baseItr;
        }
        return updated;
    }

    public ArrayList<T> getConstrained() {
        return constrained;
    }

    public ArrayList<T> getUpdated() {
        return updated;
    }

    public long getSkippedCount() {
        return skippedCount;
    }
}
