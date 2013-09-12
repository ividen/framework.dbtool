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

    static Column findColumn(IEntityMappingRegistry registry, Class entityClass, JoinRelation rootRelations, String propertyName) {
        final int index = propertyName.lastIndexOf('.');
        JoinRelation root = rootRelations;

        if (index > 0) {
            final String path = propertyName.substring(0, index - 1);
            propertyName = propertyName.substring(index + 1);

            root = rootRelations;
            StringTokenizer st = new StringTokenizer(path, ".");
            while (st.hasMoreElements()) {
                final String token = st.nextToken();
                if (root.getChild(token) == null) {
                    root = JoinRelation.createJoinRelation(registry, root, Join.Type.INNER, entityClass, token);
                }
            }
        }

        final FieldMapping fieldMapping = registry.getFieldMappingByPropertyName(entityClass, propertyName);
        if (fieldMapping == null) {
            throw new IllegalArgumentException("Unknown field " + propertyName + " in " + entityClass.getName() + "!");
        }

        return new Column(root, fieldMapping);
    }

}
