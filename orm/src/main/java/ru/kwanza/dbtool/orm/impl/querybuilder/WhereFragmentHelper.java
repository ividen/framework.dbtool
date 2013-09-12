package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.Condition;

import java.util.List;

/**
 * @author Alexander Guzanov
 */
class WhereFragmentHelper {
    private AbstractQueryBuilder builder;

    WhereFragmentHelper(AbstractQueryBuilder builder) {
        this.builder = builder;
    }

    public String createWhere(Condition condition, List<Integer> paramsTypes, ParamsHolder paramsHolder) {
        StringBuilder result = new StringBuilder();

        createConditionString(condition, paramsTypes, result,paramsHolder);

        return result.toString();
    }

    private void createConditionString(Condition condition, List<Integer> paramsTypes, StringBuilder where, ParamsHolder holder) {
        if (condition == null) {
            return;
        }

        Condition[] childs = condition.getChilds();
        Condition.Type type = condition.getType();
        if (childs != null && childs.length > 0) {
            if (type != Condition.Type.NOT) {
                where.append('(');
                createConditionString(childs[0], paramsTypes, where, holder);
                where.append(')');

                for (int i = 1; i < childs.length; i++) {
                    Condition c = childs[i];
                    where.append(' ').append(type.name()).append(" (");
                    createConditionString(c, paramsTypes, where, holder);
                    where.append(')');
                }
            } else {
                where.append("NOT (");
                createConditionString(childs[0], paramsTypes, where, holder);
                where.append(')');
            }
        } else if (type == Condition.Type.NATIVE) {
            where.append(SQLParser.prepareSQL(condition.getSql(), paramsTypes,holder));
        } else {

            final Column column = builder.getColumnFactory().findColumn(condition.getPropertyName());
            where.append(column.getFullColumnName());
            final int fieldType = column.getType();

            if (type == Condition.Type.IS_EQUAL) {
                holder.addParam(condition, paramsTypes, fieldType);
                where.append(" = ?");
            } else if (type == Condition.Type.NOT_EQUAL) {
                holder.addParam(condition, paramsTypes, fieldType);
                where.append(" <> ?");
            } else if (type == Condition.Type.IS_NOT_NULL) {
                where.append(" IS NOT NULL");
            } else if (type == Condition.Type.IS_NULL) {
                where.append(" IS NULL");
            } else if (type == Condition.Type.IS_GREATER) {
                holder.addParam(condition, paramsTypes, fieldType);
                where.append(" > ?");
            } else if (type == Condition.Type.IS_GREATER_OR_EQUAL) {
                holder.addParam(condition, paramsTypes, fieldType);
                where.append(" >= ?");
            } else if (type == Condition.Type.IS_LESS) {
                holder.addParam(condition, paramsTypes, fieldType);
                where.append(" < ?");
            } else if (type == Condition.Type.IS_LESS_OR_EQUAL) {
                holder.addParam(condition, paramsTypes, fieldType);
                where.append(" <= ?");
            } else if (type == Condition.Type.IN) {
                holder.addParam(condition, paramsTypes, fieldType);
                where.append(" IN (?)");
            } else if (type == Condition.Type.LIKE) {
                holder.addParam(condition, paramsTypes, fieldType);
                where.append(" LIKE ?");
            } else if (type == Condition.Type.BETWEEN) {
                holder.addParam(condition, paramsTypes, fieldType);
                holder.addParam(condition, paramsTypes, fieldType);
                where.append(" BETWEEN ? AND ?");
            } else {
                throw new IllegalArgumentException("Unknown condition type!");
            }
        }
    }
}
