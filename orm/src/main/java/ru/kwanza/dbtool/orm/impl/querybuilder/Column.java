package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;

/**
 * @author Alexander Guzanov
 */
class Column {
    private EntityInfo relation;
    private IFieldMapping fieldMapping;

    Column(EntityInfo relation, IFieldMapping fieldMapping) {
        this.relation = relation;
        this.fieldMapping = fieldMapping;
    }

    static String getFullColumnName(EntityInfo relation, IFieldMapping fieldMapping) {
        if (relation.isRoot() && relation.getJoins() == null) {
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
