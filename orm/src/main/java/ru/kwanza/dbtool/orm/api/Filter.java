package ru.kwanza.dbtool.orm.api;

/**
 * @author Alexander Guzanov
 */
public class Filter {
    private  If condition;
    private boolean use;
    private Object[] params;

    public Filter(boolean use, If condition, Object ... params) {
        this.use = use;
        this.condition = condition;
    }

    public If getCondition() {
        return condition;
    }

    public boolean isUse() {
        return use;
    }

    public Object[] getParams() {
        return params;
    }
}
