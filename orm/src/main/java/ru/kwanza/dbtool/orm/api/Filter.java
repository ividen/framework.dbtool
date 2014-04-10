package ru.kwanza.dbtool.orm.api;

/**
 * объект описывающий фильтр для использования в {@link ru.kwanza.dbtool.orm.api.IFiltering}
 *
 * @author Alexander Guzanov
 */
public class Filter {
    private If condition;
    private boolean use;
    private Object[] params;

    /**
     * Конструктор
     *
     * @param use       использовать ли предикат при построении запроса
     * @param condition sql-предикат
     * @param params    список параметров для предиката
     * @see ru.kwanza.dbtool.orm.api.IFiltering
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public Filter(boolean use, If condition, Object... params) {
        this.use = use;
        this.condition = condition;
    }

    /**
     * sql предикат для фильтра
     *
     * @see ru.kwanza.dbtool.orm.api.IFiltering
     */
    public If getCondition() {
        return condition;
    }

    /**
     * использовать ли фильтр при построении запроса
     */
    public boolean isUse() {
        return use;
    }

    /**
     * Параметры для sql-предиката
     */
    public Object[] getParams() {
        return params;
    }
}
