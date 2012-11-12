package ru.kwanza.dbtool.core;

import ru.kwanza.toolbox.fieldhelper.FieldHelper;

/**
 * @author Guzanov Alexander
 */
public final class TestEntity {

    private int key;
    private String name;
    private int version;
    public static final FieldHelper.Field<TestEntity, Integer> KEY = new FieldHelper.Field<TestEntity, Integer>() {
        public Integer value(TestEntity object) {
            return object.key;
        }
    };
    public static final FieldHelper.VersionField<TestEntity, Integer> VERSION = new FieldHelper.VersionField<TestEntity, Integer>() {
        public Integer value(TestEntity object) {
            return object.version;
        }

        public Integer generateNewValue(TestEntity object) {
            return object.version + 1;
        }

        public void setValue(TestEntity object, Integer value) {
            object.version = value;
        }
    };
    public static final FieldHelper.Field<TestEntity, String> NAME = new FieldHelper.Field<TestEntity, String>() {
        public String value(TestEntity object) {
            return object.name;
        }
    };

    public TestEntity(int key, String b) {
        this.key = key;
        this.name = b;
        this.version = 0;
    }

    public TestEntity(int key, String name, int version) {
        this.key = key;
        this.name = name;
        this.version = version;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int incrementVersion() {
        return ++version;
    }

    public void setVersion(int version) {
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

        TestEntity that = (TestEntity) o;

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
        int result = key;
        result = 31 * result + version;
        return result;
    }

    @Override
    public String toString() {
        return "TestEntity{" + "version=" + version + ", name='" + name + '\'' + ", key=" + key + '}';
    }
}
