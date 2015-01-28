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

import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.annotations.Field;
import ru.kwanza.dbtool.orm.annotations.IdField;

/**
 * @author Alexander Guzanov
 */
@Entity(name = "OldAgent", table = "agent")
public class OldAgent {

    @IdField("id1")
    private Long id1;

    @Field("name1")
    private String name1;

    @Field("address")
    private String address;

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    public String getName1() {
        return name1;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
