package ru.kwanza.dbtool.orm.impl.abstractentity;

import ru.kwanza.dbtool.orm.annotations.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Alexander Guzanov
 */
@Entity(name = "TestEntity", table = "test_entity")
public class TestEntity implements Serializable {
    @IdField(column = "id")
    private Long id;
    @Field(column = "int_field")
    private Integer intField;
    @Field(column = "string_field")
    private String stringField;
    @Field(column = "date_field")
    private Date dateField;
    @Field(column = "short_field")
    private Short shortField;
    @VersionField(column = "version")
    private Long version;

    @Field(column = "entity_aid")
    private Long entityAID;

    @Field(column = "entity_bid")
    private Long entityBID;

    @ManyToOne(property = "entityAID")
    private AbstractTestEntity entity;

    @Association(property = "entityAID", relationProperty = "id", condition = "isGreaterOrEqual('version',valueOf(0))")
    private AbstractTestEntity entityWithCondition;

    @ManyToOne(property = "entityBID")
    private AbstractTestEntity otherEntity;

    public TestEntity(Long id, Integer intField, String stringField, Date dateField) {
        this.id = id;
        this.intField = intField;
        this.stringField = stringField;
        this.dateField = dateField;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIntField() {
        return intField;
    }

    public void setIntField(Integer intField) {
        this.intField = intField;
    }

    public String getStringField() {
        return stringField;
    }

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }

    public Date getDateField() {
        return dateField;
    }

    public void setDateField(Date dateField) {
        this.dateField = dateField;
    }

    public Short getShortField() {
        return shortField;
    }

    public void setShortField(Short shortField) {
        this.shortField = shortField;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getEntityAID() {
        return entityAID;
    }

    public Long getEntityBID() {
        return entityBID;
    }

    public AbstractTestEntity getEntity() {
        return entity;
    }

    public AbstractTestEntity getEntityWithCondition() {
        return entityWithCondition;
    }

    public AbstractTestEntity getOtherEntity() {
        return otherEntity;
    }
}
