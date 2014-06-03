package ru.kwanza.dbtool.orm.api;

import java.util.Collection;

/**
 * Описание sql-предиката.<br/>
 * <p/>
 * Конструируется с помощью статических методов, описывающих тип условия
 * <ul>
 * <li><b>isLess</b> : &lt; </li>
 * <li><b>isLessOrEqual</b> : &lt;= </li>
 * <li><b>isEqual</b> : = </li>
 * <li><b>notEqual</b> : &lt;&gt; </li>
 * <li><b>isNull</b> : IS NULL </li>
 * <li><b>isNotNull</b> : IS NOT NULL </li>
 * <li><b>isGreater</b> : &gt </li>
 * <li><b>isGreaterOrEqual</b> : &gt= </li>
 * </ul>
 * <p/>
 * При конструировании условия указывается имя свойства по которому оно ставится.
 * <p/>
 * <pre>
 *  {@code
 *    IQuery<TestEntity> q = em.queryBuilder(TestEntity.class).where(If.isLess("fieldName")).create()
 *   }
 * </pre>
 * <p/>
 * что эквивалентно запросу
 * <pre>
 *  {@code
 *    SELECT * FROM test_entity WHERE field_name=?
 *   }
 * </pre>
 * <p/>
 * Условия может ставиться по вложенным сущностям(уровень вложенности произвольный).
 * В этом случае эти сущности  будут добавляны в пересечение и при выполнении запроса, соотвествующее поля связи будет заполнены
 * <p/>
 * <pre>
 *  {@code
 *     IQuery<TestEntity> q = em.queryBuilder(TestEntity.class).where(If.isEqual("type.code")).create()
 *   }
 * </pre>
 * <p/>
 * что эквивалентно запросу
 * <pre>
 *  {@code
 *      SELECT * FROM  test_entity INNER JOIN type on test_entity.type_id=type.id WHERE type.code=?
 *   }
 * </pre>
 * <p/>
 * <p/>
 * При конструировании запроса может возникнуть необходимость установить сразу значение параметра, с которым он выполнятеся.
 * <pre>
 *  {@code
 *     IQuery<TestEntity> q = em.queryBuilder(TestEntity.class).where(If.isLess("fieldName"),If.valueOf(10))).create()
 *   }
 * </pre>
 * <p/>
 * В этом случае тоже конструируется pre-compiled запрос, но при выполнении всегда автоматически устанавливается значенияе парамтера 10
 * <pre>
 *  {@code
 *      SELECT * FROM  test_entity  WHERE field_name=?
 *   }
 * </pre>
 * <p/>
 * Для простроения запросов состоящих из нескольких предикатов используются статические методы
 * <ul>
 * <li><b>and</b></li>
 * <li><b>or</b></li>
 * <li><b>not</b></li>
 * </ul>
 * <p/>
 * Пример:
 * <pre>
 *  {@code
 *     IQuery<TestEntity> q = em.queryBuilder(TestEntity.class)
 *                      .where(
 *                         If.or(
 *                             If.and(
 *                                   If.isEqual("intField"),
 *                                   If.isEqual("longField"),
 *                                   If.isBetween("dateField))
 *                                   ),
 *                             If.and(
 *                                   If.isLess("intField"),
 *                                   If.isGreater("longField"),
 *                                   If.not(If.isBetween("dateField))
 *                                   )
 *                                 )).create()
 *   }
 * </pre>
 * <p/>
 * что эквивалентно запросу
 * <pre>
 *  {@code
 *   SELECT * FROM  test_entity WHERE (int_field=? AND long_field=? AND (date_field between ? and ?)) OR (int_field&lt;? AND long_field&gt;? AND NOT (date_field between ? and ?))
 *   }
 * </pre>
 * <p/>
 * Если при построении запросов с sql-предикатами предполагается что парамтер может учавствовать в нескольких позициях,
 * или если параметров много, то удобно пользоаться методами, которые при конструировании sql-предиката указывают имя параметра,
 * который будет использоваться
 * <p/>
 * Пример:
 * <pre>
 *  {@code
 *     IQuery<TestEntity> q = em.queryBuilder(TestEntity.class)
 *                      .where(If.and (If.isEqual("intField","param1"),
 *                                     If.isEqual("longField","param1"))).create();
 *
 *     q.prepare().setParameter("param1",1).selectList();
 *   }
 * </pre>
 * <p/>
 * что эквивалентно запросу
 * <pre>
 *  {@code
 *   SELECT * FROM  test_entity WHERE (int_field=:param1 AND long_field=:param1)
 *   }
 * </pre>
 * <p/>
 * В некоторых случаях сложные запросы можно построить используя статический метод <b>createNative</b>
 * Пример:
 * <pre>
 *  {@code
 *     IQuery<TestEntity> q = em.queryBuilder(TestEntity.class)
 *                      .where(If.createNative("type_id IN (SELECT id FROM type WHERE code like :code").create();
 *
 *     q.prepare().setParameter("code",1).selectList();
 *   }
 * </pre>
 *
 * @author Alexander Guzanov
 * @see ru.kwanza.dbtool.orm.api.IQueryBuilder
 * @see ru.kwanza.dbtool.orm.api.IQuery
 * @see ru.kwanza.dbtool.orm.api.IStatement
 * @see ru.kwanza.dbtool.orm.api.IStatement#setParameter(String, Object)
 */
public class If {
    /**
     * Тип sql-предиката
     *
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public enum Type {
        IS_EQUAL,
        NOT_EQUAL,
        IS_GREATER,
        IS_LESS,
        IS_GREATER_OR_EQUAL,
        IS_LESS_OR_EQUAL,
        IS_NULL,
        IS_NOT_NULL,
        BETWEEN,
        IN,
        LIKE,
        AND,
        OR,
        NOT,
        NATIVE;
    }

    private final String propertyName;
    private final String sql;
    private final String paramName;
    private final Type type;
    private final If[] childs;
    private final Object value;

    /**
     * Класс используется для передачи в sql-предикате значения параметра по умолчанию
     * Для конструирования нужно использовать статический метод
     *
     * @param <T> - значение параметра
     * @see ru.kwanza.dbtool.orm.api.If#valueOf
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public static class Value<T> {
        private T obj;

        private Value(T obj) {
            this.obj = obj;
        }
    }

    private If(String propertyName, String sql, Type type, If[] childs, String paramName, Object value) {
        this.sql = sql;
        this.propertyName = propertyName;
        this.paramName = paramName;
        this.type = type;
        this.childs = childs;
        this.value = value;
    }

    /**
     * Конструирования значения по-умолчанию для sql-предиката
     *
     * @param value значение
     * @param <T>   тип значения
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public static <T> Value<T> valueOf(T value) {
        return new Value(value);
    }

    /**
     * @return имя свойств, для которого строится предикат
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * @return значения по умолчанию для предиката
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public Object getValue() {
        return value;
    }

    /**
     * @return sql-строка для "нативного" предиката
     * @see ru.kwanza.dbtool.orm.api.If#createNative(String)
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public String getSql() {
        return sql;
    }

    /**
     * @return имя параметра
     */
    public String getParamName() {
        return paramName;
    }

    /**
     * @return тип предиката
     */
    public Type getType() {
        return type;
    }

    /**
     * @return вложенные условия
     */
    public If[] getChilds() {
        return childs;
    }

    private If(Type type, If[] childs) {
        this(null, null, type, childs, null, null);
    }

    /**
     * Конструирования sql-предиката: =
     *
     * @param property имя свойства сущности
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public static If isEqual(String property) {
        return new If(property, null, Type.IS_EQUAL, null, null, null);
    }

    /**
     * Конструирования sql-предиката c именнованым параметром: =
     *
     * @param property  имя свойства сущности
     * @param paramName имя параметра
     * @see ru.kwanza.dbtool.orm.api.If
     * @see ru.kwanza.dbtool.orm.api.IStatement#setParameter(String, Object)
     */
    public static If isEqual(String property, String paramName) {
        return new If(property, null, Type.IS_EQUAL, null, paramName, null);
    }

    /**
     * Конструирования sql-предиката со значением по-умолчанию: =
     *
     * @param property имя свойства сущности
     * @param value    значение по умолчанию
     * @see ru.kwanza.dbtool.orm.api.If
     * @see ru.kwanza.dbtool.orm.api.If#valueOf
     */
    public static If isEqual(String property, Value value) {
        return new If(property, null, Type.IS_EQUAL, null, null, value.obj);
    }


    /**
     * Конструирования sql-предиката: &lt;&gt;
     *
     * @param property имя свойства сущности
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public static If notEqual(String property) {
        return new If(property, null, Type.NOT_EQUAL, null, null, null);
    }

    /**
     * Конструирования sql-предиката c именнованым параметром : &lt;&gt;
     *
     * @param property  имя свойства сущности
     * @param paramName имя параметра
     * @see ru.kwanza.dbtool.orm.api.If
     * @see ru.kwanza.dbtool.orm.api.IStatement#setParameter(String, Object)
     */
    public static If notEqual(String property, String paramName) {
        return new If(property, null, Type.NOT_EQUAL, null, paramName, null);
    }

    /**
     * Конструирования sql-предиката со значением по-умолчанию: &lt;&gt;
     *
     * @param property имя свойства сущности
     * @param value    значение по умолчанию
     * @see ru.kwanza.dbtool.orm.api.If
     * @see ru.kwanza.dbtool.orm.api.If#valueOf
     */
    public static If notEqual(String property, Value value) {
        return new If(property, null, Type.NOT_EQUAL, null, null, value.obj);
    }

    /**
     * Конструирования sql-предиката: &gt;
     *
     * @param property имя свойства сущности
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public static If isGreater(String property) {
        return new If(property, null, Type.IS_GREATER, null, null, null);
    }

    /**
     * Конструирования sql-предиката c именнованым параметром : &gt;
     *
     * @param property  имя свойства сущности
     * @param paramName имя параметра
     * @see ru.kwanza.dbtool.orm.api.If
     * @see ru.kwanza.dbtool.orm.api.IStatement#setParameter(String, Object)
     */
    public static If isGreater(String property, String paramName) {
        return new If(property, null, Type.IS_GREATER, null, paramName, null);
    }

    /**
     * Конструирования sql-предиката со значением по-умолчанию: &gt;
     *
     * @param property имя свойства сущности
     * @param value    значение по умолчанию
     * @see ru.kwanza.dbtool.orm.api.If
     * @see ru.kwanza.dbtool.orm.api.If#valueOf
     */
    public static If isGreater(String property, Value value) {
        return new If(property, null, Type.IS_GREATER, null, null, value.obj);
    }

    /**
     * Конструирования sql-предиката: &lt;
     *
     * @param property имя свойства сущности
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public static If isLess(String property) {
        return new If(property, null, Type.IS_LESS, null, null, null);
    }

    /**
     * Конструирования sql-предиката c именнованым параметром : &lt;
     *
     * @param property  имя свойства сущности
     * @param paramName имя параметра
     * @see ru.kwanza.dbtool.orm.api.If
     * @see ru.kwanza.dbtool.orm.api.IStatement#setParameter(String, Object)
     */
    public static If isLess(String property, String paramName) {
        return new If(property, null, Type.IS_LESS, null, paramName, null);
    }

    /**
     * Конструирования sql-предиката со значением по-умолчанию: &lt;
     *
     * @param property имя свойства сущности
     * @param value    значение по умолчанию
     * @see ru.kwanza.dbtool.orm.api.If
     * @see ru.kwanza.dbtool.orm.api.If#valueOf
     */
    public static If isLess(String property, Value value) {
        return new If(property, null, Type.IS_LESS, null, null, value.obj);
    }

    /**
     * Конструирования sql-предиката: &gt;=
     *
     * @param property имя свойства сущности
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public static If isGreaterOrEqual(String property) {
        return new If(property, null, Type.IS_GREATER_OR_EQUAL, null, null, null);
    }

    /**
     * Конструирования sql-предиката c именнованым параметром : &gt;=
     *
     * @param property  имя свойства сущности
     * @param paramName имя параметра
     * @see ru.kwanza.dbtool.orm.api.If
     * @see ru.kwanza.dbtool.orm.api.IStatement#setParameter(String, Object)
     */
    public static If isGreaterOrEqual(String property, String paramName) {
        return new If(property, null, Type.IS_GREATER_OR_EQUAL, null, paramName, null);
    }

    /**
     * Конструирования sql-предиката со значением по-умолчанию: &gt;=
     *
     * @param property имя свойства сущности
     * @param value    значение по умолчанию
     * @see ru.kwanza.dbtool.orm.api.If
     * @see ru.kwanza.dbtool.orm.api.If#valueOf
     */
    public static If isGreaterOrEqual(String property, Value value) {
        return new If(property, null, Type.IS_GREATER_OR_EQUAL, null, null, value.obj);
    }

    /**
     * Конструирования sql-предиката: &lt;=
     *
     * @param property имя свойства сущности
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public static If isLessOrEqual(String property) {
        return new If(property, null, Type.IS_LESS_OR_EQUAL, null, null, null);
    }

    /**
     * Конструирования sql-предиката c именнованым параметром : &lt;=
     *
     * @param property  имя свойства сущности
     * @param paramName имя параметра
     * @see ru.kwanza.dbtool.orm.api.If
     * @see ru.kwanza.dbtool.orm.api.IStatement#setParameter(String, Object)
     */
    public static If isLessOrEqual(String property, String paramName) {
        return new If(property, null, Type.IS_LESS_OR_EQUAL, null, paramName, null);
    }

    /**
     * Конструирования sql-предиката со значением по-умолчанию: &lt;=
     *
     * @param property имя свойства сущности
     * @param value    значение по умолчанию
     * @see ru.kwanza.dbtool.orm.api.If
     * @see ru.kwanza.dbtool.orm.api.If#valueOf
     */
    public static If isLessOrEqual(String property, Value value) {
        return new If(property, null, Type.IS_LESS_OR_EQUAL, null, null, value.obj);
    }

    /**
     * Конструирование sql-предиката : IS NULL
     *
     * @param property имя свойства сущности
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public static If isNull(String property) {
        return new If(property, null, Type.IS_NULL, null, null, null);
    }

    /**
     * Конструирование sql-предиката : IS NOT NULL
     *
     * @param property имя свойства сущности
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public static If isNotNull(String property) {
        return new If(property, null, Type.IS_NOT_NULL, null, null, null);
    }

    /**
     * Конструирование sql-предиката : IN(?)
     *
     * @param property имя свойства сущности
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public static If in(String property) {
        return new If(property, null, Type.IN, null, null, null);
    }

    /**
     * Конструирования sql-предиката c именнованым параметром : IN(?)
     *
     * @param property  имя свойства сущности
     * @param paramName имя параметра
     * @see ru.kwanza.dbtool.orm.api.If
     * @see ru.kwanza.dbtool.orm.api.IStatement#setParameter(String, Object)
     */

    public static If in(String property, String paramName) {
        return new If(property, null, Type.IN, null, paramName, null);
    }

    /**
     * Конструирования sql-предиката со значением по-умолчанию: IN(?)
     *
     * @param property имя свойства сущности
     * @param value    значение по умолчанию
     * @see ru.kwanza.dbtool.orm.api.If
     * @see ru.kwanza.dbtool.orm.api.If#valueOf
     */
    public static If in(String property, Value<? extends Collection> value) {
        return new If(property, null, Type.IN, null, null, value.obj);
    }

    /**
     * Конструирование sql-предиката : LIKE ?
     *
     * @param property имя свойства сущности
     * @see ru.kwanza.dbtool.orm.api.If
     * @see ru.kwanza.dbtool.orm.api.If#valueOf
     */
    public static If like(String property) {
        return new If(property, null, Type.LIKE, null, null, null);
    }

    /**
     * Конструирования sql-предиката c именнованым параметром : LIKE ?
     *
     * @param property  имя свойства сущности
     * @param paramName имя параметра
     * @see ru.kwanza.dbtool.orm.api.If
     * @see ru.kwanza.dbtool.orm.api.IStatement#setParameter(String, Object)
     */

    public static If like(String property, String paramName) {
        return new If(property, null, Type.LIKE, null, paramName, null);
    }

    /**
     * Конструирования sql-предиката со значением по-умолчанию: LIKE ?
     *
     * @param property имя свойства сущности
     * @param value    значение по умолчанию
     * @see ru.kwanza.dbtool.orm.api.If
     * @see ru.kwanza.dbtool.orm.api.If#valueOf
     */
    public static If like(String property, Value value) {
        return new If(property, null, Type.LIKE, null, null, value.obj);
    }

    /**
     * Конструирование sql-предиката : BETWEEN ? AND ?
     *
     * @param property имя свойства сущности
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public static If between(String property) {
        return new If(property, null, Type.BETWEEN, null, null, null);
    }

    /**
     * Конструирование коньюнкции условий: c1 AND c1 AND c3 ...
     *
     * @param conditions список условий
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public static If and(If... conditions) {
        return new If(Type.AND, conditions);
    }

    /**
     * Конструирование дезюнкций условий: c1 OR c1 OR c3 ...
     *
     * @param conditions список условий
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public static If or(If... conditions) {
        return new If(Type.OR, conditions);
    }

    /**
     * Конструирование отрицания: NOT(c)
     *
     * @param conditions список условий
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public static If not(If conditions) {
        return new If(Type.NOT, new If[]{conditions});
    }

    /**
     * Создание "нативного" sql-предиката.
     * <p/>
     * При создании для обозначения параметров можно использовать безымынне и именнованные парамтеры: <b>?</b> и <b>:param</b>
     *
     * @param sql - старока которая будет добавлена в запрос
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public static If createNative(String sql) {
        return new If(null, sql, Type.NATIVE, null, null, null);
    }

}

