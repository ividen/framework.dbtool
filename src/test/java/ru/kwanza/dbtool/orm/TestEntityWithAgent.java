package ru.kwanza.dbtool.orm;

import ru.kwanza.dbtool.orm.mapping.Fetch;

/**
 * @author Alexander Guzanov
 */
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
