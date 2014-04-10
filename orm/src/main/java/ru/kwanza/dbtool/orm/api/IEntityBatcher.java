package ru.kwanza.dbtool.orm.api;

import java.util.Collection;

/**
 * Пакетировщик create,update,delete операций
 *
 * Основания задача, сахранения ссылок на объекты, которые нужно сохранять  и флаширование их с помощью метода {@link #flush}
 *
 * @author Alexander Guzanov
 */
public interface IEntityBatcher {

    /**
     * Зарегистрировать объект для создания в базе данных
     *
     */
    void create(Object object);

    /**
     * Зарегистрировать список объектов для создания в базе данных
     *
     * @param entityClass тип объектов
     * @param objects список объектов
     */
    <T> void create(Class<T> entityClass, Collection objects);

    /**
     * Зарегистрировать объет для измения в базе данных
     *
     * @param object
     */
    void update(Object object);

    /**
     * Зарегистрировать список объектов для изменения в базе данных
     *
     * @param entityClass тип объектов
     * @param objects список объектов
     */
    <T> void update(Class<T> entityClass, Collection objects);

    /**
     * Зарегистрировать объект для удаления из базы данных
     *
     */
    void delete(Object object);

    /**
     * Зарегистрировать объекты для удаления из базы данных
     *
     * @param entityClass тип объектов
     * @param objects список объектов
     */
    <T> void delete(Class<T> entityClass, Collection objects);

    /**
     * Зарегистрировать объект для удаления по ключу
     *
     * @param entityClass тип объект
     * @param key значения ключевого поля
     */
    void deleteByKey(Class entityClass, Object key);

    /**
     * Зарегистрировать объекты для удаления по значениям ключевого поля
     * @param entityClass тип объектов
     * @param keys значения ключевого поля
     */
    void deleteByKeys(Class entityClass, Collection keys);

    /**
     * применить все накопленые изменения и вы полнить запросы к базе данных
     *
     * @throws EntityUpdateException - содержит описание ссылки на объекты, над которыми не удалось выполнить операцию
     */
    void flush() throws EntityUpdateException;
}
