package ru.kwanza.dbtool.orm.impl.operation;

import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.annotations.Field;
import ru.kwanza.dbtool.orm.annotations.IdField;
import ru.kwanza.dbtool.orm.annotations.VersionField;

/**
 * @author Kiryl Karatsetski
 */
@Entity(name = "TestEntityVersion", table = "test_table")
public final class TestEntityVersion {

    @IdField( "xkey")
    private Long key;

    @Field( "name")
    private String name;

    @VersionField( "version")
    private Long version;

    public TestEntityVersion() {
    }

    public TestEntityVersion(Long key, String b) {
        this.key = key;
        this.name = b;
        this.version = 0L;
    }

    public TestEntityVersion(Long key, String name, Long version) {
        this.key = key;
        this.name = name;
        this.version = version;
    }

    public TestEntityVersion(Long key, String name, Integer version) {
        this.key = key;
        this.name = name;
        this.version = version.longValue();
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public Long getVersion() {
        return version;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long incrementVersion() {
        return ++version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TestEntityVersion that = (TestEntityVersion) o;

        if (key != that.key) {
            return false;
        }
        if (version != that.version) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        Long result = key;
        result = 31 * result + version;
        return result.intValue();
    }

    @Override
    public String toString() {
        return "TestEntity{" + "version=" + version + ", name='" + name + '\'' + ", key=" + key + '}';
    }
}
