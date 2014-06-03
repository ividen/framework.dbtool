package ru.kwanza.dbtool.orm.api;

/**
 * Объект "Выражение", который позволяет выполнять запрос.
 * <p/>
 * Методы описаны таким образом, что позволяют строить цепочки:
 * <pre>
 * {@code IQuery<TestEntity> q = em.queryBilder(TestEntity.class).create();
 *
 *   List<TestEntity> result = q.prepare()
 *                              .paging(200,100)
 *                              .setParameter(1, new Date())
 *                              .setParameter(2, "Agent%")).selectList();
 *  }</pre>
 *
 * @author Alexander Guzanov
 * @see ru.kwanza.dbtool.orm.api.IQuery#prepare()
 */
public interface IStatement<T> extends ISelectOperationProvider<T> {

    /**
     * Добавление пагинации в запрос.
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
     */
    IStatement<T> paging(int offset, int maxSize);

    /**
     * Установка значения параметра.
     * <p/>
     * Параметры интексируются начиная с 1.
     * Метод можно использовать так же и для именнованных параметров, зная их порядковый номер в запросе
     *
     * @param index индекс параметра.
     * @param value значение прараметра
     */
    IStatement<T> setParameter(int index, Object value);

    /**
     * Установка значения именнованного параметра
     *
     * @param name  имя параметра
     * @param value значение параметра
     * @see If#isEqual(String, String)
     * @see If#notEqual(String, String) (String, String)
     * @see If#like(String, String)
     * @see If#isLess(String, String)
     * @see If#isLessOrEqual(String, String)
     * @see If#isGreater(String, String)
     * @see If#isGreaterOrEqual(String, String)
     * @see If#in(String, String)
     *
     */
    IStatement<T> setParameter(String name, Object value);
}
