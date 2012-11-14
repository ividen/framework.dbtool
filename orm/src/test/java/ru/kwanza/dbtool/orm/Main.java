package ru.kwanza.dbtool.orm;

import ru.kwanza.dbtool.core.UpdateException;
import ru.kwanza.dbtool.orm.entity.TestEntity;

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
            e.printStackTrace();
        }

        IEntityBatcher batcher = em.newBatcher();

        for (TestEntity e : entities) {
            batcher.create(e);
        }

        batcher.create(TestEntity.class, entities);

        try {
            batcher.flush();
        } catch (EntityUpdateException e) {
            e.printStackTrace();
        }

        TestEntity test = em.queryBuilder(TestEntity.class)
                .where(isEqual("name")).create().setParameter(1, "my name").select();

        IQuery<TestEntity> query = em.queryBuilder(TestEntity.class)
                .where(or(and(isEqual("name"),
                        in("counter")), like("description"))).setMaxSize(1000).create();


        TestEntity sadfd = query.setParameter(1, "sadfd").select();

        List<TestEntity> testEntities = query.selectList();


        em.queryBuilder(TestEntity.class)
                .where(or(and(isEqual("name"),
                        in("counter")), like("description"))).setMaxSize(1000).create().setParameter(1, 1).select();


        query = em.queryBuilder(TestEntity.class)
                .where(and(isEqual("name"), isEqual("counter")))
                .orderBy(ASC("name"), DESC("description")).setMaxSize(1000).create();

        String desc = "desc*";
        query.setParameter(1, "name").setParameter(2, 100);

        IQueryBuilder<TestEntity> SELECT_ENTITY_QUERY = em.queryBuilder(TestEntity.class);

        List<TestEntity> description = SELECT_ENTITY_QUERY.setMaxSize(1000)
                .create().selectListWithFilter(new Filter(desc != null, like("description"), desc));

    }
}
