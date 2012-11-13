package ru.kwanza.dbtool.orm;

import ru.kwanza.dbtool.orm.mapping.FieldMapping;
import ru.kwanza.dbtool.orm.mapping.IEntityMappingRegistry;

/**
 * @author Alexander Guzanov
 */
public abstract class Condition {
    Condition() {
    }

    private static class BaseCondition extends Condition {
        private String propertyName;
        private String suffix;
        private int paramsCount;

        BaseCondition(String propertyName, String keyWord, int paramsCount) {
            this.propertyName = propertyName;
            this.suffix = keyWord;
            this.paramsCount = paramsCount;
        }

        public String toSQLString(IEntityMappingRegistry registry, Class entityClass) {
            FieldMapping fieldMapping = registry.getFieldByPropertyName(entityClass, propertyName);
            return fieldMapping.getColumnName() + ' ' + suffix + " ?";
        }

        public int getParamsCount() {
            return paramsCount;
        }
    }

    public abstract String toSQLString(IEntityMappingRegistry registry, Class entityClass);

    public abstract int getParamsCount();

    public static Condition isEqual(String property) {
        return condition(property, " = ?", 1);
    }

    public static Condition notEqual(String property) {
        return condition(property, " <> ?", 1);
    }

    public static Condition isGreater(String property) {
        return condition(property, " > ?", 1);
    }

    public static Condition isLess(String property) {
        return condition(property, " < ?", 1);
    }

    public static Condition isGreaterOrEqual(String property) {
        return condition(property, " >= ?", 1);
    }

    public static Condition isLessOrEqual(String property) {
        return condition(property, " <= ?", 1);
    }

    public static Condition isNull(String property) {
        return condition(property, " IS NULL", 0);
    }

    public static Condition isNotNull(String property) {
        return condition(property, " IS NOT NULL", 0);
    }

    private static Condition condition(String property, String sing, int paramsCount) {
        return new BaseCondition(property, sing, paramsCount);
    }

    public static Condition in(String property) {
        return condition(property, "IN", 1);
    }

    public static Condition like(String property) {
        return condition(property, "LIKE", 1);
    }

    public static Condition between(String property) {
        return condition(property, "BETWEEN ", 2);
    }

    public static Condition and(Condition... conditions) {
        return new ComplexCondition("AND", conditions);
    }

    public static Condition or(Condition... conditions) {
        return new ComplexCondition("OR", conditions);
    }

    private static class ComplexCondition extends Condition {
        private String keyWord;
        private Condition[] conditions;
        private Integer paramsCount;

        ComplexCondition(String keyWord, Condition[] conditions) {
            this.keyWord = keyWord;
            this.conditions = conditions;
        }

        @Override
        public String toSQLString(IEntityMappingRegistry registry, Class entityClass) {
            StringBuilder result = new StringBuilder();

            if (conditions == null || conditions.length == 0) {
                return "";
            }

            append(result, conditions[0], registry, entityClass);

            for (int i = 1; i < conditions.length; i++) {
                result.append(' ').append(keyWord).append(' ');
                append(result, conditions[i], registry, entityClass);
            }

            return result.toString();
        }

        @Override
        public int getParamsCount() {
            if (paramsCount == null) {
                paramsCount = 0;
                if (conditions != null) {
                    for (Condition c : conditions) {
                        paramsCount += c.getParamsCount();
                    }
                }
            }

        }

        private void append(StringBuilder result, Condition c, IEntityMappingRegistry registry, Class entityClass) {
            result.append('(').append(c.toSQLString(registry, entityClass)).append(')');
        }
    }
}

