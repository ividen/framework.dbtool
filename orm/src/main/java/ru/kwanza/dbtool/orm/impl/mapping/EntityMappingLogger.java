package ru.kwanza.dbtool.orm.impl.mapping;

import org.slf4j.Logger;

/**
 * @author Kiryl Karatsetski
 */
public class EntityMappingLogger {

    static void logRegisterEntity(Logger log, Class entityClass, String entityName, String tableName) {
        log.debug("Register entity '{}' with '{}' name and '{}' table name", new Object[]{entityClass, entityName, tableName});
    }

    static void logRegisterColumn(Logger log, Class entityClass, String columnName) {
        log.debug("{}: Register column '{}'", new Object[]{entityClass, columnName});
    }

    static void logRegisterFieldMapping(Logger log, Class entityClass, FieldMapping fieldMapping) {
        log.debug("{}: Register Field Mapping {}", new Object[]{entityClass, fieldMapping});
    }

    static void logRegisterFetchMapping(Logger log, Class entityClass, FetchMapping fetchMapping) {
        log.debug("{}: Register Fetch Mapping {}", new Object[]{entityClass, fetchMapping});
    }
}
