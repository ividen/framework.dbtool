package ru.kwanza.dbtool.orm.impl.filtering;

import ru.kwanza.dbtool.orm.api.Condition;

/**
 * @author Alexander Guzanov
 */
public class Filter {
    private Condition condition;
    private Object[] value;
    private boolean hasParams = true;

    public Filter(Condition condition, Object ... value) {
        this.condition = condition;
        this.value = value;
    }

    public Filter(Condition condition) {
        this(condition, null);
        this.hasParams = false;
    }

    public boolean isHasParams() {
        return hasParams;
    }

    public Condition getCondition() {
        return condition;
    }

    public Object[] getValue() {
        return value;
    }
}
