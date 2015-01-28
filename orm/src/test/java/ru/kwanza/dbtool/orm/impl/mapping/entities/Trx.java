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

import java.util.Date;

/**
 * @author Kiryl Karatsetski
 */
public abstract class Trx extends AbstractEntity {

    @Field( "parent_trx_id")
    private Long parentTrxId;

    @Field( "started_at")
    private Date startedAt;

    @Field( "finished_at")
    private Date finishedAt;

    @Field( "result_code")
    private Integer resultCode;

    @Field( "extended_code")
    private Integer extendedCode;

    protected Trx() {
    }

    protected Trx(Long id, String pcid) {
        super(id, pcid);
    }

    public Long getParentTrxId() {
        return parentTrxId;
    }

    public void setParentTrxId(Long parentTrxId) {
        this.parentTrxId = parentTrxId;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public Integer getExtendedCode() {
        return extendedCode;
    }

    public void setExtendedCode(Integer extendedCode) {
        this.extendedCode = extendedCode;
    }
}
