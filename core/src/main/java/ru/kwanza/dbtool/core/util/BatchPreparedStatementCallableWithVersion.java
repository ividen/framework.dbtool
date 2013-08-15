package ru.kwanza.dbtool.core.util;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.UpdateSetterWithVersion;
import ru.kwanza.toolbox.fieldhelper.FieldHelper;

import java.sql.*;
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
    private List<T> checkList = new ArrayList<T>();
    private long result = 0;
    private long skippedCount = 0;
    private Map<Object, VersionPair> versions = new HashMap<Object, VersionPair>();
    private SQLExceptionTranslator exeptionTranslator;

    public BatchPreparedStatementCallableWithVersion(String sql, final Collection<T> objects, UpdateSetterWithVersion<T, V> setter,
                                                     FieldHelper.Field<T, K> keyField, FieldHelper.VersionField<T, V> versionField,
                                                     SQLExceptionTranslator exceptionTranslator, DBTool.DBType dbType) {
        super(sql, dbType);
        this.objects = objects;
        this.setter = setter;
        this.versionField = versionField;
        this.keyField = keyField;
        this.exeptionTranslator = exceptionTranslator;
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
            DataAccessException translate = exeptionTranslator.translate("BatchPreparedStatementCallable", sql, e);
            if (translate instanceof DuplicateKeyException) {
                throw translate;
            }
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

    public Map<Object, VersionPair> getVersions() {
        return versions;
    }

    public static class VersionPair {
        Object newValue;
        Object oldValue;
    }
}

