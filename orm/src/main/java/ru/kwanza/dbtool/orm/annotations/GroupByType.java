package ru.kwanza.dbtool.orm.annotations;

/**
 * Тип крупировки
 * <ul>
 * <li><B>MAP</B> - групировка по уникальному полю. Результатом группировки является карта <i>Map&lt;?,?&gt;</i></li>
 * <li><B>MAP_OF_LIST</B> - групировка по неуникальному полю. Результатом группировки является карта <i>Map&lt;?,List&lt;?&gt;&gt;</i></li>
 * </ul>
 *
 * @author Alexander Guzanov
 * @see ru.kwanza.dbtool.orm.annotations.GroupBy
 */
public enum GroupByType {
    /**
     * групировка по уникальному полю. Результатом группировки является карта <i>Map&lt;?,?&gt;</i></li>
     */
    MAP,
    /**
     * групировка по неуникальному полю. Результатом группировки является карта <i>Map&lt;?,List&lt;?&gt;&gt;</i></li>
     */
    MAP_OF_LIST;
}
