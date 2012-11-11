package ru.kwanza.dbtool.orm;

import ru.kwanza.dbtool.UpdateException;

import java.util.List;

import static ru.kwanza.dbtool.orm.Condition.*;
import static ru.kwanza.dbtool.orm.OrderBy.ASC;
import static ru.kwanza.dbtool.orm.OrderBy.DESC;

/**
 * @author Alexander Guzanov
 */
public class Main {
    static IEntityManager em;

    public static void main(String[] args) {
        TestEntity entity = null;
        List<TestEntity> entities = null;

        try {
            em.create(entity);
        } catch (UpdateException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        IEntityBatcher batcher = em.newBatcher();

        for (TestEntity e : entities) {
            batcher.create(e);
        }

        batcher.create(TestEntity.class, entities);

        try {
            batcher.flush();
        } catch (EntityUpdateException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        TestEntity test = em.queryBuilder(TestEntity.class)
                .where(Condition.isEqual("name")).create().setParameter(1, "my name").select();

        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .where(or(and(Condition.isEqual("name"),
                        Condition.in("counter")), Condition.like("description"))).setMaxSize(1000).create();


        TestEntity sadfd = query.setParameter(1, "sadfd").select();

        List<TestEntity> testEntities = query.selectList();


        em.queryBuilder(TestEntity.class)
                .where(or(and(Condition.isEqual("name"),
                        Condition.in("counter")), Condition.like("description"))).setMaxSize(1000).create().setParameter(1, 1).select();


        query = em.queryBuilder(TestEntity.class)
                .where(and(isEqual("name"), isEqual("counter")))
                .orderBy(ASC("name"), DESC("description")).setMaxSize(1000).create();

        String desc = "desc*";
        query.setParameter(1, "name").setParameter(2, 100);

        IQueryBuilder<TestEntityWithAgent> SELECT_ENTITY_QUERY = em.queryBuilder(TestEntityWithAgent.class);

        List<TestEntityWithAgent> description = SELECT_ENTITY_QUERY.setMaxSize(1000)
                .create().selectListWithFilter(new Filter(desc != null, Condition.like("description"), desc));

    }
}
