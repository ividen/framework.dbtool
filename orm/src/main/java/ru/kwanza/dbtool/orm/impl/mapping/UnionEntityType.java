package ru.kwanza.dbtool.orm.impl.mapping;

import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;

import java.sql.Types;
import java.util.*;

/**
 * @author Alexander Guzanov
 */
public class UnionEntityType extends AbstractEntityType {
    private static final String UNION_ALL = " UNION ALL ";
    private static final String CLAZZ_ = "clazz_";
    public FieldMapping CLAZZ_FIELD ;

    private List<IEntityType> entityTypes = new ArrayList<IEntityType>();
    private List<SubUnionEntityType> subUnionEntityTypes;
    private int aliasCounter;
    private boolean validated = false;

    public UnionEntityType(String name, Class entityClass) {
        super(entityClass, name, name + "_", null);
    }

    @Override
    public String getSql() {
        if (subUnionEntityTypes == null) {
            validate();
        }

        if (subUnionEntityTypes.isEmpty()) {
            throw new RuntimeException("Can't find any descendants for entity " + getEntityClass().getName());
        }

        return super.getSql();
    }

    public synchronized void validate() {
        if (!validated) {
            if (subUnionEntityTypes == null) {
                subUnionEntityTypes = new ArrayList<SubUnionEntityType>();

                for (IEntityType entity : entityTypes) {
                    validateEntityType(entity);
                }

                this.CLAZZ_FIELD = FieldMapping.create(this, null, CLAZZ_, Types.BIGINT); ;

                prepareSql();
            }

            validated = true;
        }
    }

    public FieldMapping getClazzField() {
        return CLAZZ_FIELD;
    }

    private void prepareSql() {
        StringBuilder sql = new StringBuilder();
        StringBuilder commonFields = new StringBuilder("");

        for (IFieldMapping fieldMapping : getFields()) {
            if (!(fieldMapping instanceof SubEntityFieldMapping) && !fieldMapping.getColumn().equals(CLAZZ_)) {
                commonFields.append(fieldMapping.getColumn()).append(',');
            }
        }

        for (int i = 0; i < subUnionEntityTypes.size(); i++) {
            sql.append("SELECT ");
            SubUnionEntityType entityType = subUnionEntityTypes.get(i);
            sql.append(commonFields);
            sql.append(i).append(" ").append(CLAZZ_).append(',');

            for (SubUnionEntityType type : subUnionEntityTypes) {
                if (type == entityType) {
                    final Collection<SubEntityFieldMapping> fieldMappings = entityType.getSubFields();
                    for (SubEntityFieldMapping field : fieldMappings) {
                        sql.append(field.getOriginalColumn()).append(' ').append(field.getColumn()).append(',');

                    }
                } else {
                    final Collection<SubEntityFieldMapping> fieldMappings = type.getSubFields();
                    for (SubEntityFieldMapping field : fieldMappings) {
                        sql.append("null ").append(field.getColumn()).append(',');

                    }
                }
            }

            sql.deleteCharAt(sql.length() - 1);
            sql.append(" FROM ");
            final String entitySql = entityType.getSql();
            if (entitySql != null) {
                sql.append('(').append(entitySql).append(") ");
            }

            sql.append(entityType.getTableName()).append(UNION_ALL);
        }
        setSql(sql.delete(sql.length() - UNION_ALL.length(), sql.length()).toString());
    }

    private void validateEntityType(IEntityType entityType) {
        if (entityType instanceof UnionEntityType) {
            final List<IEntityType> entities = ((UnionEntityType) entityType).getEntityTypes();
            for (IEntityType subUnionEntityType : entities) {
                validateEntityType(subUnionEntityType);
            }
        } else {
            SubUnionEntityType subUnionEntityType = new SubUnionEntityType(entityType, this);
            subUnionEntityTypes.add(subUnionEntityType);
        }
    }

    public IFieldMapping getCommonField(String name) {
        return super.getField(name);
    }

    public boolean isAbstract() {
        return true;
    }

    public void addEntity(IEntityType entityType) {
        entityTypes.add(entityType);
    }

    public IEntityType getEntity(int index) {
        return subUnionEntityTypes.get(index);
    }

    public List<IEntityType> getEntityTypes() {
        return Collections.unmodifiableList(entityTypes);
    }

    public int nextFieldAlias() {
        return aliasCounter++;
    }
}
