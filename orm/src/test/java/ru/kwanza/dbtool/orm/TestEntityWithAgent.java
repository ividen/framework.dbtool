package ru.kwanza.dbtool.orm;

import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.annotations.Fetch;

/**
 * @author Alexander Guzanov
 */
@Entity
public class TestEntityWithAgent extends TestEntity {

    @Fetch(fieldName="agentId")
    private Agent agent;

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }
}
