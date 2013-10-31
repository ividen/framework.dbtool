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
        String alias = builder.getEntityInfoFactory().getRoot().getAllChilds() == null
                ? null
                : builder.getEntityInfoFactory().getRoot().getAlias();
        processFields(alias, builder.getEntityInfoFactory().getRoot(), result);
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    private void processFields(String alias, EntityInfo root, StringBuilder result) {
        Collection<IFieldMapping> fields = root.getRelationMapping() == null
                ? builder.getRegistry().getEntityType(builder.getEntityClass()).getFields()
                : builder.getRegistry().getEntityType(root.getRelationMapping().getRelationClass()).getFields();
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
            for (EntityInfo entityInfo : root.getAllChilds().values()) {
                if (entityInfo.getJoinType() != Join.Type.FETCH) {
                    processFields(entityInfo.getAlias(), entityInfo, result);
                }
            }
        }
    }
}
