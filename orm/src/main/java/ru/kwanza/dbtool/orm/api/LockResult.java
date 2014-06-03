package ru.kwanza.dbtool.orm.api;

import java.util.Collection;

/**
 * Результат выподлнения блокировки
 *
 * @author Alexander Guzanov
 * @see ru.kwanza.dbtool.orm.api.LockType
 * @see ru.kwanza.dbtool.orm.api.IEntityManager#lock(LockType, Class, java.util.Collection)
 */
public class LockResult<T> {
    private Collection<T> locked;
    private Collection<T> unlocked;

    public LockResult(Collection<T> locked, Collection<T> unlocked) {
        this.locked = locked;
        this.unlocked = unlocked;
    }

    /**
     * Список объектов, на которые удалось установить блокировку
     */
    public Collection<T> getLocked() {
        return locked;
    }

    /**
     * Cписок объектов, на которые не удалось установить блокировку
     */
    public Collection<T> getUnlocked() {
        return unlocked;
    }
}
