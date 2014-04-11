package ru.kwanza.dbtool.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import ru.kwanza.dbtool.core.blob.BlobInputStream;
import ru.kwanza.dbtool.core.blob.BlobOutputStream;
import ru.kwanza.dbtool.core.lock.AppLock;
import ru.kwanza.dbtool.core.util.SelectUtil;
import ru.kwanza.dbtool.core.util.UpdateUtil;
import ru.kwanza.toolbox.fieldhelper.FieldHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Утилита работы с базой данных
 *
 * @author Guzanov Alexander
 */
public class DBTool extends JdbcDaoSupport {

    private static final Logger logger = LoggerFactory.getLogger(DBTool.class);
    private DBType dbType;
    private int dbVersion;

    @Override
    protected void initDao() throws Exception {
        try {
            Connection conn = getJDBCConnection();
            String databaseProductName = conn.getMetaData().getDatabaseProductName();
            if ("Microsoft SQL Server".equalsIgnoreCase(databaseProductName)) {
                dbType = DBType.MSSQL;
            } else if ("Oracle".equalsIgnoreCase(databaseProductName)) {
                dbType = DBType.ORACLE;
            } else if ("MySQL".equalsIgnoreCase(databaseProductName)) {
                dbType = DBType.MYSQL;
            } else if ("PostgreSQL".equalsIgnoreCase(databaseProductName)) {
                dbType = DBType.POSTGRESQL;

            } else {
                dbType = DBType.OTHER;
            }
            dbVersion = conn.getMetaData().getDatabaseMajorVersion();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error in taking the type and version of database", e);
        }
    }

    /**
     * Получить коннекшен к базе данных
     */
    public Connection getJDBCConnection() {
        return new ConnectionWrapper(getDataSource());
    }

    /**
     * Выбрать список из базы данных, с возможностью использования конструкции in(?)
     *
     * @param selectSQL запрос на выборку данных
     * @param rowMapper конструктор объектов из полей
     * @param inValues  параметры запроса
     */
    public <T> List<T> selectList(String selectSQL, RowMapper<T> rowMapper, Object... inValues) {
        return SelectUtil.selectList(getJdbcTemplate(), selectSQL, rowMapper, inValues);
    }

    /**
     * Выбрать список из базы данных с возможностью использовать конструкцию in(?)
     *
     * @param selectSQL запрос на выборку данных
     * @param cls       класс, на который мэпится результат
     * @param inValues
     * @param <T>
     * @return
     */
    public <T> List<T> selectList(String selectSQL, Class<T> cls, Object... inValues) {
        return SelectUtil.selectList(getJdbcTemplate(), selectSQL, cls, inValues);
    }

    /**
     * Выбрать набор элементов из базы данных с возможностью использовать конструкцию in(?)
     *
     * @param selectSQL запрос на выборку данных
     * @param rowMapper констуктор объектов из полей
     * @param inValues  параметры запроса
     */
    public <T> Set<T> selectSet(String selectSQL, RowMapper<T> rowMapper, Object... inValues) {
        return SelectUtil.selectSet(getJdbcTemplate(), selectSQL, rowMapper, inValues);
    }

    /**
     * Выбрать набор элементов из базы данных с возможностью использовать конструкцию in(?)
     *
     * @param selectSQL запрос на выборку данных
     * @param cls       класс на который мэпится результат
     * @param inValues  параметры запроса
     */
    public <T> Set<T> selectSet(String selectSQL, Class<T> cls, Object... inValues) {
        return SelectUtil.selectSet(getJdbcTemplate(), selectSQL, cls, inValues);
    }

    /**
     * Выбрать карту списков объектов с возможностью использовать конструкцию in(?)
     *
     * @param selectSQL запрос на выборку данных
     * @param rowMapper конструктор элементов карты
     * @param inValues  параметры запроса
     */
    public <K, V> Map<K, List<V>> selectMapList(String selectSQL, RowMapper<KeyValue<K, V>> rowMapper, Object... inValues) {
        return SelectUtil.selectMapList(getJdbcTemplate(), selectSQL, rowMapper, inValues);
    }

    /**
     * Выбрать карту карт объектов с возможностью использовать конструкцию in(?)
     *
     * @param selectSQL запрос на выборку данных
     * @param rowMapper конструктор элементов карты
     * @param inValues  параметры запроса
     */
    public <K0, K, V> Map<K0, Map<K, V>> selectMapOfMaps(String selectSQL, RowMapper<KeyValue<K0, KeyValue<K, V>>> rowMapper,
                                                         Object... inValues) {
        return SelectUtil.selectMapOfMaps(getJdbcTemplate(), selectSQL, rowMapper, inValues);
    }

    /**
     * выбрать карту объектов с возможностью использовать конструкцию in(?)
     *
     * @param selectSQL запрос на выборку данных
     * @param rowMapper конструктор элементов карты
     * @param inValues  параметры запроса
     */
    public <K, V> Map<K, V> selectMap(String selectSQL, RowMapper<KeyValue<K, V>> rowMapper, Object... inValues) {
        return SelectUtil.selectMap(getJdbcTemplate(), selectSQL, rowMapper, inValues);
    }

    /**
     * Выполнение пакетной jdbc операции обновления без проверки optimistic locking.
     * <p/>
     * Чаще всего этот метод используется для выполнения INSERT INTO, DELETE.
     *
     * @param updateSQL    запрос на обновление
     * @param objects      список объектов, для которых обновляются записи
     * @param updateSetter установщик значений параметров
     * @throws UpdateException исключение в случае возникновения constraint violation
     */
    public <T> long update(String updateSQL, final Collection<T> objects, final UpdateSetter<T> updateSetter) throws UpdateException {
        return UpdateUtil.batchUpdate(getJdbcTemplate(), updateSQL, objects, updateSetter, dbType);
    }

    /**
     * Выполнение пакетной операции обновления с проверкой optimistic locking
     *
     * @param updateSQL        запрос на обновление
     * @param objects          список объектов, для которых обновляются записи
     * @param updateSetter     установщик значений для параметров запроса обновления
     * @param checkSQL         запрос проверки вверсии
     * @param keyVersionMapper коструктор пары key-version для запроса проверки версии
     * @param keyField         доступ к ключевому полю объектов
     * @param versionField     доступ к полю версии объектов
     * @throws UpdateException исключение в случае возникновения constraint violation или optimistic lock violation.
     */
    public <T, K extends Comparable, V> long update(String updateSQL, Collection<T> objects, UpdateSetterWithVersion<T, V> updateSetter,
                                                    String checkSQL, RowMapper<KeyValue<K, V>> keyVersionMapper,
                                                    FieldHelper.Field<T, K> keyField, FieldHelper.VersionField<T, V> versionField)
            throws UpdateException {
        return UpdateUtil
                .batchUpdate(getJdbcTemplate(), updateSQL, objects, updateSetter, checkSQL, keyVersionMapper, keyField, versionField, dbType);
    }

    /**
     * Получение утилиты для установки блокировки
     * <p/>
     * Поддержка различными версиями баз данных:
     * <table>
     * <thead>
     * <tr>
     * <td>СУБД</td>
     * <td>Поддержка</td>
     * </tr>
     * </thead>
     * <tbody>
     * <tr>
     * <td>Oracle</td>
     * <td>native</td>
     * </tr>
     * <tr>
     * <td>MSSQL</td>
     * <td>native</td>
     * </tr>
     * <tr>
     * <td>MySQL</td>
     * <td>native</td>
     * <td>native</td>
     * </tr>
     * <tr>
     * <td>PosgreSQL</td>
     * <td>реализована через блокировку записи в таблицы dbmutex</td>
     * </tr>
     * </tbody>
     * </table>
     *
     * @param lockName название блокировки
     * @return
     */
    public AppLock getLock(String lockName) {
        return getLock(lockName, true);
    }

    public AppLock getDefaultLock(String lockName) {
        return getDefaultLock(lockName, true);
    }

    /**
     * Получение утилиты для установки блокировки.
     * <p/>
     * Поведение аналогично {@link #getLock(String)}, но дополнительный параметр указывает на возможность вложенного вызова метода
     * {@link ru.kwanza.dbtool.core.lock.AppLock#lock()}
     * <p/>
     * Пример:
     * <pre>{@code
     * <p/>
     * AppLock lock = dbTool.getLock("name1",true);
     * try{
     *     lock.lock();
     *     try{
     *         // do some usefull work
     *     }finally{
     *         lock.close();
     *     }
     * }finally{
     *     lock.close();
     * }
     * }</pre>
     *
     * @param lockName  название блокировки
     * @param reentrant можно ли в одном потоке несколько раз заходить секцию {@link ru.kwanza.dbtool.core.lock.AppLock#lock()}
     */
    public AppLock getLock(String lockName, boolean reentrant) {
        try {
            return AppLock.defineLock(this, lockName, dbType, reentrant);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Дефальтная реализация блокировки, которая использует таблицу <i>dbmutex</i>
     *
     * @param lockName  название блокировки
     * @param reentrant можно ли в одном потоке несколько раз заходить секцию {@link ru.kwanza.dbtool.core.lock.AppLock#lock()}
     */
    public AppLock getDefaultLock(String lockName, boolean reentrant) {
        try {
            return AppLock.defineLock(this, lockName, DBType.OTHER, reentrant);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получить поток умеющий читать данные  из blob полей
     * <p/>
     * Работа с данными из поля производится через буфер в памяти, куда зачитывается только часть данных.
     *
     * @param tableName  имя таблицы
     * @param fieldName  имя blob поля
     * @param conditions условия выбора записи из таблицы
     * @return поток, из которого можно читать содержимое поля
     * @throws IOException
     */
    public BlobInputStream getBlobInputStream(String tableName, String fieldName, Collection<KeyValue<String, Object>> conditions)
            throws IOException {
        return BlobInputStream.create(this, tableName, fieldName, conditions);
    }

    /**
     * Поток умеющий писать данные в блоб поля.
     *
      * Работа с данными из поля производится через буфер в памяти, который периодически скидывается в базу данных.
     *
     * Для того, чтобы иметь возможность изменять сорежимое поле, можно устанавливать текущую позицию с
     * помощью метода {@link ru.kwanza.dbtool.core.blob.BlobOutputStream#setPosition(long)}
     *
     * @param tableName  имя таблицы
     * @param fieldName  имя blob поля
     * @param conditions условия выбора записи из таблицы
     * @return поток, из которого можно читать содержимое поля
     * @return поток, в который можно писать, для изменения содержимого поля
     * @throws IOException
     */
    public BlobOutputStream getBlobOutputStream(String tableName, String fieldName, Collection<KeyValue<String, Object>> conditions)
            throws IOException {
        return BlobOutputStream.create(this, tableName, fieldName, conditions);
    }

    /**
     * Информация о типу СУБД
     */
    public DBType getDbType() {
        return dbType;
    }

    /**
     * Информация о версии СУДБ
     */
    public int getDbVersion() {
        return dbVersion;
    }

    public static enum DBType {
        MSSQL, ORACLE, MYSQL, OTHER, POSTGRESQL
    }

    /**
     * Silent закрытие объектов для работы с базой данных: {@link java.sql.ResultSet},
     * {@link java.sql.Statement}, {@link Connection}, {@link java.sql.PreparedStatement}, {@link ru.kwanza.dbtool.core.lock.AppLock}
     *
     * @param objects список объектов для которых вызывается метод <b>close</b>
     */
    public void closeResources(Object... objects) {
        for (Object o : objects) {
            try {
                if (o != null) {
                    if (o instanceof Connection) {
                        ((Connection) o).close();
                    } else if (o instanceof ResultSet) {
                        ((ResultSet) o).close();
                    } else if (o instanceof Statement) {
                        ((Statement) o).close();
                    } else if (o instanceof AppLock) {
                        ((AppLock) o).close();
                    } else if (o instanceof InputStream) {
                        ((InputStream) o).close();
                    } else if (o instanceof OutputStream) {
                        ((OutputStream) o).close();
                    }
                }
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
