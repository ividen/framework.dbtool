package ru.kwanza.dbtool.orm.api;

/**
 * Объект, который хранит в себе сконструированный с помощью {@link ru.kwanza.dbtool.orm.api.IQueryBuilder} запрос.
 * <p/>
 * Внутри объект содержит достаточно много служебной информации, необходимой для выолнения запроса(ов).
 * С точки зрения использования, если есть возможность  - то лучше кэшировать этот запрос:
 * в такой идеалогии его использование будет аналогично NamedQuery в jpa.
 * <p/>
 * C точки зрения выполнения IQuery может делать несколько sql запросов к базе данных,
 * если при его построении использовался {@link ru.kwanza.dbtool.orm.api.Join.Type#FETCH}-стратегия для выборки данных связанных сущностей.
 * <p/>
 * Например:
 * <p/>
 * <pre>{@code
 * IQuery<TestEntity> q = em.queryBuilder(TestEntity.class).join(Join.fetch("entityA",Join.inner("entityB",Join.fetch("entityC")).create();
 * <p/>
 * List<TestEntity> result = q.prepare().selectList();
 * }</pre>
 * <p/>
 * Выполнение этого запроса будет осуществлятеься следующим образом
 * <p/>
 * <ol>
 * <li>Выполняется запрос {@code SELECT * FROM test_entity}</li>
 * <li>Для выбранных объектов "фетчатся связи" TestEntity.entityA, TestEntity.entityA.entityB с помощью одного запроса {@code SELECT * FROM entity_A INNER JOIN entity_B ON ... WHERE entity_A.ID in(...) }</li>
 * <li>Следующий запрос "дофетчивает" связь TestEntity.entityA.entityB.entityC  {@code SELECT * FROM entity_C WHERE entity_C.ID in(...) }</li>
 * </ol>
 *
 * @author Alexander Guzanov
 * @see IStatement
 * @see ru.kwanza.dbtool.orm.api.IQueryBuilder
 */
public interface IQuery<T> {

    /**
     * Подготовка запроса к выполнению
     *
     * @return объект "выражение", в который можно указывать параметры запроса и выполнять
     * @see ru.kwanza.dbtool.orm.api.IStatement
     * @see ru.kwanza.dbtool.orm.api.ISelectOperationProvider
     */
    IStatement<T> prepare();
}
