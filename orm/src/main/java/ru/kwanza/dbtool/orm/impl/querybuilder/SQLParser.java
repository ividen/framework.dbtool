package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.springframework.jdbc.core.SqlTypeValue;

/**
 * @author Alexander Guzanov
 */
class SQLParser {
    static String prepareSQL(String sql, Parameters holder) {
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder paramBuilder = null;
        char[] chars = sql.toCharArray();
        boolean variableMatch = false;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '?') {
                holder.addParam(SqlTypeValue.TYPE_UNKNOWN);

                paramBuilder = new StringBuilder();
                variableMatch = false;
                sqlBuilder.append('?');
            } else if (c == ':') {
                variableMatch = true;
                sqlBuilder.append('?');
                paramBuilder = new StringBuilder();
            } else if (variableMatch) {
                if (isDelimiter(c)) {
                    String paramName = paramBuilder.toString();
                    holder.addParam(paramName,SqlTypeValue.TYPE_UNKNOWN);
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
            holder.addParam(paramName,SqlTypeValue.TYPE_UNKNOWN);
        }

        return sqlBuilder.toString();
    }

    private static boolean isDelimiter(char c) {
        return c == '+' || c == '-' || c == ' ' || c == ')' || c == '(' || c == '\n' || c == '\t' || c == ',';
    }
}
