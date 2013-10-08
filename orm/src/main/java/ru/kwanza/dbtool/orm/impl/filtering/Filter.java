package ru.kwanza.dbtool.orm.impl.filtering;

import ru.kwanza.dbtool.orm.api.If;

/**
 * @author Alexander Guzanov
 */
public class Filter {
    private If condition;
    private Object[] value;
    private boolean hasParams = true;

    public Filter(If condition, Object... value) {
        this.condition = condition;
        this.value = value;
    }

    public Filter(If condition) {
        this(condition, null);
        this.hasParams = false;
    }

    public boolean isHasParams() {
        return hasParams;
    }

    public If getCondition() {
        return condition;
    }

    public Object[] getValue() {
        return value;
    }
}
