package ru.kwanza.dbtool.orm.api;

import ru.kwanza.dbtool.core.UpdateException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Основной интерфейс для работы с сущностями через механизм ORM.
 * <p/>
 * Для доступа к объекту нужно подключить spring контекст "dbtool-orm-config.xml"
 * <p/>
 * Объект доступен по имени <b>dbtool.IEntityManager</b>.
 * Для того, чтобы сущность могла быть доступна через механизм ORM нужно в spring добавить :
 * <p/>
 * <pre>
 *    <b> &lt;dbtool-orm:entityMapping scan-package="<i>здесь должно быть имя пакета с сущностями</i>"/&gt;</b>
 * </pre>
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
     * @param keys        объекты, которые нужно удалить
     * @throws UpdateException если возникла ошибка вставки. Объекты, которые мы пытались удалить будет в {@link ru.kwanza.dbtool.core.UpdateException#getOptimistic()}
     * @see <a href="http://ru.wikipedia.org/wiki/Delete_(SQL)">DELETE</a>
     * @see ru.kwanza.dbtool.core.UpdateException
     */
    void deleteByKeys(Class entityClass, Collection keys) throws UpdateException;

    /**
     * Прочитать из базы данных объект по ключу
     *
     * @param entityClass тип сущности
     * @param key         значение ключевого поля
     * @return объект из базы данных, или null  - если в базе не был найден
     */
    <T> T readByKey(Class<T> entityClass, Object key);

    /**
     * Прочитать из базы данных объекты по ключу
     *
     * @param entityClass тип сущности
     * @param keys        значения ключей
     * @return список объектов с соответствующими значениями ключейю.
     */
    <T> Collection<T> readByKeys(Class<T> entityClass, Collection keys);

    /**
     * Прочитать из базы данных объекты по ключу и сгрупировать по значению уникального свойства
     *
     * @param entityClass  тип сущности
     * @param keys         значения ключей
     * @param propertyName имя свойства по которому нужно группировать.
     * @return карта, в которой ключ - значения соотвествующего свойства, значение - сам объект
     */
    <F, T> Map<F, T> readMapByKeys(Class<T> entityClass, Collection keys, String propertyName);

    /**
     * Прочитать из базы данных объекты по ключу и сгрупировать по идентификатору
     *
     * @param entityClass тип сущности
     * @param keys        значения ключей
     * @return карта, в которой ключ - значения идентификатора, значение - сам объект
     */
    <F, T> Map<F, T> readMapByKeys(Class<T> entityClass, Collection keys);

    /**
     * Прочитать из базы данных объекты по идентификаторам и сгрупировать по значению неуникального свойства
     *
     * @param entityClass  тип сущности
     * @param keys         значения идентификаторов
     * @param propertyName имя свойства по которому нужно группировать.
     * @return карта списков, в которой ключ - значения соотвествующего свойства, значение - список объектов у которого свойво равно ключу
     */
    <F, T> Map<F, List<T>> readMapListByKeys(Class<T> entityClass, Collection keys, String propertyName);

    /**
     * Создание построителя запросов
     *
     * @param entityClass тип сущности
     * @see ru.kwanza.dbtool.orm.api.IQueryBuilder
     * @see ru.kwanza.dbtool.orm.api.IQuery
     */
    <T> IQueryBuilder<T> queryBuilder(Class<T> entityClass);

    /**
     * Создание построителя динамических запросов на основании фильтров
     *
     * @param entityClass тип сущности
     * @see ru.kwanza.dbtool.orm.api.IFiltering
     */
    <T> IFiltering<T> filtering(Class<T> entityClass);

    /**
     * Создание пакетировщика, который может накапливать объекты для выполнения пакетных операций по методу {@link IEntityBatcher#flush()}
     *
     * @return
     */
    IEntityBatcher createEntityBatcher();

    /**
     * Метод позволяет загрузить значения связанных сущностей.
     * <p/>
     * Формат строки с relationPath:
     * <pre>
     *
     *       Список пересечений: JOIN | (JOIN , JOIN)
     *
     *       JOIN = JOIN_PROPERTY| (JOIN_PROPERTY{JOIN_PROPERTY})
     *       JOIN_PROPERTY = JOIN_SIGN PROPERTY
     *       PROPERTY = ((RELATION_PROPERTY.PROPERTY_NAME) | SIMPLE_PROPERTY)
     *       JOIN_SIGN  = (&) | (!) | ()
     *       RELATION_PROPERTY - имя поля для связанной сущности
     *       SIMPLE_PROPERTY  - имя поля сущности
     *
     * </pre>
     * <p/>
     * Пример:
     * <pre>
     *     "entityA,entityB,entityC{!entityE{&entityF}}
     * </pre>
     * <p/>
     * Обозначния для типов пересечения:
     * <ul>
     * <li>& - внешнее пресечение слева(LEFT JOIN)</li>
     * <li>! - внутренне пресечение (INNER JOIN)</li>
     * <li>(empty) - связь выбирается отдельным запросом(стратегия FETCH_JOIN, аналог {@link ru.kwanza.dbtool.orm.api.IEntityManager#fetch(Class, java.util.Collection, String)})
     * </li>
     * </ul>
     * <p/>
     * Таким образом можно гибко настроить способа выбора связанных сущностей.<br/>
     * Например:
     * <pre>{@code
     *     Collection<TestEntity> items = ...;
     *     em.fetch(TestEntity.class,items,"entityA{!entityB,entityC},entityD{&entityE{&entityF}}")
     * }
     * </pre>
     * В приведенном примере будут выполнены следующие запросы:
     * <ol>
     * <li>выбраны связи <i>TestEntity.entityA</i> и <i>TestEntity.entityA.entityB</i> с помощью запроса
     * <pre>
     *
     *  {@code
     *      SELECT * FROM entity_A INNER JOIN entity_B ON entityA.id=entity_B.entity_a_id WHERE entity_a.id in(?)
     *   }
     *
     *  </pre></li>
     * <li>выбраны связи <i>TestEntity.entityA.entityC</i> и <i>TestEntity.entityA.entityB</i> с помощью запроса
     * <pre>
     *
     *   {@code
     *         SELECT * FROM entity_C WHERE entity_c.id in(?)  }
     *
     * </pre></li>
     * <li>выбраны связи <i>TestEntity.entityD</i>, <i>TestEntity.entityD.entityE</i>, <i>TestEntity.entityD.entityE.entityF</i> с помощью запроса
     * <pre>
     *
     *   {@code
     *         SELECT * FROM entity_D  LEFT JOIN entity_E ON entity_d.id=entity_E.entity_d_id  LEFT JOIN entity_F ON entity_E.id-entity_F.entity_e_id WHERE entity_d.id in(?)}
     *
     * </pre></li>
     * </ol>
     *
     * Следует отметить , что данный метод работает не только для сущностей orm, помеченных
     * с помощью аннатаций {@link ru.kwanza.dbtool.orm.annotations.Entity} и {@link ru.kwanza.dbtool.orm.annotations.AbstractEntity}, но и для
     * plain java object, у которых есть поля с аннотациями {@link ru.kwanza.dbtool.orm.annotations.Association},{@link ru.kwanza.dbtool.orm.annotations.ManyToOne}
     *
     * @param entityClass  тип сущности
     * @param items        коллекция объектов, для которых загружаются связи
     * @param relationPath список и пути связанных сущностей
     * @see ru.kwanza.dbtool.orm.api.IQueryBuilder#join(String)
     */
    <T> void fetch(Class<T> entityClass, Collection<T> items, String relationPath);

    /**
     * Выбор из базы связанных обхектов
     *
     * @param object       объект
     * @param relationPath список и пути связанных сущностей
     * @see #fetch(Class, java.util.Collection, String)
     */
    <T> void fetch(T object, String relationPath);

    /**
     * Поменить все связанные сущности вниз по иерархии для разгрузки On-demand.
     * <p/>
     * Данный метод позволяет реализовать lazy-load стратегию.
     * Все поля связи будут инициализированы специальными обхектами прокси, при обращении к методам которых,
     * реально выполняется обращение к базе данных, при этом загрузка данныз будет осуществялться пакетно, исходя из того списка обхектов, который был передан первоначально
     * <p/>
     * Отличительной особенностью прокси-объектов(по сравненею например с Hibernate):
     * <ol>
     * <li>они сериализуемы</li>
     * <li>загрузка данных не зависит от transaction scope</li>
     * </ol>
     * <p/>
     * <p/>
     * При работе со связанными сущнотями желательно учитывать возможность наличия "сломанных" ссылок: т.е. когда поле с со ссылкой в базе данных не равно Null - но сам связанный объект отсутствует.
     * Поэтому для проверки объекта на Null желательно использовать {@link #isNull(Object)}
     * <p/>
     * Следует отметить , что данный метод работает не только для сущностей orm, помеченных
     * с помощью аннатаций {@link ru.kwanza.dbtool.orm.annotations.Entity} и {@link ru.kwanza.dbtool.orm.annotations.AbstractEntity}, но и для
     * plain java object, у которых есть поля с аннотациями {@link ru.kwanza.dbtool.orm.annotations.Association},{@link ru.kwanza.dbtool.orm.annotations.ManyToOne}
     *
     * @param entityClass тип сущности
     * @param items       объекты, в которых сущности будут загружаться on-demand
     */
    <T> void fetchLazy(Class<T> entityClass, Collection<T> items);

    /**
     * Поменить все связанные сущности вниз по иерархии для разгрузки On-demand.
     *
     * @see #fetchLazy(Class, java.util.Collection)
     */
    <T> void fetchLazy(T object);

    /**
     * Проверить, является ли объект динамической прокси, построенной в результате загрузки on-demand
     *
     * @param object объект для проверки
     */
    boolean isProxy(Object object);

    /**
     * "Разоблачить" объект динамической прокси, построенный в результате загрузки on-demand;
     *
     * @param object
     * @param <T>
     * @return
     */
    <T> T unwrapProxy(T object);

    /**
     * Проверить объект на Null.
     * <p/>
     * Это очень важный метод. Его основное предназначение - единообразно обрабатывать ситуации, когда у сущностей, поля связи равны null, но
     * при этом они могли загружаться двуми способави:
     * <ul>
     * <li>по требования - {@link ru.kwanza.dbtool.orm.api.IEntityManager#fetchLazy(Class, java.util.Collection)} или {@link IQueryBuilder#lazy()}</li>
     * <li>зафетчены - {@link ru.kwanza.dbtool.orm.api.IEntityManager#fetch} </li>
     * </ul>
     *
     * @param object
     * @return
     */
    boolean isNull(Object object);

    /**
     * Метод позволяет выполнять advanced блокировки объектов
     *
     * @param type        тип блокировки
     * @param entityClass тип сущности
     * @param items       список сущностей, над которыми нужно выполнить блокировку
     * @return результат блокировки
     * @see ru.kwanza.dbtool.orm.api.LockType
     */

    <T> LockResult<T> lock(LockType type, Class<T> entityClass, Collection<T> items);


    /**
     * Метод позволяет выполнять advanced блокировку объекта
     *
     * @param type тип блокировки
     * @param item список сущностей, над которыми нужно выполнить блокировку
     * @return результат блокировки
     * @see ru.kwanza.dbtool.orm.api.LockType
     */
    <T> LockResult<T> lock(LockType type, T item);
}