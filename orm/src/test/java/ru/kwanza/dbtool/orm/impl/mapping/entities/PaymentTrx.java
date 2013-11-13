package ru.kwanza.dbtool.orm.impl.mapping.entities;

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
