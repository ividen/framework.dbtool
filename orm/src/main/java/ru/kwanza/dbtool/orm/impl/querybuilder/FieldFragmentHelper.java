package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
class FieldFragmentHelper {
    public AbstractQueryBuilder builder;

    public FieldFragmentHelper(AbstractQueryBuilder builder) {
        this.builder = builder;
    }

    String createFieldsFragment() {
        StringBuilder result = new StringBuilder();
        processFields(builder.getEntityInfoFactory().getRoot(), result);
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    private void processFields(QueryEntityInfo root, StringBuilder result) {
        Collection<IFieldMapping> fields = root.getRelationMapping() == null
                ? builder.getRegistry().getEntityType(builder.getEntityClass()).getFields()
                : builder.getRegistry().getEntityType(root.getRelationMapping().getRelationClass()).getFields();
        if (fields != null) {
            for (IFieldMapping fm : fields) {
                result.append(root.getColumnWithAlias(fm)).append(",");
            }

        }
        if (root != null && root.getJoins() != null) {
            for (QueryEntityInfo queryEntityInfo : root.getJoins().values()) {
                if (queryEntityInfo.getJoinType() != Join.Type.FETCH) {
                    processFields(queryEntityInfo, result);
                }
            }
        }
    }
}
