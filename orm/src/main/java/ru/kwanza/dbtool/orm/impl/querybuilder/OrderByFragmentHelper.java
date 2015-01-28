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

import ru.kwanza.dbtool.orm.api.OrderBy;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Alexander Guzanov
 */
public class OrderByFragmentHelper {
    private AbstractQueryBuilder builder;

    public OrderByFragmentHelper(AbstractQueryBuilder builder) {
        this.builder = builder;
    }

    public static List<OrderBy> parse(String orderByClause) {
        StringTokenizer tokenizer = new StringTokenizer(orderByClause, ",");
        List<OrderBy> result = new ArrayList<OrderBy>();
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken().trim();
            final int i = token.indexOf(' ');
            if (i == -1) {
                result.add(OrderBy.ASC(token));
            } else {
                final String type = token.substring(i + 1).trim();
                if (type.equals("ASC")) {
                    result.add(OrderBy.ASC(token.substring(0, i).trim()));
                } else if (type.equals("DESC")) {
                    result.add(OrderBy.DESC(token.substring(0, i).trim()));
                }
            }

        }

        return result;
    }

    String createOrderByFragment() {
        StringBuilder orderBy = new StringBuilder();
        final List<OrderBy> orderByList = builder.getOrderBy();
        if (orderByList != null && !orderByList.isEmpty()) {
            for (OrderBy ob : orderByList) {
                orderBy.append(
                        builder.getColumnFactory().findColumn(builder.getQueryMappingFactory().getRoot(), ob.getPropertyName()).getColumnName())
                        .append(' ').append(ob.getType()).append(',');
            }

            orderBy.deleteCharAt(orderBy.length() - 1);
        }

        return orderBy.toString();
    }
}
