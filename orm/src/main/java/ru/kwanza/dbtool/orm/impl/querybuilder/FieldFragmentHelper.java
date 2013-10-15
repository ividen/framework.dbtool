package ru.kwanza.dbtool.orm.impl.querybuilder;

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
        String alias =
                builder.getRelationFactory().getRoot().getAllChilds() == null ? null : builder.getRelationFactory().getRoot().getAlias();
        processFields(alias, builder.getRelationFactory().getRoot(), result);
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    private void processFields(String alias, JoinRelation root, StringBuilder result) {
        Collection<IFieldMapping> fields = root.getRelationMapping() == null
                ? builder.getRegistry().getFieldMappings(builder.getEntityClass())
                : builder.getRegistry().getFieldMappings(root.getRelationMapping().getRelationClass());
        if (fields != null) {
            for (IFieldMapping fm : fields) {
                if (alias != null) {
                    result.append(alias).append('.').append(fm.getColumn()).append(' ').append(alias).append('_').append(fm.getColumn())
                            .append(",");
                } else {
                    result.append(fm.getColumn()).append(",");
                }
            }

        }
        if (root != null && root.getAllChilds() != null) {
            for (JoinRelation joinRelation : root.getAllChilds().values()) {
                processFields(joinRelation.getAlias(), joinRelation, result);
            }
        }
    }
}
