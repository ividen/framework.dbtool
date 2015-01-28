package ru.kwanza.dbtool.orm.entity;

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

import java.sql.Types;

/**
 * @author Alexander Guzanov
 */
@Entity(name = "TestEntity1", table = "test_entity")
public class TestEntity1 extends OldAgent {

    @IdField("id")
    private Long id;

    @Field("name")
    private String name;

    @Field(value="desc", type = Types.NVARCHAR)
    private String description;

    @VersionField( "version")
    private Long version = 0l;

    private Long counter;

    @ManyToOne(property = "agentId")
    private OldAgent oldAgent;

    public OldAgent getOldAgent() {
        return oldAgent;
    }

    public void setOldAgent(OldAgent oldAgent) {
        this.oldAgent = oldAgent;
    }

    @Field("counter")
    public Long getCounter() {
        return counter;
    }

    @Field("agent_id")
    private Long agentId;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCounter(Long counter) {
        this.counter = counter;
    }
}
