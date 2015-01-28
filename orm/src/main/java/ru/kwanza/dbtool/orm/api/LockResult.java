package ru.kwanza.dbtool.orm.api;

/*
 * #%L
 * dbtool-orm
 * %%
 * Copyright (C) 2015 Kwanza
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
