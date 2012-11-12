package ru.kwanza.dbtool.core.blob;

/**
 * @author Ivan Baluk
 */
enum DatabaseProductName {

    UNKNOWN(""),
    MS_SQL_SERVER("Microsoft SQL Server"),
    ORACLE("Oracle");

    private final String name;

    DatabaseProductName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
