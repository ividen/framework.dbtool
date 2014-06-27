package ru.kwanza.dbtool.orm.impl.mapping;

import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public class SubUnionEntityType extends AbstractEntityType {
    private static final String FIELD_PREFIX = "f_";

    private IEntityType original;
    private Map<String, SubEntityFieldMapping> fields = new HashMap<String, SubEntityFieldMapping>();

    public SubUnionEntityType(IEntityType original, UnionEntityType unionEntityType) {
        this.original = original;
        final Collection<AbstractFieldMapping> fields1 = original.getFields();
        for (AbstractFieldMapping field : fields1) {
            if (unionEntityType.getCommonField(field.getName()) == null) {
                String alias = FIELD_PREFIX + unionEntityType.nextFieldAlias();
                SubEntityFieldMapping subEntityField = new SubEntityFieldMapping(original, field, alias);
                SubEntityFieldMapping unionField = new SubEntityFieldMapping(original, field, alias);

                fields.put(field.getName(), subEntityField);
                addField(subEntityField);
                unionEntityType.addField(unionField);
            } else {
                addField(new CommondFieldMapping(field));
            }
        }
    }

    public String getName() {
        return original.getName();
    }

    public String getTableName() {
        return original.getTableName();
    }

    public String getSql() {
        return original.getSql();
    }

    public Class getEntityClass() {
        return original.getEntityClass();
    }

    public boolean isAbstract() {
        return original.isAbstract();
    }

    public IFieldMapping getIdField() {
        return original.getIdField();
    }

    public IFieldMapping getVersionField() {
        return original.getVersionField();
    }

    public Collection<SubEntityFieldMapping> getSubFields() {
        return Collections.<SubEntityFieldMapping>unmodifiableCollection(fields.values());
    }

    public SubEntityFieldMapping getSubField(String name) {
        return fields.get(name);
    }

    public IRelationMapping getRelation(String name) {
        return original.getRelation(name);
    }

    public Collection<IRelationMapping> getRelations() {
        return original.getRelations();
    }
}
