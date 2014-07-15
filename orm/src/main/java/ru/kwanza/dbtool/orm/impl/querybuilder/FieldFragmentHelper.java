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
        processFields(0, builder.getEntityInfoFactory().getRoot(), result);
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    private int processFields(int fieldStartIndex, QueryEntityInfo root, StringBuilder result) {
        root.setFieldStartIndex(fieldStartIndex);
        Collection<IFieldMapping> fields = root.getEntityType().getFields();
        for (IFieldMapping fm : fields) {
            result.append(root.getColumnWithAlias(fm)).append(",");
        }

        fieldStartIndex += fields.size();

        if (root.getJoins() != null) {
            for (QueryEntityInfo queryEntityInfo : root.getJoins().values()) {
                if (queryEntityInfo.getJoinType() != Join.Type.FETCH) {
                    fieldStartIndex = processFields(fieldStartIndex, queryEntityInfo, result);
                }
            }
        }

        return fieldStartIndex;
    }
}
