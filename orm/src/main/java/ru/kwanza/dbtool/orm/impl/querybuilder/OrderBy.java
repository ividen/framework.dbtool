package ru.kwanza.dbtool.orm.impl.querybuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Alexander Guzanov
 */
public class OrderBy {
    public static final String ASC = "ASC";
    public static final String DESC = "DESC";
    private String propertyName;
    private String type;

    private OrderBy(String propertyName, String type) {
        this.propertyName = propertyName;
        this.type = type;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getType() {
        return type;
    }

    public static OrderBy ASC(String propertyName) {
        return new OrderBy(propertyName, ASC);
    }

    public static OrderBy DESC(String propertyName) {
        return new OrderBy(propertyName, DESC);
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

    public static void main(String[] args) {
        parse("name");
    }
}
