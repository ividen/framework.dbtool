package ru.kwanza.dbtool.orm.impl.operation;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.core.UpdateException;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.mapping.entities.Agent;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Kiryl Karatsetski
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class SimpleOperationTest extends AbstractJUnit4SpringContextTests {

    @Resource(name = "dbtool.IEntityManager")
    private IEntityManager entityManager;

    @Resource(name = "dbtool.IEntityMappingRegistry")
    private IEntityMappingRegistry entityMappingRegistry;

    @Resource(name = "dataSource")
    private DataSource dataSource;

    @Before
    public void registerAgent() throws Exception {
        entityMappingRegistry.registerEntityClass(Agent.class);
        DatabaseOperation.DELETE_ALL.execute(getConnection(), new DefaultDataSet(new DefaultTable("agent")));
    }

    @Test
    public void testCreate() throws Exception {
        final Agent agent1 = new Agent(1L, "pcid", "name");
        final Agent agent2 = new Agent(2L, "pcid", "name");
        final Agent agent3 = new Agent(3L, "pcid", "name");
        final Agent agent4 = new Agent(4L, "pcid", "name");
        entityManager.create(Agent.class, Arrays.asList(agent1, agent2, agent3, agent4));
    }

    @Test
    //TODO kkaratsetski: Use DBUnit for init
    public void testRead() throws UpdateException {
        final Agent agent1 = new Agent(1L, "pcid", "name");
        final Agent agent2 = new Agent(2L, "pcid", "name");
        final Agent agent3 = new Agent(3L, "pcid", "name");
        final Agent agent4 = new Agent(4L, "pcid", "name");
        entityManager.create(Agent.class, Arrays.asList(agent1, agent2, agent3, agent4));
        final Agent agent = entityManager.readByKey(Agent.class, Arrays.asList(1L));
        final Collection<Agent> agents = entityManager.readByKeys(Agent.class, Arrays.asList(1L, 2L, 3L, 4L));
    }

    @Test
    public void testUpdate() throws Exception {
        final Agent agent = new Agent(1L, "pcid2", "name2");
        entityManager.create(agent);
        entityManager.update(agent);
    }

    @Test
    //TODO kkaratsetski: Use DBUnit for init
    public void testDeleteByObject() throws Exception {
        final Agent agent1 = new Agent(1L, "pcid", "name");
        final Agent agent2 = new Agent(2L, "pcid", "name");
        final Agent agent3 = new Agent(3L, "pcid", "name");
        final Agent agent4 = new Agent(4L, "pcid", "name");
        entityManager.create(Agent.class, Arrays.asList(agent1, agent2, agent3, agent4));
        entityManager.delete(Agent.class, Arrays.asList(agent1, agent2, agent3, agent4));
        entityManager.delete(Agent.class, Arrays.asList(agent1, agent2, agent3, agent4));
    }

    @Test
    //TODO kkaratsetski: Use DBUnit for init
    public void testDeleteByKey() throws Exception {
        final Agent agent = new Agent(1L, "pcid", "name");
        entityManager.create(agent);
        entityManager.deleteByKey(Agent.class, 1L);
    }

    public IDatabaseConnection getConnection() throws SQLException, DatabaseUnitException {
        return new DatabaseConnection(dataSource.getConnection());
    }
}
