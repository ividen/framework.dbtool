package ru.kwanza.dbtool.orm.impl.mapping;

import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.orm.impl.mapping.entities.Agent;
import ru.kwanza.dbtool.orm.impl.mapping.entities.PaymentTrx;

import javax.annotation.Resource;

/**
 * @author Kiryl Karatsetski
 */
@ContextConfiguration(locations = "dbtool-orm-test-config.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class EntityMappingTest extends AbstractJUnit4SpringContextTests {

    @Resource(name = "dbtool.IEntityMappingRegistry")
    private IEntityMappingRegistry entityMappingRegistry;

    @Test
    public void testPaymentTrx() throws Exception {
        entityMappingRegistry.registerEntityClass(PaymentTrx.class);
        entityMappingRegistry.registerEntityClass(Agent.class);
        //...
    }
}
