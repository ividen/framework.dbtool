package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.impl.fetcher.TestEntity;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingRegistryImpl;

import javax.annotation.Resource;

import static junit.framework.Assert.assertEquals;

/**
 * @author Alexander Guzanov
 */

@ContextConfiguration(locations = "mssql-config.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TestQueryBuilderWithJoin extends AbstractJUnit4SpringContextTests {
    @Resource(name = "dbtool.IEntityManager")
    private IEntityManager em;
    @Resource(name = "dbtool.IEntityMappingRegistry")
    private EntityMappingRegistryImpl registry;

    @Before
    public void init() {
        registry.registerEntityClass(TestEntity.class);
    }

    @Test
    public void test1() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("entityA, entityB, entityC {#entityE{#entityG},#entityG} ,entityD").create();

        System.out.println(query1);
//        assertEquals(query1.getConfig().getSql(),
//                "SELECT id, int_field, string_field, date_field, short_field, version, entity_aid, entity_bid, entity_cid, entity_did " +
//                        "FROM test_entity " +
//                        "WHERE (id IN (?)) AND (id LIKE ?) AND (id = ?) AND (id > ?) AND (id >= ?) AND (id < ?)" +
//                        " AND (id <= ?) AND (id IS NOT NULL) AND (id IS NULL) AND (id BETWEEN ? AND ?) AND (id <> ?)" +
//                        " AND (NOT (id <> ?)) AND (Exists(select * from test_entity where id=?)) ORDER BY id ASC, string_field DESC ");

    }

    @Test
    public void test2() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("#entityA, #entityB, #entityC {#entityE{#entityG},#entityG} ,#entityD").create();

//        assertEquals(query1.getConfig().getSql(),
//                "SELECT id, int_field, string_field, date_field, short_field, version, entity_aid, entity_bid, entity_cid, entity_did " +
//                        "FROM test_entity " +
//                        "WHERE (id IN (?)) AND (id LIKE ?) AND (id = ?) AND (id > ?) AND (id >= ?) AND (id < ?)" +
//                        " AND (id <= ?) AND (id IS NOT NULL) AND (id IS NULL) AND (id BETWEEN ? AND ?) AND (id <> ?)" +
//                        " AND (NOT (id <> ?)) AND (Exists(select * from test_entity where id=?)) ORDER BY id ASC, string_field DESC ");

    }

    @Test
    public void test3() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("#entityA, #entityB, #entityC {#entityE{#entityG},#entityG} ,#entityD").create();

//        assertEquals(query1.getConfig().getSql(),
//                "SELECT id, int_field, string_field, date_field, short_field, version, entity_aid, entity_bid, entity_cid, entity_did " +
//                        "FROM test_entity " +
//                        "WHERE (id IN (?)) AND (id LIKE ?) AND (id = ?) AND (id > ?) AND (id >= ?) AND (id < ?)" +
//                        " AND (id <= ?) AND (id IS NOT NULL) AND (id IS NULL) AND (id BETWEEN ? AND ?) AND (id <> ?)" +
//                        " AND (NOT (id <> ?)) AND (Exists(select * from test_entity where id=?)) ORDER BY id ASC, string_field DESC ");

    }

    @Test
    public void test4() {
        AbstractQuery<TestEntity> query1 =
                (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                        .join(Join.inner("entityA"))
                        .join(Join.inner("entityB"))
                        .join(Join.inner("entityC",
                                Join.inner("entityE", Join.inner("entityG", Join.inner("entityG")), Join.inner("entityF"))))
                        .join(Join.inner("entityD"))
                        .create();

//        assertEquals(query1.getConfig().getSql(),
//                "SELECT id, int_field, string_field, date_field, short_field, version, entity_aid, entity_bid, entity_cid, entity_did " +
//                        "FROM test_entity " +
//                        "WHERE (id IN (?)) AND (id LIKE ?) AND (id = ?) AND (id > ?) AND (id >= ?) AND (id < ?)" +
//                        " AND (id <= ?) AND (id IS NOT NULL) AND (id IS NULL) AND (id BETWEEN ? AND ?) AND (id <> ?)" +
//                        " AND (NOT (id <> ?)) AND (Exists(select * from test_entity where id=?)) ORDER BY id ASC, string_field DESC ");

    }

    @Test
    public void test5() {
        AbstractQuery<TestEntity> query1 =
                (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                        .join(Join.left("entityA"))
                        .join(Join.left("entityB"))
                        .join(Join.left("entityC",
                                Join.left("entityE", Join.left("entityG", Join.left("entityG")), Join.left("entityF"))))
                        .join(Join.left("entityD"))
                        .create();

//        assertEquals(query1.getConfig().getSql(),
//                "SELECT id, int_field, string_field, date_field, short_field, version, entity_aid, entity_bid, entity_cid, entity_did " +
//                        "FROM test_entity " +
//                        "WHERE (id IN (?)) AND (id LIKE ?) AND (id = ?) AND (id > ?) AND (id >= ?) AND (id < ?)" +
//                        " AND (id <= ?) AND (id IS NOT NULL) AND (id IS NULL) AND (id BETWEEN ? AND ?) AND (id <> ?)" +
//                        " AND (NOT (id <> ?)) AND (Exists(select * from test_entity where id=?)) ORDER BY id ASC, string_field DESC ");

    }

}
