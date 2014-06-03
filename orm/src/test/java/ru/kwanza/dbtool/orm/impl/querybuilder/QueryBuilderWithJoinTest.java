package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.api.If;
import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.impl.fetcher.TestEntity;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingRegistry;

import javax.annotation.Resource;

import static junit.framework.Assert.assertEquals;

/**
 * @author Alexander Guzanov
 */

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class QueryBuilderWithJoinTest extends AbstractJUnit4SpringContextTests {
    @Resource(name = "dbtool.IEntityManager")
    private IEntityManager em;
    @Resource(name = "dbtool.IEntityMappingRegistry")
    private EntityMappingRegistry registry;

    @Test
    public void test1() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("!entityA, !entityB, !entityC {!entityE{!entityG},!entityF} ,!entityD").create();

        assertEquals(query1.getConfig().getSql(),
                "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,test_entity.string_field test_entity_string_field,"
                        + "test_entity.date_field test_entity_date_field,test_entity.short_field test_entity_short_field,"
                        + "test_entity.version test_entity_version,test_entity.entity_aid test_entity_entity_aid,"
                        + "test_entity.entity_bid test_entity_entity_bid,test_entity.entity_cid test_entity_entity_cid,"
                        + "test_entity.entity_did test_entity_entity_did,t1.id t1_id,t1.title t1_title,t1.version t1_version,t2.id t2_id,"
                        + "t2.title t2_title,t2.version t2_version,t7.id t7_id,t7.title t7_title,t7.version t7_version,t3.id t3_id,"
                        + "t3.title t3_title,t3.version t3_version,t3.entity_eid t3_entity_eid,t3.entity_fid t3_entity_fid,t6.id t6_id,"
                        + "t6.title t6_title,t6.version t6_version,t4.id t4_id,t4.title t4_title,t4.version t4_version,"
                        + "t4.entity_gid t4_entity_gid,t5.id t5_id,t5.title t5_title,t5.version t5_version " + "FROM test_entity "
                        + "INNER JOIN test_entity_a t1 ON test_entity.entity_aid=t1.id  "
                        + "INNER JOIN test_entity_b t2 ON test_entity.entity_bid=t2.id  "
                        + "INNER JOIN test_entity_d t7 ON test_entity.entity_did=t7.id  "
                        + "INNER JOIN (test_entity_c t3 INNER JOIN test_entity_f t6 ON t3.entity_fid=t6.id  "
                        + "INNER JOIN (test_entity_e t4 INNER JOIN test_entity_g t5 ON t4.entity_gid=t5.id ) ON t3.entity_eid=t4.id ) "
                        + "ON test_entity.entity_cid=t3.id ");
    }

    @Test
    public void test2() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).orderBy("id ASC")
                .join("&entityA, &entityB, &entityC {&entityE{!entityG},&entityF} ,&entityD").create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,"
                + "test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,"
                + "test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,"
                + "test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,"
                + "test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_entity_did,t1.id t1_id,"
                + "t1.title t1_title,t1.version t1_version,t2.id t2_id,t2.title t2_title,t2.version t2_version,t7.id t7_id,"
                + "t7.title t7_title,t7.version t7_version,t3.id t3_id,t3.title t3_title,t3.version t3_version,"
                + "t3.entity_eid t3_entity_eid,t3.entity_fid t3_entity_fid,t6.id t6_id,t6.title t6_title,t6.version t6_version,"
                + "t4.id t4_id,t4.title t4_title,t4.version t4_version,t4.entity_gid t4_entity_gid,t5.id t5_id,t5.title t5_title,"
                + "t5.version t5_version " + "FROM test_entity " + "LEFT JOIN test_entity_a t1 ON test_entity.entity_aid=t1.id  "
                + "LEFT JOIN test_entity_b t2 ON test_entity.entity_bid=t2.id  "
                + "LEFT JOIN test_entity_d t7 ON test_entity.entity_did=t7.id  " + "LEFT JOIN (test_entity_c t3 "
                + "LEFT JOIN test_entity_f t6 ON t3.entity_fid=t6.id  " + "LEFT JOIN (test_entity_e t4 "
                + "INNER JOIN test_entity_g t5 ON t4.entity_gid=t5.id ) ON t3.entity_eid=t4.id ) ON test_entity.entity_cid=t3.id  "
                + "ORDER BY test_entity.id ASC");
    }

    @Test
    public void test3() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("!entityA, !entityB, !entityC {&entityE{&entityG},&entityF} ,!entityD").create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,"
                + "test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,"
                + "test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,"
                + "test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,"
                + "test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_entity_did,t1.id t1_id,"
                + "t1.title t1_title,t1.version t1_version,t2.id t2_id,t2.title t2_title,t2.version t2_version,t7.id t7_id,"
                + "t7.title t7_title,t7.version t7_version,t3.id t3_id,t3.title t3_title,t3.version t3_version,"
                + "t3.entity_eid t3_entity_eid,t3.entity_fid t3_entity_fid,t6.id t6_id,t6.title t6_title,"
                + "t6.version t6_version,t4.id t4_id,t4.title t4_title,t4.version t4_version,t4.entity_gid t4_entity_gid,"
                + "t5.id t5_id,t5.title t5_title,t5.version t5_version "
                + "FROM test_entity INNER JOIN test_entity_a t1 ON test_entity.entity_aid=t1.id  "
                + "INNER JOIN test_entity_b t2 ON test_entity.entity_bid=t2.id  "
                + "INNER JOIN test_entity_d t7 ON test_entity.entity_did=t7.id  " + "INNER JOIN (test_entity_c t3 "
                + "LEFT JOIN test_entity_f t6 ON t3.entity_fid=t6.id  " + "LEFT JOIN (test_entity_e t4 "
                + "LEFT JOIN test_entity_g t5 ON t4.entity_gid=t5.id ) ON t3.entity_eid=t4.id ) ON test_entity.entity_cid=t3.id ");
    }

    @Test
    public void test4() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("&entityA, &entityB, &entityC {!entityE{!entityG},!entityF} ,&entityD").create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,"
                + "test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,"
                + "test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,"
                + "test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,"
                + "test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_entity_did,"
                + "t1.id t1_id,t1.title t1_title,t1.version t1_version,t2.id t2_id,t2.title t2_title,"
                + "t2.version t2_version,t7.id t7_id,t7.title t7_title,t7.version t7_version,t3.id t3_id,"
                + "t3.title t3_title,t3.version t3_version,t3.entity_eid t3_entity_eid,t3.entity_fid t3_entity_fid,"
                + "t6.id t6_id,t6.title t6_title,t6.version t6_version,t4.id t4_id,t4.title t4_title,t4.version t4_version,"
                + "t4.entity_gid t4_entity_gid,t5.id t5_id,t5.title t5_title,t5.version t5_version "
                + "FROM test_entity LEFT JOIN test_entity_a t1 ON test_entity.entity_aid=t1.id  "
                + "LEFT JOIN test_entity_b t2 ON test_entity.entity_bid=t2.id  "
                + "LEFT JOIN test_entity_d t7 ON test_entity.entity_did=t7.id  "
                + "LEFT JOIN (test_entity_c t3 INNER JOIN test_entity_f t6 ON t3.entity_fid=t6.id  "
                + "INNER JOIN (test_entity_e t4 INNER JOIN test_entity_g t5 ON t4.entity_gid=t5.id ) ON t3.entity_eid=t4.id ) "
                + "ON test_entity.entity_cid=t3.id ");
    }

    @Test
    public void test5() {
        AbstractQuery<TestEntity> query1 =
                (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).join(Join.left("entityA")).join(Join.left("entityB"))
                        .join(Join.left("entityC", Join.inner("entityE", Join.inner("entityG")), Join.inner("entityF")))
                        .join(Join.left("entityD")).create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,"
                + "test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,"
                + "test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,"
                + "test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,"
                + "test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_entity_did,"
                + "t1.id t1_id,t1.title t1_title,t1.version t1_version,t2.id t2_id,t2.title t2_title,t2.version t2_version,"
                + "t7.id t7_id,t7.title t7_title,t7.version t7_version,t3.id t3_id,t3.title t3_title,t3.version t3_version,"
                + "t3.entity_eid t3_entity_eid,t3.entity_fid t3_entity_fid,t6.id t6_id,t6.title t6_title,"
                + "t6.version t6_version,t4.id t4_id,t4.title t4_title,t4.version t4_version,t4.entity_gid t4_entity_gid,"
                + "t5.id t5_id,t5.title t5_title,t5.version t5_version " + "FROM test_entity "
                + "LEFT JOIN test_entity_a t1 ON test_entity.entity_aid=t1.id  "
                + "LEFT JOIN test_entity_b t2 ON test_entity.entity_bid=t2.id  "
                + "LEFT JOIN test_entity_d t7 ON test_entity.entity_did=t7.id " + " LEFT JOIN (test_entity_c t3 "
                + "INNER JOIN test_entity_f t6 ON t3.entity_fid=t6.id  "
                + "INNER JOIN (test_entity_e t4 INNER JOIN test_entity_g t5 ON t4.entity_gid=t5.id ) "
                + "ON t3.entity_eid=t4.id ) ON test_entity.entity_cid=t3.id ");
    }

    @Test
    public void test6() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("&entityA, &entityB, &entityC {!entityE{!entityG},!entityF} ,&entityD").create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,"
                + "test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,"
                + "test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,"
                + "test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,"
                + "test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_entity_did,t1.id t1_id,"
                + "t1.title t1_title,t1.version t1_version,t2.id t2_id,t2.title t2_title,t2.version t2_version,"
                + "t7.id t7_id,t7.title t7_title,t7.version t7_version,t3.id t3_id,t3.title t3_title,t3.version t3_version,"
                + "t3.entity_eid t3_entity_eid,t3.entity_fid t3_entity_fid,t6.id t6_id,t6.title t6_title,"
                + "t6.version t6_version,t4.id t4_id,t4.title t4_title,t4.version t4_version,t4.entity_gid t4_entity_gid,"
                + "t5.id t5_id,t5.title t5_title,t5.version t5_version "
                + "FROM test_entity LEFT JOIN test_entity_a t1 ON test_entity.entity_aid=t1.id  "
                + "LEFT JOIN test_entity_b t2 ON test_entity.entity_bid=t2.id  "
                + "LEFT JOIN test_entity_d t7 ON test_entity.entity_did=t7.id  "
                + "LEFT JOIN (test_entity_c t3 INNER JOIN test_entity_f t6 ON t3.entity_fid=t6.id  "
                + "INNER JOIN (test_entity_e t4 INNER JOIN test_entity_g t5 ON t4.entity_gid=t5.id ) ON t3.entity_eid=t4.id ) "
                + "ON test_entity.entity_cid=t3.id ");
    }

    @Test
    public void test7() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("!entityA, !entityB, !entityC {!entityE{!entityG},!entityF} ,!entityD")
                .where(If.and(If.between("dateField"), If.isEqual("id"))).orderBy("dateField ASC").create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,"
                + "test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,"
                + "test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,"
                + "test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,"
                + "test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_entity_did,t1.id t1_id,"
                + "t1.title t1_title,t1.version t1_version,t2.id t2_id,t2.title t2_title,t2.version t2_version,t7.id t7_id,"
                + "t7.title t7_title,t7.version t7_version,t3.id t3_id,t3.title t3_title,t3.version t3_version,"
                + "t3.entity_eid t3_entity_eid,t3.entity_fid t3_entity_fid,t6.id t6_id,t6.title t6_title,"
                + "t6.version t6_version,t4.id t4_id,t4.title t4_title,t4.version t4_version,t4.entity_gid t4_entity_gid,"
                + "t5.id t5_id,t5.title t5_title,t5.version t5_version " + "FROM test_entity "
                + "INNER JOIN test_entity_a t1 ON test_entity.entity_aid=t1.id  "
                + "INNER JOIN test_entity_b t2 ON test_entity.entity_bid=t2.id  "
                + "INNER JOIN test_entity_d t7 ON test_entity.entity_did=t7.id  " + "INNER JOIN (test_entity_c t3 "
                + "INNER JOIN test_entity_f t6 ON t3.entity_fid=t6.id  "
                + "INNER JOIN (test_entity_e t4 INNER JOIN test_entity_g t5 ON t4.entity_gid=t5.id ) ON t3.entity_eid=t4.id ) ON test_entity.entity_cid=t3.id  "
                + "WHERE (test_entity.date_field BETWEEN ? AND ?) AND (test_entity.id = ?) ORDER BY test_entity.date_field ASC");
    }

    @Test
    public void test8() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("!entityA, !entityB, !entityC {!entityE{!entityG},!entityF} ,!entityD")
                .where(If.and(If.isEqual("entityA.title"), If.isEqual("entityB.title"), If.isEqual("entityC.title")))
                .orderBy("dateField ASC").create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,"
                + "test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,"
                + "test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,"
                + "test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,"
                + "test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_entity_did,"
                + "t1.id t1_id,t1.title t1_title,t1.version t1_version,t2.id t2_id,t2.title t2_title,"
                + "t2.version t2_version,t7.id t7_id,t7.title t7_title,t7.version t7_version,t3.id t3_id,"
                + "t3.title t3_title,t3.version t3_version,t3.entity_eid t3_entity_eid,t3.entity_fid t3_entity_fid,"
                + "t6.id t6_id,t6.title t6_title,t6.version t6_version,t4.id t4_id,t4.title t4_title,t4.version t4_version,"
                + "t4.entity_gid t4_entity_gid,t5.id t5_id,t5.title t5_title,t5.version t5_version " + "FROM test_entity "
                + "INNER JOIN test_entity_a t1 ON test_entity.entity_aid=t1.id  "
                + "INNER JOIN test_entity_b t2 ON test_entity.entity_bid=t2.id  "
                + "INNER JOIN test_entity_d t7 ON test_entity.entity_did=t7.id  " + "INNER JOIN (test_entity_c t3 "
                + "INNER JOIN test_entity_f t6 ON t3.entity_fid=t6.id  " + "INNER JOIN (test_entity_e t4 "
                + "INNER JOIN test_entity_g t5 ON t4.entity_gid=t5.id ) ON t3.entity_eid=t4.id ) ON test_entity.entity_cid=t3.id  "
                + "WHERE (t1.title = ?) AND (t2.title = ?) AND (t3.title = ?) ORDER BY test_entity.date_field ASC");
    }

    @Test
    public void test9() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).where(If
                .and(If.isEqual("entityA.title"), If.isEqual("entityB.title"), If.isEqual("entityC.title"),
                        If.isEqual("entityC.entityE.entityG.title"))).orderBy("entityC.entityF.title ASC").create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,"
                + "test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,"
                + "test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,"
                + "test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,"
                + "test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_entity_did,"
                + "t1.id t1_id,t1.title t1_title,t1.version t1_version,t2.id t2_id,t2.title t2_title,"
                + "t2.version t2_version,t3.id t3_id,t3.title t3_title,t3.version t3_version,t3.entity_eid t3_entity_eid,"
                + "t3.entity_fid t3_entity_fid,t6.id t6_id,t6.title t6_title,t6.version t6_version,t4.id t4_id,t4.title t4_title,"
                + "t4.version t4_version,t4.entity_gid t4_entity_gid,t5.id t5_id,t5.title t5_title,t5.version t5_version "
                + "FROM test_entity " + "INNER JOIN test_entity_a t1 ON test_entity.entity_aid=t1.id  "
                + "INNER JOIN test_entity_b t2 ON test_entity.entity_bid=t2.id  " + "INNER JOIN (test_entity_c t3 "
                + "INNER JOIN test_entity_f t6 ON t3.entity_fid=t6.id  "
                + "INNER JOIN (test_entity_e t4 INNER JOIN test_entity_g t5 ON t4.entity_gid=t5.id ) ON t3.entity_eid=t4.id ) ON test_entity.entity_cid=t3.id "
                + " WHERE (t1.title = ?) AND (t2.title = ?) AND (t3.title = ?) AND (t5.title = ?) ORDER BY t6.title ASC");
    }

    @Test
    public void test10() {
        AbstractQuery<TestEntity> query1 =
                (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).join("!associatedEntityC,!entityC{!entityE,!entityF}")
                        .create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,"
                + "test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,"
                + "test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,"
                + "test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,"
                + "test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_entity_did,t1.id t1_id,"
                + "t1.title t1_title,t1.version t1_version,t1.entity_eid t1_entity_eid,t1.entity_fid t1_entity_fid,"
                + "t6.id t6_id,t6.title t6_title,t6.version t6_version,t5.id t5_id,t5.title t5_title,t5.version t5_version,"
                + "t5.entity_gid t5_entity_gid,t2.id t2_id,t2.title t2_title,t2.version t2_version,t2.entity_eid t2_entity_eid,"
                + "t2.entity_fid t2_entity_fid,t4.id t4_id,t4.title t4_title,t4.version t4_version,t3.id t3_id,t3.title t3_title,"
                + "t3.version t3_version,t3.entity_gid t3_entity_gid " + "FROM test_entity " + "INNER JOIN (test_entity_c t1 "
                + "INNER JOIN test_entity_f t6 ON t1.entity_fid=t6.id  "
                + "INNER JOIN test_entity_e t5 ON t1.entity_eid=t5.id ) ON test_entity.entity_cid=t1.id  "
                + "AND ((t5.version IS NOT NULL) AND (t6.version > ?)) "
                + "INNER JOIN (test_entity_c t2 INNER JOIN test_entity_f t4 ON t2.entity_fid=t4.id  "
                + "INNER JOIN test_entity_e t3 ON t2.entity_eid=t3.id ) ON test_entity.entity_cid=t2.id ");
    }

    @Test
    public void test11() {
        AbstractQuery<TestEntity> query1 =
                (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).join("&associatedEntityC,!entityC{!entityE,&entityF}")
                        .create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,"
                + "test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,"
                + "test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,"
                + "test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,"
                + "test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_entity_did,t1.id t1_id,"
                + "t1.title t1_title,t1.version t1_version,t1.entity_eid t1_entity_eid,t1.entity_fid t1_entity_fid,"
                + "t6.id t6_id,t6.title t6_title,t6.version t6_version,t5.id t5_id,t5.title t5_title,t5.version t5_version,"
                + "t5.entity_gid t5_entity_gid,t2.id t2_id,t2.title t2_title,t2.version t2_version,t2.entity_eid t2_entity_eid,"
                + "t2.entity_fid t2_entity_fid,t4.id t4_id,t4.title t4_title,t4.version t4_version,t3.id t3_id,t3.title t3_title,"
                + "t3.version t3_version,t3.entity_gid t3_entity_gid " + "FROM test_entity " + "LEFT JOIN (test_entity_c t1 "
                + "INNER JOIN test_entity_f t6 ON t1.entity_fid=t6.id  "
                + "INNER JOIN test_entity_e t5 ON t1.entity_eid=t5.id ) ON test_entity.entity_cid=t1.id  "
                + "AND ((t5.version IS NOT NULL) AND (t6.version > ?)) "
                + "INNER JOIN (test_entity_c t2 LEFT JOIN test_entity_f t4 ON t2.entity_fid=t4.id  "
                + "INNER JOIN test_entity_e t3 ON t2.entity_eid=t3.id ) ON test_entity.entity_cid=t2.id ");
    }

    @Test
    public void test12() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("&associatedEntityC, &associatedEntityA, !entityA, !entityC").create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,"
                + "test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,"
                + "test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,"
                + "test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,"
                + "test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_entity_did,"
                + "t2.id t2_id,t2.title t2_title,t2.version t2_version,t1.id t1_id,t1.title t1_title,"
                + "t1.version t1_version,t1.entity_eid t1_entity_eid,t1.entity_fid t1_entity_fid,t6.id t6_id,"
                + "t6.title t6_title,t6.version t6_version,t5.id t5_id,t5.title t5_title,t5.version t5_version,"
                + "t5.entity_gid t5_entity_gid,t3.id t3_id,t3.title t3_title,t3.version t3_version,t4.id t4_id,t4.title t4_title,"
                + "t4.version t4_version,t4.entity_eid t4_entity_eid,t4.entity_fid t4_entity_fid " + "FROM test_entity "
                + "LEFT JOIN test_entity_a t2 ON test_entity.entity_aid=t2.id  AND ((t2.version IS NOT NULL) AND (t2.version > ?)) "
                + "LEFT JOIN (test_entity_c t1 INNER JOIN test_entity_f t6 ON t1.entity_fid=t6.id  INNER JOIN test_entity_e t5 ON t1.entity_eid=t5.id ) "
                + "ON test_entity.entity_cid=t1.id  AND ((t5.version IS NOT NULL) AND (t6.version > ?)) "
                + "INNER JOIN test_entity_a t3 ON test_entity.entity_aid=t3.id  INNER JOIN test_entity_c t4 ON test_entity.entity_cid=t4.id ");
    }

    @Test
    public void test13() {
        AbstractQuery<TestEntity> query1 =
                (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).where(If.isEqual("associatedEntityC.title")).create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,"
                + "test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,"
                + "test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,"
                + "test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,"
                + "test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_entity_did,t1.id t1_id,"
                + "t1.title t1_title,t1.version t1_version,t1.entity_eid t1_entity_eid,t1.entity_fid t1_entity_fid,"
                + "t3.id t3_id,t3.title t3_title,t3.version t3_version,t2.id t2_id,t2.title t2_title,t2.version t2_version,"
                + "t2.entity_gid t2_entity_gid " + "FROM test_entity INNER JOIN (test_entity_c t1 "
                + "INNER JOIN test_entity_f t3 ON t1.entity_fid=t3.id  " + "INNER JOIN test_entity_e t2 ON t1.entity_eid=t2.id )"
                + " ON test_entity.entity_cid=t1.id  AND ((t2.version IS NOT NULL) AND (t3.version > ?)) " + "WHERE t1.title = ?");
    }

    @Test
    public void test14() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("entityA, entityB, entityC {!entityE{!entityG},!entityF} ,!entityD")
                .where(If.and(If.isEqual("entityA.title"), If.isEqual("entityB.title"), If.isEqual("entityC.title")))
                .orderBy("dateField ASC").create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,"
                + "test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,"
                + "test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,"
                + "test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,"
                + "test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_entity_did,t2.id t2_id,"
                + "t2.title t2_title,t2.version t2_version,t3.id t3_id,t3.title t3_title,t3.version t3_version,t1.id t1_id,"
                + "t1.title t1_title,t1.version t1_version,t4.id t4_id,t4.title t4_title,t4.version t4_version,"
                + "t4.entity_eid t4_entity_eid,t4.entity_fid t4_entity_fid FROM test_entity "
                + "INNER JOIN test_entity_a t2 ON test_entity.entity_aid=t2.id  "
                + "INNER JOIN test_entity_b t3 ON test_entity.entity_bid=t3.id  INNER JOIN test_entity_d t1 "
                + "ON test_entity.entity_did=t1.id  INNER JOIN test_entity_c t4 ON test_entity.entity_cid=t4.id  "
                + "WHERE (t2.title = ?) AND (t3.title = ?) AND (t4.title = ?) ORDER BY test_entity.date_field ASC");
    }

    @Test
    public void test15() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("entityA, entityB, entityC {entityE{!entityG},entityF} ,entityD").orderBy("dateField ASC").create();

        assertEquals(query1.getConfig().getSql(), "SELECT id,int_field,string_field,date_field,short_field,version,entity_aid,entity_bid,"
                + "entity_cid,entity_did FROM test_entity ORDER BY test_entity.date_field ASC");
    }

    @Test
    public void test16() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("entityA, entityB, entityC {entityE{!entityG},entityF} ,entityD").where(If.isEqual("entityC.entityE.entityG.title"))
                .orderBy("dateField ASC").create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,"
                + "test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,test_entity.short_fi"
                + "eld test_entity_short_field,test_entity.version test_entity_version,test_entity.entity_aid test_entity_entity_aid,test_en"
                + "tity.entity_bid test_entity_entity_bid,test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_"
                + "entity_did,t1.id t1_id,t1.title t1_title,t1.version t1_version,t1.entity_eid t1_entity_eid,t1.entity_fid t1_entity_fid,"
                + "t2.id t2_id,t2.title t2_title,t2.version t2_version,t2.entity_gid t2_entity_gid,t3.id t3_id,t3.title t3_title,t3.versi"
                + "on t3_version FROM test_entity INNER JOIN (test_entity_c t1 INNER JOIN (test_entity_e t2 INNER JOIN test_entity_g t3 O"
                + "N t2.entity_gid=t3.id ) ON t1.entity_eid=t2.id ) ON test_entity.entity_cid=t1.id  WHERE t3.title = ? ORDER BY test_e"
                + "ntity.date_field ASC");
    }

    @Test
    public void test17() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("entityA, entityB, entityC {entityE{!entityG},entityF} ,entityD").where(If.isEqual("entityC.entityE.title"))
                .orderBy("dateField ASC").create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,test"
                + "_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,test_entity.short_field te"
                + "st_entity_short_field,test_entity.version test_entity_version,test_entity.entity_aid test_entity_entity_aid,test_entit"
                + "y.entity_bid test_entity_entity_bid,test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_"
                + "entity_did,t1.id t1_id,t1.title t1_title,t1.version t1_version,t1.entity_eid t1_entity_eid,t1.entity_fid t1_entity_fid"
                + ",t2.id t2_id,t2.title t2_title,t2.version t2_version,t2.entity_gid t2_entity_gid FROM test_entity INNER JOIN (test_ent"
                + "ity_c t1 INNER JOIN test_entity_e t2 ON t1.entity_eid=t2.id ) ON test_entity.entity_cid=t1.id  WHERE t2.title = ? O"
                + "RDER BY test_entity.date_field ASC");
    }

    @Test
    public void test18() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("entityA, entityB, entityC {entityE{entityG},entityF} ,entityD").orderBy("dateField ASC").create();

        assertEquals(query1.getConfig().getSql(),
                "SELECT id,int_field,string_field,date_field,short_field,version,entity_aid,entity_bid,entity_cid,"
                        + "entity_did FROM test_entity ORDER BY test_entity.date_field ASC");
    }

}
