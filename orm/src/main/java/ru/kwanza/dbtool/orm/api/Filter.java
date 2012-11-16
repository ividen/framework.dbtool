package ru.kwanza.dbtool.orm.api;

/**
 * @author Alexander Guzanov
 */
public class Filter {
    private boolean use;
    private Condition condition;
    private Object[] value;
    private boolean hasParams = true;

    public Filter(boolean use, Condition condition, Object ... value) {
        this.use = use;
        this.condition = condition;
        this.value = value;
    }

    public Filter(boolean use, Condition condition) {
        this(use, condition, null);
        this.hasParams = false;
    }

    public boolean isUse() {
        return use;
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
