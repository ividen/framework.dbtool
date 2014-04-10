package ru.kwanza.dbtool.orm.api;

/**
 * Объект предназначенный для построения динамических запросов на основе условий фильтрации.
 * <p/>
 * Очень удобно использовать в ui-списках в которых есть возможность выполнять фильтрацию по различным полям.
 * В отличии от {@link ru.kwanza.dbtool.orm.api.IQuery}, утилита строит и сразу выполняте запрос.
 * Методы {@link #filter} - позволяют добавлять предикаты в запрос и значения к параметрам, при этом эти предикаты в конечном запросе связаны через коньюнкцию.
 *
 * Пример:
 * <pre>{@code
 * public List<PaymentMgtTrx> getList(
 *              @literal @RequestParam(value = "id", required = false) Long id,
 *               @literal @RequestParam(value = "start", required = false) Integer start,
 *               @literal @RequestParam(value = "limit", required = false) Integer limit,
 *               @literal @RequestParam(value = "filter.serialCardNumber", required = false) String serialCardNumber,
 *               @literal @RequestParam(value = "filter.ticketNumber", required = false) String ticketNumber,
 *               @literal @RequestParam(value = "filter.productTypeCode", required = false) Integer productTypeCode,
 *               @literal @RequestParam(value = "filter.entryDate.from", required = false) Date entryDateFrom,
 *               @literal @RequestParam(value = "filter.entryDate.to", required = false) Date entryDateTo,
 *               @literal @RequestParam(value = "filter.entryPlaceCode", required = false) Integer entryPlaceCode,
 *               @literal @RequestParam(value = "filter.registryId", required = false) Long registryId,
 *               @literal @RequestParam(value = "filter.agentId", required = false) Long agentId,
 *               @literal @RequestParam(value = "filter.fileName", required = false) String fileName,
 *               @literal @RequestParam(value = "filter.logicCheckStateCode", required = false) Integer logicCheckStateCode) {
 *
 *   List<PaymentMgtTrx> result = manager.filtering(PaymentMgtTrx.class)
 *                                    .paging(start,limit)
 *                                    .join("agent{!agentType},registry}")
 *                                    .filter((id != null, If.isEqual("id"), id),
 *                                    .filer(serialCardNumber != null, If.like("serialCardNumber"), serialCardNumber != null ? serialCardNumber.toUpperCase() + "%" : null),
 *                                    .filer(ticketNumber != null, If.like("ticketNumber"), ticketNumber != null ? ticketNumber + "%" : null),
 *                                    .filer(productTypeCode != null, If.isEqual("productTypeCode"), productTypeCode),
 *                                    .filer(entryDateFrom != null, If.isGreaterOrEqual("entryDate"), entryDateFrom),
 *                                    .filer(entryDateTo != null, If.isLessOrEqual("entryDate"), entryDateTo),
 *                                    .filer(entryPlaceCode != null, If.isEqual("entryPlaceCode"), entryPlaceCode),
 *                                    .filer(registryId != null, If.isEqual("registryId"), registryId),
 *                                    .filer(agentId != null, If.isEqual("agentId"), agentId),
 *                                    .filer(fileName != null, If.createNative("registry_id in (select id from registry where name like ?)"), fileName),
 *                                    .filer(logicCheckStateCode != null, If.isEqual("logicCheckStateCode"), logicCheckStateCode)
 *                                    .orderBy("entryDate DESC")
 *                                    .selectList();
 *
 *    return result;
 * }
 * }</pre>
 * 
 *
 *
 * @author Alexander Guzanov
 * @see ru.kwanza.dbtool.orm.api.ISelectOperationProvider
 * @see ru.kwanza.dbtool.orm.api.IQueryBuilder
 */
public interface IFiltering<T> extends ISelectOperationProvider<T> {

    /**
     * Пагинация
     * <p/>
     * Смысл параметров метода
     * <pre>
     * +-----------------------------+
     * +   1              +   skip   +
     * +------------------+----------+
     * +   2              +   skip   +
     * +------------------+----------+
     * ..............         skip
     * +------------------+----------+
     * + offset-1         +   skip   +
     * +------------------+----------+
     * + offset           +   GET    +
     * +------------------+----------+
     * ..............         GET    +
     * +------------------+----------+
     * + offset+maxSize+1 +   GET    +
     * +------------------+----------+
     * + offset+maxSize+2 +   skip   +
     * +------------------+----------+
     * ..............         skip
     *
     * </pre>
     * <p/>
     * При выполнении запроса используются database specific контрукции, которые позволяют делать пагинацию
     *
     * @param offset  количество объектов, которые нужно "пропустить", не выбирать в результат
     * @param maxSize максимальное количество выбираемых элементов
     * @see ru.kwanza.dbtool.orm.api.IFiltering
     */
    IFiltering<T> paging(Integer offset, Integer maxSize);

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
     * @see ru.kwanza.dbtool.orm.api.IQuery
     * @see ru.kwanza.dbtool.orm.api.Join
     * @see ru.kwanza.dbtool.orm.api.IQueryBuilder#join(String)
     * @see ru.kwanza.dbtool.orm.api.IFiltering
     */
    IFiltering<T> join(String join);

    /**
     * Добавить пересечения в запрос согласно указанной строке
     * <p/>
     *
     * @see ru.kwanza.dbtool.orm.api.IQuery
     * @see ru.kwanza.dbtool.orm.api.Join
     * @see ru.kwanza.dbtool.orm.api.IQueryBuilder#join(String)
     * @see ru.kwanza.dbtool.orm.api.IFiltering
     */
    IFiltering<T> join(Join join);

    /**
     * Добавить пересечения в запрос, если <b>use</b>==true
     *
     * @param use  использовать ли пересечение
     * @param join строка описывающая пути и вложенность сущностей
     * @see #join(String)
     * @see ru.kwanza.dbtool.orm.api.IFiltering
     */
    IFiltering<T> join(boolean use, String join);

    /**
     * Добавить пересечения в запрос, если <b>use</b>==true
     *
     * @param use  использовать ли пересечение
     * @param join сущность для пересечения
     * @see #join(String)
     * @see ru.kwanza.dbtool.orm.api.IFiltering
     */
    IFiltering<T> join(boolean use, Join join);

    /**
     * Добавить фильтр
     *
     * @param use       применять ли предикат
     * @param condition предикат
     * @param params    параметры, которые будет подставлены в предикат
     * @see ru.kwanza.dbtool.orm.api.Filter
     * @see ru.kwanza.dbtool.orm.api.If
     * @see ru.kwanza.dbtool.orm.api.IFiltering
     */
    IFiltering<T> filter(boolean use, If condition, Object... params);

    /**
     * Добавить фильтр
     *
     * @param condition предикат
     * @param params    парамтеры предиката
     * @see ru.kwanza.dbtool.orm.api.If
     */
    IFiltering<T> filter(If condition, Object... params);

    /**
     * Добавить список фильтров
     *
     * @param filters список фильтров
     * @see ru.kwanza.dbtool.orm.api.Filter
     * @see ru.kwanza.dbtool.orm.api.IFiltering
     */
    IFiltering<T> filter(Filter... filters);

    /**
     * Добавить условие по сортировке.
     * <p/>
     * Фортмат строки
     * <pre>
     *    Сортировка: (ORDER_CLAUSE,ORDER_CLAUSE) | ORDER_CLAUSE
     *    ORDER_CLAUSE =  (RELATED_ENTITY.FIELD ORDER_SIGN) | (FIELD ORDER_SIGN)
     *    ORDER_SIGN= ASC | DESC
     *    RELATED_ENTITY = RELATION_FIELD| (RELATION_FIELD.RELATED_ENTITY)
     *    FIELD - поле сущности
     *    RELATION_FIELD - поле сущности описывающее связь
     *
     * </pre>
     *
     * @param orderByClause условие по сортировке
     * @see ru.kwanza.dbtool.orm.api.OrderBy
     * @see ru.kwanza.dbtool.orm.api.IFiltering
     */
    IFiltering<T> orderBy(String orderByClause);

    /**
     * Добавить сортировку
     *
     * @param orderBy описание сортировки
     * @see ru.kwanza.dbtool.orm.api.OrderBy
     */
    IFiltering<T> orderBy(OrderBy orderBy);

    /**
     * Добавить сортировку, если выполняется условие
     *
     * @param use           использовать ли сортировку
     * @param orderByClause список полей для сортировки
     * @see #orderBy(String)
     * @see ru.kwanza.dbtool.orm.api.IFiltering
     */
    IFiltering<T> orderBy(boolean use, String orderByClause);

    /**
     * Добавить сортировку, если выполняется условие
     *
     * @param use     использовать ли сортировку
     * @param orderBy описание сортировки
     * @see #orderBy(String)
     * @see ru.kwanza.dbtool.orm.api.OrderBy
     * @see ru.kwanza.dbtool.orm.api.IFiltering
     */
    IFiltering<T> orderBy(boolean use, OrderBy orderBy);
}
