package ru.kwanza.dbtool.orm.impl.mapping.entities;

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

import ru.kwanza.dbtool.orm.annotations.Field;
import ru.kwanza.dbtool.orm.annotations.IdField;
import ru.kwanza.dbtool.orm.annotations.VersionField;

/**
 * @author Kiryl Karatsetski
 */
public abstract class AbstractEntity {

    @IdField( "id")
    private Long id;

    @Field( "pcid")
    private String pcid;

    @VersionField( "version")
    private Long version;

    protected AbstractEntity() {
    }

    protected AbstractEntity(Long id, String pcid) {
        this.id = id;
        this.pcid = pcid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPcid() {
        return pcid;
    }

    public void setPcid(String pcid) {
        this.pcid = pcid;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
