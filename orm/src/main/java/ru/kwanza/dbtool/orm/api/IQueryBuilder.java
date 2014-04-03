package ru.kwanza.dbtool.orm.api;

/**
 * Построитель запросов.
 * Реализован с помощью шаблона проектирования Builder.
 * <p/>
 * Перед созданием запроса вызываются методы правил формирования запроса:
 * <ul>
 * <li><B>join</B></li>
 * <li><B>where</B></li>
 * <li><B>orderBy</B></li>
 * <li><B>lazy</B></li>
 * </ul>
 * <p/>
 * После этого запрос создается с помощью метода {@link IQueryBuilder#create()}
 * <p/>
 * <p/>
 * Пример:
 * <pre>
 *  {@code
 * IQuery<TestEntity> q = em.queryBuilder(TestEntity.class)
 *                          .join("!entityA, !entityB")
 *                          .join("!entityC {&entityE{&entityG},!entityF}")
 *                          .where(If.or(
 *                                       If.and(
 *                                              If.isEqual("intField"),
 *                                              If.between("dateField")
 *                                              ),
 *                                        If.and(
 *                                              If.isLess("intField"),
 *                                              If.not(If.between("dateField")
 *                                              )
 *                                       )
 *                                 )).orderBy("entityA.title DESC").create();
 *
 *
 *   }
 * </pre>
 * <p/>
 * что эквивалентно запросу
 * <pre>
 *  {@code
 * SELECT *
 * FROM test_entity INNER JOIN test_entity_a t1 ON test_entity.entity_aid=t1.id INNER JOIN test_entity_b t2
 *    ON test_entity.entity_bid=t2.id INNER JOIN (test_entity_c t3 INNER JOIN test_entity_f t6 ON t3.entity_fid=t6.id
 *    LEFT JOIN (test_entity_e t4 LEFT JOIN test_entity_g t5 ON t4.entity_gid =t5.id )  ON t3.entity_eid =t4.id )
 *    ON test_entity.entity_cid=t3.id
 * WHERE ((test_entity.int_field = ?) AND (test_entity.date_field BETWEEN ? AND ?))  OR ((test_entity.int_field < ?)
 * AND (NOT (test_entity.date_field BETWEEN ? AND ?)))
 * ORDER BY t1.title DESC
 *   }
 * </pre>
 * <p/>
 * <p/>
 * Если нужно построить очень сложный запрос, или на диалекте конктретной СУБД, можно поспользоваться методом {@link ru.kwanza.dbtool.orm.api.IQueryBuilder#createNative(String)} -
 * в этом случае {@link ru.kwanza.dbtool.orm.api.IQuery} по сути предстваляет собой просто мэппинг колонок запроса на поля сущности
 * <p/>
 * <p/>
 * Одним из преимуществ использования IQueryBuilder является возможность гибко настроить правила выборки связанных сущностей {@link #join(String)}.
 * Фактически выполнение  {@link IQuery} может выполнение нескольких sql-запросов
 * Пример:
 * <pre>
 *  {@code
 *
 *
 *   IQuery<TestEntity> q = em.queryBuilder(TestEntity.class)
 *                          .join("!entityC {entityE{!entityG}}").create();
 *
 *   }
 * </pre>
 * <p/>
 * для данного запроса будет выполнено следующее
 * <p/>
 * 1. TestEntity выбирается вместе с EntityC
 * <pre>
 *  {@code SELECT * FROM test_entity_c INNER JOIN entity_C ...
 *  }
 * </pre>
 * 2. Фетчатся дополнительные связи: testEntity.entityC.entityE и testEntity.entityC.entityE.entityF одним запросом
 * <pre>
 *  {@code SELECT * FROM entity_E INNER JOIN entityG ...
 *   }
 * </pre>
 * <p/>
 * Нужно учитывать, что правила персечения могут добавляться неявно, если sql-предикат или сортировка указана по полю связанной сущности
 * Пример:
 * <pre>
 *  {@code
 *
 *   IQuery<TestEntity> q = em.queryBuilder(TestEntity.class)
 *                          .where(If.isEqual("entityA.title")
 *                          .orderBy("entityB.title".create();*
 *   }
 * </pre>
 * <p/>
 * Для этого запроса будут автоматически подгружены связи entityA и entityB и выполнен запрос:
 * <pre>
 *  {@code
 * SELECT * FROM test_entity inner join entity_a on...
 *              inner join on entity_b on ...
 * WHERE entity_a.title=?
 * ORDER BY entity_b
 *   }
 * </pre>
 * <p/>
 * Если запрос констуировался с помощью {@link IQueryBuilder#lazy()}, то для всех связей, которые не попали в {@link #join(String)}
 * будет действовать принцип on-demand.
 *
 * @author Alexander Guzanov
 * @see <a href="http://ru.wikipedia.org/wiki/%D0%A1%D1%82%D1%80%D0%BE%D0%B8%D1%82%D0%B5%D0%BB%D1%8C_(%D1%88%D0%B0%D0%B1%D0%BB%D0%BE%D0%BD_%D0%BF%D1%80%D0%BE%D0%B5%D0%BA%D1%82%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D1%8F)">Builder Pattern</a>
 * @see ru.kwanza.dbtool.orm.api.IQuery
 * @see ru.kwanza.dbtool.orm.api.Join
 * @see ru.kwanza.dbtool.orm.api.OrderBy
 * @see ru.kwanza.dbtool.orm.annotations.OneToMany
 * @see ru.kwanza.dbtool.orm.annotations.ManyToOne
 * @see ru.kwanza.dbtool.orm.annotations.Association
 * @see #join(String)
 */
public interface IQueryBuilder<T> {
    /**
     * Создание запроса
     *
     * @see ru.kwanza.dbtool.orm.api.IQuery
     */
    IQuery<T> create();

    /**
     * Создание запроса из нативной sql-строки
     *
     * @param sql
     * @return
     */
    IQuery<T> createNative(String sql);

    /**
     * Добавить пересечение в запрос
     *
     * @param join пресечение
     * @return ссылку на самого себя
     * @see ru.kwanza.dbtool.orm.api.Join
     * @see #join(String)
     * @see ru.kwanza.dbtool.orm.annotations.OneToMany
     * @see ru.kwanza.dbtool.orm.annotations.ManyToOne
     * @see ru.kwanza.dbtool.orm.annotations.Association
     */
    IQueryBuilder<T> join(Join join);

    /**
     * Пометить, что все связанные сущности за исключением тех, которые попали в {@link #join(Join)}
     * будут выбраны on-demand
     *
     * @return ссылку на самого себя
     * @see ru.kwanza.dbtool.orm.api.IEntityManager#fetchLazy(Class, java.util.Collection)
     * @see ru.kwanza.dbtool.orm.api.Join
     * @see ru.kwanza.dbtool.orm.annotations.OneToMany
     * @see ru.kwanza.dbtool.orm.annotations.ManyToOne
     * @see ru.kwanza.dbtool.orm.annotations.Association
     */
    IQueryBuilder<T> lazy();

    /**
     * Добавить пересечения в запрос согласно указанной строке
     * <p/>
     * Формат строки:
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
     *
     * @param string список и пути связанных сущностей, которые нужно выбрать
     * @return ссылку на самого себя
     * @see ru.kwanza.dbtool.orm.annotations.OneToMany
     * @see ru.kwanza.dbtool.orm.annotations.ManyToOne
     * @see ru.kwanza.dbtool.orm.annotations.Association
     * @see <a href="http://en.wikipedia.org/wiki/Join_(SQL)">SQL JOIN</a>
     */
    IQueryBuilder<T> join(String string);

    /**
     * Установить в блок <i>WHERE</i> sql-предикат
     *
     * @param condition sql-предикат
     * @return ссылку на самого себя                                  l
     */
    IQueryBuilder<T> where(If condition);

    /**
     * Добавить сортировку поля
     *
     * @param orderByClause строка с описание типа сортировки и поля
     * @return ссылку на самого себя
     */
    IQueryBuilder<T> orderBy(String orderByClause);

    /**
     * Добавить сортировку поля
     *
     * @param orderBy строка с описание типа сортировки и поля
     * @return ссылку на самого себя
     * @see ru.kwanza.dbtool.orm.api.OrderBy
     */
    IQueryBuilder<T> orderBy(OrderBy orderBy);
}
