package ru.kwanza.dbtool.orm.api;

/**
 * Типы блокровок сущностей
 *
 * @author Alexander Guzanov
 */
public enum LockType {
    /**
     * Блокируем сущности в пессемистическом режиме (SELECT FOT UPDATE)
     * В случае, если сущность уже заблокирована -ожидаем блокировки
     */
    WAIT,
    /**
     *
     * Блокируем сущности в пессемистическом режиме (SELECT FOT UPDATE)
     * В случае, если сущность уже заблокирована - возвращемся из метода блокировки без ожидание,
     * т.е блокировка не выполняется
     */
    NOWAIT,
    /**
     * Блокируем сущности в пессемистическом режиме (SELECT FOT UPDATE)
     * В случае, если сущность уже заблокирована, пропускаем её и пытыемся блокировать следующую сущность в пачк
     */
    SKIP_LOCKED,
    /**
     * Обновление записи с увеличением значения поля версии
     */
    INC_VERSION,
}
