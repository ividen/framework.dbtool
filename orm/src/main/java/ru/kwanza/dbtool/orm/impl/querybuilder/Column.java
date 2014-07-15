package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;

/**
 * @author Alexander Guzanov
 */
class Column {
    private QueryMapping relation;
    private IFieldMapping fieldMapping;

    Column(QueryMapping relation, IFieldMapping fieldMapping) {
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
