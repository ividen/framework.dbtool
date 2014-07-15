package ru.kwanza.dbtool.orm.impl.mapping;

import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.toolbox.fieldhelper.Property;

/**
 * @author Alexander Guzanov
 */
public class SubEntityFieldMapping extends AbstractFieldMapping {
    private final String name;
    private final int newOrderNum;
    private final IFieldMapping originalField;
    private final String column;


    public SubEntityFieldMapping(int orderNum,IFieldMapping originalField, String alias) {
        this.newOrderNum = orderNum;
        this.originalField = originalField;
        this.name = this.column = alias;
    }


    @Override
    public Integer getOrderNum() {
        return newOrderNum;
    }

    public String getColumn() {
        return column;
    }

    public String getOriginalColumn() {
        return originalField.getColumn();
    }

    public int getType() {
        return originalField.getType();
    }

    public String getName() {
        return name;
    }

    public Property getProperty() {
        return originalField.getProperty();
    }
}
