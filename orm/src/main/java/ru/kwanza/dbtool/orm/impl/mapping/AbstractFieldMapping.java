package ru.kwanza.dbtool.orm.impl.mapping;

import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;

/**
 * @author Alexander Guzanov
 */
public abstract class AbstractFieldMapping implements IFieldMapping {
    private Integer orderNum;

    public Integer getOrderNum() {
        return orderNum;
    }

    void setOrderNum(int orderNum) {
        if (this.orderNum != null) {
            throw new IllegalStateException("Field  name=" + this.getName()
                    + ", column=" + this.getName() + " already belongs to other entity!");
        }
        this.orderNum = orderNum;
    }
}
