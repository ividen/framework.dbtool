package ru.kwanza.dbtool.orm.impl.mapping;

import ru.kwanza.dbtool.orm.api.internal.IEntity;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Alexander Guzanov
 */
public class UnionEntity extends AbstractEntity {
    private static final String UNION_ALL = " UNION ALL ";
    private static final String CLAZZ_ = "clazz_";
    private int aliasCounter;

    private List<SubUnionEntity> entities = new ArrayList<SubUnionEntity>();

    public String getNextAlias() {
        return "f_" + (aliasCounter++);
    }

    public void validate() {
        if (getSql() != null) {
            return;
        }
        for (SubUnionEntity entity : entities) {
            entity.validate();
        }

        StringBuilder sql = new StringBuilder("(SELECT ");
        StringBuilder commonFields = new StringBuilder("");

        for (IFieldMapping fieldMapping : getFields()) {
            commonFields.append(fieldMapping.getColumn()).append(',');
        }

        Set<String> fields = new LinkedHashSet<String>();

        for (int i = 0; i < entities.size(); i++) {
            SubUnionEntity entity = entities.get(i);
            IEntity baseEntity = entity.getEntity();
            sql.append(commonFields);
            sql.append(i).append(" ").append(CLAZZ_).append(',');

            for (String field : fields) {
                sql.append("null ").append(field).append(',');
            }

            for (IFieldMapping fieldMapping : entity.getFields()) {
                final IFieldMapping parentField = getField(fieldMapping.getName());
                if (parentField == null) {
                    sql.append(baseEntity.getField(fieldMapping.getColumn())).append(' ');
                    sql.append(fieldMapping.getColumn());
                    fields.add(fieldMapping.getColumn());
                }
            }

            sql.deleteCharAt(sql.length() - 1);
            sql.append(" FROM ");
            final String entitySql = entity.getSql();
            if (entitySql != null) {
                sql.append(entitySql).append(' ');
            }

            sql.append(entity.getTableName()).append(UNION_ALL);
        }
        setSql(sql.delete(sql.length() - UNION_ALL.length(), sql.length()).toString());
    }

    public boolean isAbstract() {
        return false;
    }

    public void addEntity(IEntity entity) {
        entities.add(new SubUnionEntity(entity, this));
    }

    public static final String getClassColumnName() {
        return CLAZZ_;
    }

}
