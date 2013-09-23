package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Alexander Guzanov
 */
@Entity(name = "TestEntity", table = "test_entity")
public class TestEntity  implements Serializable{
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
    @Field(column = "entity_cid")
    private Long entityCID;
    @Field(column = "entity_did")
    private Long entityDID;


    @ManyToOne(property = "entityAID")
    private TestEntityA entityA;
    @ManyToOne(property = "entityBID")
    private TestEntityB entityB;
    @ManyToOne(property = "entityCID")
    private TestEntityC entityC;
    @ManyToOne(property = "entityDID")
    private TestEntityD entityD;

    @Association(property = "entityAID", relationProperty = "id" ,condition="and(isNotNull('version'),isGreater('version',valueOf(-1)))")
    private TestEntityA associatedEntityA;

    @Association(property = "entityCID", relationProperty = "id" ,condition="and(isNotNull('entityE.version'),isGreater('entityF.version',valueOf(-1)))")
    private TestEntityC associatedEntityC;

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

    public Long getEntityCID() {
        return entityCID;
    }

    public Long getEntityDID() {
        return entityDID;
    }

    public TestEntityA getEntityA() {
        return entityA;
    }

    public TestEntityB getEntityB() {
        return entityB;
    }

    public TestEntityC getEntityC() {
        return entityC;
    }

    public TestEntityD getEntityD() {
        return entityD;
    }

    public TestEntityA getAssociatedEntityA() {
        return associatedEntityA;
    }

    public TestEntityC getAssociatedEntityC() {
        return associatedEntityC;
    }
}
