package ru.kwanza.dbtool.orm;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public class LockResult<T> {
    private Collection<T> locked;
    private Collection<T> unlocked;

    public LockResult(Collection<T> locked, Collection<T> unlocked) {
        this.locked = locked;
        this.unlocked = unlocked;
    }

    public Collection<T> getLocked() {
        return locked;
    }

    public Collection<T> getUnlocked() {
        return unlocked;
    }
}
