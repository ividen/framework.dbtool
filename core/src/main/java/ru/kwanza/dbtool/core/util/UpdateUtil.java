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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.kwanza.dbtool.core.*;
import ru.kwanza.toolbox.fieldhelper.FieldHelper;

import java.util.*;

/**
 * @author Guzanov Alexander
 */
public class UpdateUtil {
    public static final Logger logger = LoggerFactory.getLogger(DBTool.class);

    public static <T> long batchUpdate(JdbcTemplate template, String updateSQL, final Collection<T> objects,
                                       final UpdateSetter<T> updateSetter, final DBTool.DBType dbType) throws UpdateException {

        BatchPreparedStatementCallable action =
                new BatchPreparedStatementCallable(updateSQL, objects, updateSetter, template.getExceptionTranslator(), dbType);
        template.execute(action, action);

        if (logger.isTraceEnabled()) {
            logger.trace("Executed sql {}, updateCount={}, constrainedCount={}, skipedCount={}",
                    new Object[]{updateSQL, action.getUpdated().size(), action.getConstrained().size(), action.getSkippedCount()});
        }

        if (!action.getConstrained().isEmpty()) {
            throw new UpdateException("Has some constrained violation!",
                    action.getConstrained(), null, action.getUpdated());

        }

        return action.getUpdated().size();
    }

    public static <T, K extends Comparable, V> long batchUpdate(final JdbcTemplate template, String updateSQL, final Collection<T> objects,
                                                                final UpdateSetterWithVersion<T, V> updateSetter, final String checkSQL,
                                                                final RowMapper<KeyValue<K, V>> keyVersionMapper,
                                                                final FieldHelper.Field<T, K> keyField,
                                                                final FieldHelper.VersionField<T, V> versionField,
                                                                final DBTool.DBType dbType) throws UpdateException {

        List<T> sortedObjects = getSortedList(objects, keyField);
        BatchPreparedStatementCallableWithVersion<T, K, V> action =
                new BatchPreparedStatementCallableWithVersion<T, K, V>(updateSQL, sortedObjects, updateSetter,
                        keyField, versionField, dbType);
        Long result = (Long) template.execute(action, action);

        if (!action.getCheckList().isEmpty()) {
            List<K> fieldList = FieldHelper.getFieldList(action.getCheckList(), keyField);
            Map<K, V> versionById = SelectUtil.selectMap(template, checkSQL, keyVersionMapper, fieldList);
            for (T obj : action.getCheckList()) {
                K key = keyField.value(obj);
                Object version = versionById.get(key);
                V currVersion = (V) action.getVersions().get(key).newValue;
                if ((version == null && currVersion != null) || !version.equals(currVersion)) {
                    action.getOptimistic().add(obj);
                } else {
                    versionField.setValue(obj, currVersion);
                    action.getUpdated().add(obj);
                }
            }
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Executed sql {}, updateCount={}, constrainedCount={}, optimisticCount={}, skipedCount=debug{}",
                    new Object[]{updateSQL, result, action.getConstrained().size(), action.getOptimistic().size(),
                            action.getSkippedCount()});
        }

        if (!action.getConstrained().isEmpty() || !action.getOptimistic().isEmpty()) {
            throw new UpdateException("Has some constrained violation!",
                    action.getConstrained(), action.getOptimistic(), action.getUpdated());
        }

        return action.getUpdated().size();

    }

    public static <T, F extends Comparable<F>> List<T> getSortedList(Collection<T> collection, FieldHelper.Field<T, F> keyField) {
        List<T> list;

        if (collection instanceof List) {
            list = (List<T>) collection;
        } else {
            list = new ArrayList<T>(collection);
        }

        Collections.sort(list, new ObjectByKeyComparator<T, F>(keyField));

        return list;
    }

    public static class ObjectByKeyComparator<T, F extends Comparable<F>> implements Comparator<T> {

        private FieldHelper.Field<T, F> keyField;

        public ObjectByKeyComparator(FieldHelper.Field<T, F> keyField) {
            this.keyField = keyField;
        }

        public int compare(T o1, T o2) {
            return keyField.value(o1).compareTo(keyField.value(o2));
        }

    }

}
