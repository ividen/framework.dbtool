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

import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.annotations.ManyToOne;
import ru.kwanza.dbtool.orm.annotations.Field;

import java.sql.Types;

/**
 * @author Kiryl Karatsetski
 */
@Entity(name = "PaymentTrx", table = "payment_trx")
public class PaymentTrx extends Trx {

    @Field(value="amount", type = Types.BIGINT)
    private Long amount;

    @Field( "full_amount")
    private transient Long fullAmount;

    @Field( "agent_id")
    private Long agentId;

    private Agent agent;

    public PaymentTrx() {
    }

    public PaymentTrx(Long id, String pcid) {
        super(id, pcid);
    }

    @ManyToOne(property = "agentId")
    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    private String description;

    @Field( "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
