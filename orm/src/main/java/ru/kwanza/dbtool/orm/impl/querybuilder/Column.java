package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.impl.mapping.FieldMapping;

/**
 * @author Alexander Guzanov
 */
class Column {
    private JoinRelation relation;
    private FieldMapping fieldMapping;

    Column(JoinRelation relation, FieldMapping fieldMapping) {
        this.relation = relation;
        this.fieldMapping = fieldMapping;
    }

    static String getFullColumnName(JoinRelation relation,FieldMapping fieldMapping) {
        return relation.getAlias() + "_" + fieldMapping.getColumn();
    }

    String getColumnName() {
        return relation.getAlias() + "." + fieldMapping.getColumn();
    }

    int getType() {
        return fieldMapping.getType();
    }
}
