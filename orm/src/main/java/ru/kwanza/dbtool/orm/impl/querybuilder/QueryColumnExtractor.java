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

import ru.kwanza.dbtool.core.util.FieldValueExtractor;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Alexander Guzanov
 */
abstract class QueryColumnExtractor {

    public abstract Object readColumnValue(ResultSet rs, QueryMapping queryMapping, IFieldMapping fm)
            throws SQLException;


    public static final QueryColumnExtractor BY_INDEX = new QueryColumnExtractor() {
        public Object readColumnValue(ResultSet rs, QueryMapping queryMapping, IFieldMapping fm)
                throws SQLException {
            return FieldValueExtractor.getValue(rs, queryMapping.getColumnIndex(fm), fm.getProperty().getType());
        }
    };

    public static final QueryColumnExtractor BY_ALIAS = new QueryColumnExtractor() {
        public Object readColumnValue(ResultSet rs, QueryMapping queryMapping, IFieldMapping fm)
                throws SQLException {
            return FieldValueExtractor.getValue(rs, queryMapping.getColumnAlias(fm), fm.getProperty().getType());
        }
    };
}
