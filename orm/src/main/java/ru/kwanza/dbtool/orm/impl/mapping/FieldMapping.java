package ru.kwanza.dbtool.orm.impl.mapping;

import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.toolbox.fieldhelper.Property;

/**
 * @author Kiryl Karatsetski
 */
class FieldMapping implements IFieldMapping {
    private String name;
    private String column;
    public int type;
    private Property property;

    public FieldMapping(String name, String column, int type, Property property) {
        this.name = name;
        this.column = column;
        this.type = type;
        this.property = property;
    }

    public String getColumn() {
        return column;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Property getProperty() {
        return this.property;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FieldMapping{");
        stringBuilder.append("name='").append(name).append('\'');
        stringBuilder.append(", column='").append(column).append('\'');
        stringBuilder.append(", type=").append(type);
        stringBuilder.append(", property=").append(property != null ? property.getClass() : null);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
