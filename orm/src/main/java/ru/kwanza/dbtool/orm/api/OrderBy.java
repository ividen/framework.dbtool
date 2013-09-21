package ru.kwanza.dbtool.orm.api;

/**
 * @author Alexander Guzanov
 */
public class OrderBy {
    public static final String ASC = "ASC";
    public static final String DESC = "DESC";
    private String propertyName;
    private String type;

    private OrderBy(String propertyName, String type) {
        this.propertyName = propertyName;
        this.type = type;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getType() {
        return type;
    }

    public static OrderBy ASC(String propertyName) {
        return new OrderBy(propertyName, ASC);
    }

    public static OrderBy DESC(String propertyName) {
        return new OrderBy(propertyName, DESC);
    }

}
