package ru.kwanza.dbtool.orm.impl.querybuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Alexander Guzanov
 */
class SQLParser {
    static String prepareSQL(String sql, List<Integer> paramTypes, ParamsHolder holder) {
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder paramBuilder = null;
        char[] chars = sql.toCharArray();
        boolean variableMatch = false;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '?') {
                paramTypes.add(Integer.MAX_VALUE);
                paramBuilder = new StringBuilder();
                variableMatch = false;
                sqlBuilder.append('?');
            } else if (c == ':') {
                variableMatch = true;
                sqlBuilder.append('?');
                paramTypes.add(Integer.MAX_VALUE);
                paramBuilder = new StringBuilder();
            } else if (variableMatch) {
                if (isDelimiter(c)) {
                    String paramName = paramBuilder.toString();
                    List<Integer> indexes = holder.get(paramName);
                    if (indexes == null) {
                        indexes = new LinkedList<Integer>();
                        holder.put(paramName, indexes);
                    }
                    indexes.add(paramTypes.size());
                    variableMatch = false;

                    sqlBuilder.append(c);
                } else {
                    paramBuilder.append(c);
                }
            } else {
                sqlBuilder.append(c);
            }
        }

        if (variableMatch) {
            String paramName = paramBuilder.toString();
            List<Integer> indexes = holder.get(paramName);
            if (indexes == null) {
                indexes = new LinkedList<Integer>();
                holder.put(paramName, indexes);
            }
            indexes.add(paramTypes.size());
        }

        return sqlBuilder.toString();
    }

    private static boolean isDelimiter(char c) {
        return c == '+' || c == '-' || c == ' ' || c == ')' || c == '(' || c == '\n' || c == '\t' || c == ',';
    }
}
