package ru.kwanza.dbtool.orm.impl.querybuilder;

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

    protected String createOrderByFragment() {
        StringBuilder orderBy = new StringBuilder();
        final List<OrderBy> orderByList = builder.getOrderBy();
        if (orderByList != null && !orderByList.isEmpty()) {
            for (OrderBy ob : orderByList) {
                orderBy.append(builder.getColumnFactory().findColumn(ob.getPropertyName()).getColumnName()).append(' ').append(ob.getType())
                        .append(", ");
            }

            orderBy.deleteCharAt(orderBy.length() - 2);
        }

        return orderBy.toString();
    }
}
