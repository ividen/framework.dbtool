package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.If;

import java.util.List;

/**
 * @author Alexander Guzanov
 */
class WhereFragmentHelper {
    private AbstractQueryBuilder builder;

    WhereFragmentHelper(AbstractQueryBuilder builder) {
        this.builder = builder;
    }

    public String createWhereFragment(If condition,ParamsHolder paramsHolder) {
        StringBuilder result = new StringBuilder();

        createConditionString(condition,  result, paramsHolder);

        return result.toString();
    }

    private void createConditionString(If condition, StringBuilder where, ParamsHolder holder) {
        if (condition == null) {
            return;
        }

        If[] childs = condition.getChilds();
        If.Type type = condition.getType();
        if (childs != null && childs.length > 0) {
            if (type != If.Type.NOT) {
                where.append('(');
                createConditionString(childs[0], where, holder);
                where.append(')');

                for (int i = 1; i < childs.length; i++) {
                    If c = childs[i];
                    where.append(' ').append(type.name()).append(" (");
                    createConditionString(c, where, holder);
                    where.append(')');
                }
            } else {
                where.append("NOT (");
                createConditionString(childs[0], where, holder);
                where.append(')');
            }
        } else if (type == If.Type.NATIVE) {
            where.append(SQLParser.prepareSQL(condition.getSql(), holder));
        } else {

            final Column column = builder.getColumnFactory().findColumn(condition.getPropertyName());
            where.append(column.getColumnName());
            final int fieldType = column.getType();

            if (type == If.Type.IS_EQUAL) {
                holder.addParam(condition, fieldType);
                where.append(" = ?");
            } else if (type == If.Type.NOT_EQUAL) {
                holder.addParam(condition, fieldType);
                where.append(" <> ?");
            } else if (type == If.Type.IS_NOT_NULL) {
                where.append(" IS NOT NULL");
            } else if (type == If.Type.IS_NULL) {
                where.append(" IS NULL");
            } else if (type == If.Type.IS_GREATER) {
                holder.addParam(condition, fieldType);
                where.append(" > ?");
            } else if (type == If.Type.IS_GREATER_OR_EQUAL) {
                holder.addParam(condition, fieldType);
                where.append(" >= ?");
            } else if (type == If.Type.IS_LESS) {
                holder.addParam(condition, fieldType);
                where.append(" < ?");
            } else if (type == If.Type.IS_LESS_OR_EQUAL) {
                holder.addParam(condition, fieldType);
                where.append(" <= ?");
            } else if (type == If.Type.IN) {
                holder.addParam(condition, fieldType);
                where.append(" IN (?)");
            } else if (type == If.Type.LIKE) {
                holder.addParam(condition, fieldType);
                where.append(" LIKE ?");
            } else if (type == If.Type.BETWEEN) {
                holder.addParam(condition, fieldType);
                holder.addParam(condition, fieldType);
                where.append(" BETWEEN ? AND ?");
            } else {
                throw new IllegalArgumentException("Unknown condition type!");
            }
        }
    }
}
