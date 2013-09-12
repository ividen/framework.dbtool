package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.impl.mapping.FieldMapping;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import java.util.StringTokenizer;

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

    String getFullColumnName() {
        return relation.getAlias() + "_" + fieldMapping.getColumn();
    }

    int getType() {
        return fieldMapping.getType();
    }

}
