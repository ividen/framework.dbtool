package ru.kwanza.dbtool.orm;

import ru.kwanza.dbtool.core.UpdateException;
import ru.kwanza.dbtool.orm.api.*;
import ru.kwanza.dbtool.orm.entity.TestEntity1;

import java.util.List;

import static ru.kwanza.dbtool.orm.api.Condition.*;
import static ru.kwanza.dbtool.orm.api.OrderBy.ASC;
import static ru.kwanza.dbtool.orm.api.OrderBy.DESC;

/**
 * @author Alexander Guzanov
 */
public class Main {

    static IEntityManager em;

    public static void main(String[] args) {
        TestEntity1 entity = null;
        List<TestEntity1> entities = null;

        try {
            em.create(entity);
        } catch (UpdateException e) {
            e.printStackTrace();
        }

        IEntityBatcher batcher = em.createEntityBatcher();

        for (TestEntity1 e : entities) {
            batcher.create(e);
        }

        batcher.create(TestEntity1.class, entities);

        try {
            batcher.flush();
        } catch (EntityUpdateException e) {
            e.printStackTrace();
        }

        TestEntity1 test = em.queryBuilder(TestEntity1.class).where(isEqual("name")).create().setParameter(1, "my name").select();

        IQuery<TestEntity1> query =
                em.queryBuilder(TestEntity1.class).where(or(and(isEqual("name"), in("counter")), like("description"))).setMaxSize(1000)
                        .create();

        TestEntity1 sadfd = query.setParameter(1, "sadfd").select();

        List<TestEntity1> testEntities = query.selectList();

        em.queryBuilder(TestEntity1.class).where(or(and(isEqual("name"), in("counter")), like("description"))).setMaxSize(1000).create()
                .setParameter(1, 1).select();

        query = em.queryBuilder(TestEntity1.class).where(and(isEqual("name"), isEqual("counter"))).orderBy(ASC("name"), DESC("description"))
                .setMaxSize(1000).create();

        String desc = "desc*";
        query.setParameter(1, "name").setParameter(2, 100);

        IQueryBuilder<TestEntity1> SELECT_ENTITY_QUERY = em.queryBuilder(TestEntity1.class);


    }
}
