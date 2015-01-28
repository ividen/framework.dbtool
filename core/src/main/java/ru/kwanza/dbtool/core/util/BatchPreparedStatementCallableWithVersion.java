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
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.UpdateSetterWithVersion;
import ru.kwanza.toolbox.fieldhelper.FieldHelper;

import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * @author Guzanov Alexander
 */
public class BatchPreparedStatementCallableWithVersion<T, K, V> extends AbstractBatchPreparedStatementCallable {
    private UpdateSetterWithVersion setter;
    private FieldHelper.VersionField versionField;
    private FieldHelper.Field<T, K> keyField;
    private Collection<T> objects;

    private ArrayList<T> constrained = new ArrayList<T>();
    private ArrayList<T> optimistic = new ArrayList<T>();
    private ArrayList<T> updated;
    private List<T> checkList = new ArrayList<T>();
    private long result = 0;
    private long skippedCount = 0;
    private Map<Object, VersionPair> versions = new HashMap<Object, VersionPair>();

    public BatchPreparedStatementCallableWithVersion(String sql, final Collection<T> objects, UpdateSetterWithVersion<T, V> setter,
                                                     FieldHelper.Field<T, K> keyField, FieldHelper.VersionField<T, V> versionField,
                                                     DBTool.DBType dbType) {
        super(sql, dbType);
        this.objects = objects;
        this.updated = new ArrayList<T>(objects.size());
        this.setter = setter;
        this.versionField = versionField;
        this.keyField = keyField;
    }

    public Object doInPreparedStatement0(PreparedStatement ps, Iterator<T> iterator) throws SQLException, DataAccessException {
        Skip skip = null;
        Skip currSkip = null;
        int totalCount = 0;
        try {
            int i = 0;
            while (iterator.hasNext()) {
                T next = iterator.next();
                VersionPair pair = getVersionPair(next);
                if (setter.setValues(ps, next, pair.newValue, pair.oldValue)) {
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

    private VersionPair getVersionPair(T next) {
        Object key = keyField.value(next);
        VersionPair pair = versions.get(key);
        if (pair == null) {
            pair = new VersionPair();
            pair.oldValue = versionField.value(next);
            pair.newValue = versionField.generateNewValue(next);
            versions.put(key, pair);
        }
        return pair;
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
                    } else if (code == Statement.SUCCESS_NO_INFO) {
                        checkList.add(obj);
                    } else if (code == 0) {
                        optimistic.add(obj);
                    } else {
                        K key = keyField.value(obj);
                        V currVersion = (V) versions.get(key).newValue;
                        versionField.setValue(obj, currVersion);
                        result += code;
                        updated.add(obj);
                    }
                }
                i++;
            }

            from += updateCodes.length + 1;
            currentItr = baseItr;
        }

        return result;
    }


    public long getResult() {
        return result;
    }

    public long getSkippedCount() {
        return skippedCount;
    }

    public ArrayList<T> getConstrained() {
        return constrained;
    }

    public ArrayList<T> getOptimistic() {
        return optimistic;
    }

    public List<T> getCheckList() {
        return checkList;
    }

    public ArrayList<T> getUpdated() {
        return updated;
    }

    public Map<Object, VersionPair> getVersions() {
        return versions;
    }

    public static class VersionPair {
        Object newValue;
        Object oldValue;
    }
}

