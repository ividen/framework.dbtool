package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;

/**
 * @author Alexander Guzanov
 */
class Column {
    private JoinRelation relation;
    private IFieldMapping fieldMapping;

    Column(JoinRelation relation, IFieldMapping fieldMapping) {
        this.relation = relation;
        this.fieldMapping = fieldMapping;
    }

    static String getFullColumnName(JoinRelation relation, IFieldMapping fieldMapping) {
        if (relation.isRoot() && relation.getAllChilds() == null) {
            return fieldMapping.getColumn();
        }
        return relation.getAlias() + "_" + fieldMapping.getColumn();
    }

    String getColumnName() {
        return relation.getAlias() + "." + fieldMapping.getColumn();
    }

    int getType() {
        return fieldMapping.getType();
    }
}
