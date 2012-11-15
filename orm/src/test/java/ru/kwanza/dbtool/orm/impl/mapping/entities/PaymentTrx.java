package ru.kwanza.dbtool.orm.impl.mapping.entities;

import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.annotations.Fetch;
import ru.kwanza.dbtool.orm.annotations.Field;

/**
 * @author Kiryl Karatsetski
 */
@Entity(name = "PaymentTrx", tableName = "payment_trx")
public class PaymentTrx extends Trx {

    @Field(columnName = "amount")
    private Long amount;

    @Field(columnName = "full_amount")
    private transient Long fullAmount;

    @Field(columnName = "agent_id")
    private Long agentId;

    @Fetch(propertyName = "agentId")
    private Agent agent;

    private String description;

    @Field(columnName = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
