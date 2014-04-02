package ru.kwanza.dbtool.orm.api;

import ru.kwanza.dbtool.core.UpdateException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Основной интерфейс для работы с сущностями через механизм ORM.
 *
 * Для доступа к объекту нужно подключить spring контекст "dbtool-orm-config.xml"
 *
 * @author Alexander Guzanov
 */
public interface IEntityManager {

    /**
     * Сохранение сущности в базе данных
     *
     * @param object объект, должен быть анатирован с помощью {@link ru.kwanza.dbtool.orm.annotations.Entity}
     * @param <T>    тип объекта
     * @throws UpdateException если возникла ошибка вставки. Объект для которого мы пытались добавить запись в базу данных будет в {@link ru.kwanza.dbtool.core.UpdateException#getConstrainted()}
     * @see <a href="http://ru.wikipedia.org/wiki/Insert_(SQL)">INSERT INTO</a>
     * @see ru.kwanza.dbtool.core.UpdateException
     */
    <T> T create(T object) throws UpdateException;

    /**
     * Сохранение сущностей в базе данных.
     * <p/>
     * При сохранении будет использована пакетная вставка данных.
     *
     * @param entityClass тим объектов, находящийся в коллекции, , должен быть анатирован с помощью {@link ru.kwanza.dbtool.orm.annotations.Entity}
     * @param objects     объекты, которые нужно сохранить в базе
     * @param <T>         тип объекта
     * @throws UpdateException если возникла ошибка вставки. Объекты для которых не удалось добавить запись в базу данных будут в {@link ru.kwanza.dbtool.core.UpdateException#getConstrainted()}
     * @see <a href="http://ru.wikipedia.org/wiki/Insert_(SQL)">INSERT INTO</a>
     * @see ru.kwanza.dbtool.core.UpdateException
     */
    <T> Collection<T> create(Class<T> entityClass, Collection<T> objects) throws UpdateException;

    /**
     * Изменение сущности в базе данных
     *
     * @param object объект, должен быть анатирован с помощью {@link ru.kwanza.dbtool.orm.annotations.Entity}
     * @param <T>    тип объекта
     * @throws UpdateException если возникла ошибка вставки. Объект, который мы пытались изменить будет в {@link ru.kwanza.dbtool.core.UpdateException#getConstrainted()} или
     *                         {@link ru.kwanza.dbtool.core.UpdateException#getOptimistic()}
     * @see <a href="http://ru.wikipedia.org/wiki/Update_(SQL)">UPDATE</a>
     * @see ru.kwanza.dbtool.core.UpdateException
     */
    <T> T update(T object) throws UpdateException;

    /**
     * Изменение сущностей в базе данных
     * <p/>
     * При сохранении будет использована пакетная вставка данных.
     *
     * @param entityClass тим объектов, находящийся в коллекции, , должен быть анатирован с помощью {@link ru.kwanza.dbtool.orm.annotations.Entity}
     * @param objects     объекты, которые нужно изменить
     * @param <T>         тип объекта
     * @throws UpdateException если возникла ошибка вставки. Объекты, которые мы пытались изменить  будут в {@link ru.kwanza.dbtool.core.UpdateException#getConstrainted()} или
     *                         {@link ru.kwanza.dbtool.core.UpdateException#getOptimistic()}
     * @see <a href="http://ru.wikipedia.org/wiki/Update_(SQL)">UPDATE</a>
     * @see ru.kwanza.dbtool.core.UpdateException
     */
    <T> Collection<T> update(Class<T> entityClass, Collection<T> objects) throws UpdateException;

    /**
     * Удаление сущности из базы данных
     *
     * @param object объект, должен быть анатирован с помощью {@link ru.kwanza.dbtool.orm.annotations.Entity}
     * @param <T>    тип объекта
     * @throws UpdateException если возникла ошибка вставки. Объект, который пытались удалить будет в {@link ru.kwanza.dbtool.core.UpdateException#getConstrainted()}
     * @see <a href="http://ru.wikipedia.org/wiki/Delete_(SQL)">DELETE</a>
     * @see ru.kwanza.dbtool.core.UpdateException
     */
    <T> T delete(T object) throws UpdateException;

    /**
     * Удаление сущностей в базе данных
     * <p/>
     * При сохранении будет использована пакетная вставка данных.
     *
     * @param entityClass тим объектов, находящийся в коллекции, , должен быть анатирован с помощью {@link ru.kwanza.dbtool.orm.annotations.Entity}
     * @param objects     объекты, которые нужно удалить
     * @param <T>         тип объекта
     * @throws UpdateException если возникла ошибка вставки. Объекты, которые мы пытались удалить будет в {@link ru.kwanza.dbtool.core.UpdateException#getOptimistic()}
     * @see <a href="http://ru.wikipedia.org/wiki/Delete_(SQL)">DELETE</a>
     * @see ru.kwanza.dbtool.core.UpdateException
     */
    <T> Collection<T> delete(Class<T> entityClass, Collection<T> objects) throws UpdateException;

    /**
     * Удаление сущности из базы данных по ключу
     *
     * @param entityClass тип объекта, должен быть анатирован с помощью {@link ru.kwanza.dbtool.orm.annotations.Entity}
     * @param key         значение идентификатора объекта, который нужно удалить
     * @throws UpdateException если возникла ошибка вставки. Объект, который пытались удалить будет в {@link ru.kwanza.dbtool.core.UpdateException#getConstrainted()}
     * @see <a href="http://ru.wikipedia.org/wiki/Delete_(SQL)">DELETE</a>
     * @see ru.kwanza.dbtool.core.UpdateException
     */
    void deleteByKey(Class entityClass, Object key) throws UpdateException;

    /**
     * Удаление сущностей в базе данных
     * <p/>
     * При сохранении будет использована пакетная sql-операция
     *
     * @param entityClass тим объектов, находящийся в коллекции, , должен быть анатирован с помощью {@link ru.kwanza.dbtool.orm.annotations.Entity}
     * @param keys     объекты, которые нужно удалить
     * @throws UpdateException если возникла ошибка вставки. Объекты, которые мы пытались удалить будет в {@link ru.kwanza.dbtool.core.UpdateException#getOptimistic()}
     * @see <a href="http://ru.wikipedia.org/wiki/Delete_(SQL)">DELETE</a>
     * @see ru.kwanza.dbtool.core.UpdateException
     */
    void deleteByKeys(Class entityClass, Collection keys) throws UpdateException;
                            l
    <T> T readByKey(Class<T> entityClass, Object key);

    <T> Collection<T> readByKeys(Class<T> entityClass, Collection keys);

    <F, T> Map<F, T> readMapByKeys(Class<T> entityClass, Collection keys, String propertyName);

    <F, T> Map<F, T> readMapByKeys(Class<T> entityClass, Collection keys);

    <F, T> Map<F, List<T>> readMapListByKeys(Class<T> entityClass, Collection keys, String propertyName);

    <T> IQueryBuilder<T> queryBuilder(Class<T> entityClass);

    <T> IFiltering<T> filtering(Class<T> entityClass);

    IEntityBatcher createEntityBatcher();

    <T> void fetch(Class<T> entityClass, Collection<T> items, String relationPath);

    <T> void fetch(T object, String relationPath);

    <T> void fetchLazy(Class<T> entityClass, Collection<T> items);

    <T> void fetchLazy(T object);

    boolean isProxy(Object object);

    <T> T unwrapProxy(T object);

    boolean isNull(Object object);

    <T> LockResult<T> lock(LockType type, Class<T> entityClass, Collection<T> items);

    <T> LockResult<T> lock(LockType type, T item);
}