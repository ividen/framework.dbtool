package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;

import java.util.StringTokenizer;

/**
 * @author Alexander Guzanov
 */
class ColumnFactory {
    public static final String DOT_CHAR = ".";
    private final AbstractQueryBuilder builder;

    ColumnFactory(AbstractQueryBuilder builder) {
        this.builder = builder;
    }

    Column findColumn(QueryMapping root, String propertyName) {
        final int index = propertyName.lastIndexOf(DOT_CHAR);
        Class entityClass = builder.getEntityClass();
        if (index > 0) {
            final String path = propertyName.substring(0, index);
            propertyName = propertyName.substring(index + 1);

            StringTokenizer st = new StringTokenizer(path, ".");
            while (st.hasMoreElements()) {
                final String token = st.nextToken();
                root = builder.getQueryMappingFactory().registerInfo(root, Join.inner(token));
                entityClass = root.getRelationMapping().getRelationClass();
            }
        }

        final IFieldMapping fieldMapping = builder.getRegistry().getEntityType(entityClass).getField(propertyName);
        if (fieldMapping == null) {
            throw new IllegalArgumentException("Unknown field " + propertyName + " in " + entityClass.getName() + "!");
        }

        return new Column(root, fieldMapping);
    }

}
