package ru.kwanza.dbtool.orm.api;

/**
 * @author Alexander Guzanov
 */
public enum LockType {
    PESSIMISTIC_WAIT,
    PESSIMISTIC_NOWAIT,
    PESSIMISTIC_SKIP,
    OPTIMISTIC,
}
