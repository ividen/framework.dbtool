package ru.kwanza.dbtool.orm.impl.mapping;

import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Alexander Guzanov
 */
public class UnionEntityType extends AbstractEntityType {
    private static final String UNION_ALL = " UNION ALL ";
    private static final String CLAZZ_ = "clazz_";
    private int aliasCounter;

    private List<SubUnionEntityType> entities = new ArrayList<SubUnionEntityType>();

    public UnionEntityType(String name, Class entityClass) {
        setName(name);
        setEntityClass(entityClass);
        setTableName(name + "_");
    }

    public String getNextAlias() {
        return "f_" + (aliasCounter++);
    }

    @Override
    public String getSql() {
        if (entities.isEmpty()) {
            return null;
        }
        for (SubUnionEntityType entity : entities) {
            entity.validate();
        }

        StringBuilder sql = new StringBuilder();
        StringBuilder commonFields = new StringBuilder("");

        for (IFieldMapping fieldMapping : getFields()) {
            commonFields.append(fieldMapping.getColumn()).append(',');
        }

        Set<String> fields = new LinkedHashSet<String>();

        for (int i = 0; i < entities.size(); i++) {
            sql.append("SELECT ");
            SubUnionEntityType entity = entities.get(i);
            IEntityType baseEntity = entity.getEntity();
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
                sql.append('(').append(entitySql).append(") ");
            }

            sql.append(entity.getTableName()).append(UNION_ALL);
        }
        return  sql.delete(sql.length() - UNION_ALL.length(), sql.length()).toString();
    }

    public void validate() {
//        if (entities.isEmpty()) {
//            return;
//        }
//        for (SubUnionEntityType entity : entities) {
//            entity.validate();
//        }
//
//        StringBuilder sql = new StringBuilder();
//        StringBuilder commonFields = new StringBuilder("");
//
//        for (IFieldMapping fieldMapping : getFields()) {
//            commonFields.append(fieldMapping.getColumn()).append(',');
//        }
//
//        Set<String> fields = new LinkedHashSet<String>();
//
//        for (int i = 0; i < entities.size(); i++) {
//            sql.append("SELECT ");
//            SubUnionEntityType entity = entities.get(i);
//            IEntityType baseEntity = entity.getEntity();
//            sql.append(commonFields);
//            sql.append(i).append(" ").append(CLAZZ_).append(',');
//
//            for (String field : fields) {
//                sql.append("null ").append(field).append(',');
//            }
//
//            for (IFieldMapping fieldMapping : entity.getFields()) {
//                final IFieldMapping parentField = getField(fieldMapping.getName());
//                if (parentField == null) {
//                    sql.append(baseEntity.getField(fieldMapping.getColumn())).append(' ');
//                    sql.append(fieldMapping.getColumn());
//                    fields.add(fieldMapping.getColumn());
//                }
//            }
//
//            sql.deleteCharAt(sql.length() - 1);
//            sql.append(" FROM ");
//            final String entitySql = entity.getSql();
//            if (entitySql != null) {
//                sql.append(entitySql).append(' ');
//            }
//
//            sql.append(entity.getTableName()).append(UNION_ALL);
//        }
//        setSql(sql.delete(sql.length() - UNION_ALL.length(), sql.length()).toString());
    }

    public boolean isAbstract() {
        return true;
    }

    public void addEntity(IEntityType entity) {
        entities.add(new SubUnionEntityType(entity, this));
        validate();
    }

    public static final String getClassColumnName() {
        return CLAZZ_;
    }

    public IEntityType getEntity(int index) {
        return entities.get(index);
    }

}
