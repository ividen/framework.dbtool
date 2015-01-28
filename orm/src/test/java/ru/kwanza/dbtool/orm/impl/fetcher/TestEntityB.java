package ru.kwanza.dbtool.orm.impl.fetcher;

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
import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
@Entity(name="TestEntityB", table = "test_entity_b")
public class TestEntityB   implements Serializable {
    @IdField( "id")
    private Long id;
    @Field( "title")
    private String title;
    @VersionField( "version")
    private Long version;
    @OneToMany(relationClass = TestEntity.class,relationProperty = "entityAID")
    private Collection<TestEntity> testEntities;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Long getVersion() {
        return version;
    }

    public Collection<TestEntity> getTestEntities() {
        return testEntities;
    }
}
