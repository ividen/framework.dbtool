package ru.kwanza.dbtool.orm.impl.abstractentity;

import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.impl.querybuilder.AbstractQuery;

import javax.annotation.Resource;

/**
 * @author Alexander Guzanov
 */
@ContextConfiguration(locations = "mssql-config.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TestAbstractEntity extends AbstractJUnit4SpringContextTests {
    @Resource(name = "dbtool.IEntityManager")
    private IEntityManager em;

    @Test
    public void testQuieryBuilder() {
        final AbstractQuery<AbstractTestEntity> query =
                (AbstractQuery<AbstractTestEntity>) em.queryBuilder(AbstractTestEntity.class).create();
        System.out.println(query.getConfig().getSql());
        try {
            Thread.currentThread().sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQuieryBuilder_2() {
        final AbstractQuery<TestEntityD> query = (AbstractQuery<TestEntityD>) em.queryBuilder(TestEntityD.class).create();
        System.out.println(query.getConfig().getSql());
        try {
            Thread.currentThread().sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
