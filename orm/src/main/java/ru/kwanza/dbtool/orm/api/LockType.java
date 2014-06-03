package ru.kwanza.dbtool.orm.api;

/**
 * Типы блокировок сущностей
 * <p/>
 * Эффективность блокировок зависит от СУБД - не все базы данных предоставляют средства для реализациии блокировок.
 * Ниже описана поддержка блокировок для разных СУБД
 * <table>
 * <col width="5%"/>
 * <col width="23%"/>
 * <col width="24%"/>
 * <col width="23%"/>
 * <col width="24%"/>
 * <thead>
 * <tr>
 * <td>СУБД</td>
 * <td>{@link LockType#WAIT}</td>
 * <td>{@link LockType#NOWAIT}</td>
 * <td>{@link LockType#SKIP_LOCKED}</td>
 * <td>{@link LockType#INC_VERSION}</td>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td>Oracle</td>
 * <td>native</td>
 * <td>native</td>
 * <td>native</td>
 * <td>native</td>
 * </tr>
 * <tr>
 * <td>MSSQL</td>
 * <td>native(возможна эскалация блокировок)</td>
 * <td>native(возможна эскалация блокировок)</td>
 * <td>native( возможна эскалация блокировок)</td>
 * <td>native</td>
 * </tr>
 * <tr>
 * <td>MySQL</td>
 * <td>native</td>
 * <td>Реализована через установку таймаута блокировки</td>
 * <td>Реализована через установку таймаута блокировки и выполнения блокировок построчно(неэффекфтивно для больших колекций)</td>
 * <td>native</td>
 * </tr>
 * <tr>
 * <td>PosgreSQL</td>
 * <td>native</td>
 * <td>native</td>
 * <td>Использование дополнительного contrib модуля: admin и функционала Advisory Lock. Учитывае, что Advisory Lock использует для реализации
 * блокировок глобальные bigint идентификаторы работает только для сущностей с целочисленным первичным ключом,
 * глобальный ключ вычисляется hash-функцией по идентификатору  и типу объекта(теоретически возможно коллизии)</td>
 * <td>native</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @author Alexander Guzanov
 * @see IEntityManager#lock(LockType, Class, java.util.Collection)
 * @see IEntityManager#lock(LockType, Object)
 */
public enum LockType {
    /**
     * Блокируем сущности в пессимистическом режиме (SELECT FOT UPDATE)
     * В случае, если сущность уже заблокирована -ожидаем блокировки
     * @see ru.kwanza.dbtool.orm.api.LockType
     */
    WAIT,
    /**
     * Блокируем сущности в пессимистическом режиме (SELECT FOT UPDATE)
     * В случае, если сущность уже заблокирована - возвращемся из метода блокировки без ожидание,
     * т.е блокировка не выполняется
     * @see ru.kwanza.dbtool.orm.api.LockType
     */
    NOWAIT,
    /**
     * Блокируем сущности в пессимистическом режиме (SELECT FOT UPDATE)
     * В случае, если сущность уже заблокирована, пропускаем её и пытыемся блокировать следующую сущность в пачке
     * @see ru.kwanza.dbtool.orm.api.LockType
     */
    SKIP_LOCKED,
    /**
     * Обновление записи с увеличением значения поля версии  (UPDATE SET version=version+1)
     * @see ru.kwanza.dbtool.orm.api.LockType
     */
    INC_VERSION,
}
