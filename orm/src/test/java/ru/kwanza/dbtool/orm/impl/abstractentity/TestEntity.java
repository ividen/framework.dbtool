package ru.kwanza.dbtool.orm.impl.abstractentity;

/*
 * #%L
 * dbtool-orm
 * %%
 * Copyright (C) 2015 Kwanza
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import ru.kwanza.dbtool.orm.annotations.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Alexander Guzanov
 */
@Entity(name = "TestEntity", table = "test_entity")
public class TestEntity implements Serializable {
    @IdField( "id")
    private Long id;
    @Field( "int_field")
    private Integer intField;
    @Field( "string_field")
    private String stringField;
    @Field( "date_field")
    private Date dateField;
    @Field( "short_field")
    private Short shortField;
    @VersionField( "version")
    private Long version;

    @Field( "entity_aid")
    private Long entityAID;

    @Field( "entity_bid")
    private Long entityBID;

    @ManyToOne(property = "entityAID")
    private AbstractTestEntity entity;

    @ManyToOne(property = "entityAID")
    @Condition("isGreaterOrEqual('version',valueOf(0))")
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
