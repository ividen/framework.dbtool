package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;

/**
 * @author Alexander Guzanov
 */
class Column {
    private QueryEntityInfo relation;
    private IFieldMapping fieldMapping;

    Column(QueryEntityInfo relation, IFieldMapping fieldMapping) {
        this.relation = relation;
        this.fieldMapping = fieldMapping;
    }

    public String getColumnName() {
        return relation.getColumnName(fieldMapping);
    }

    int getType() {
        return fieldMapping.getType();
    }
}
