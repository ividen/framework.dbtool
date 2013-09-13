package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.orm.api.Condition;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.impl.fetcher.TestEntity;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingRegistryImpl;

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
    private EntityMappingRegistryImpl registry;

    @Test
    public void test1() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("entityA, entityB, entityC {entityE{entityG},entityF} ,entityD").create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,"
                + "test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,"
                + "test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,"
                + "test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,"
                + "test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_entity_did,t1.id t1_id,"
                + "t1.title t1_title,t1.version t1_version,t2.id t2_id,t2.title t2_title,t2.version t2_version,t7.id t7_id,"
                + "t7.title t7_title,t7.version t7_version,t3.id t3_id,t3.title t3_title,t3.version t3_version,"
                + "t3.entity_eid t3_entity_eid,t3.entity_fid t3_entity_fid,t6.id t6_id,t6.title t6_title,t6.version t6_version,"
                + "t4.id t4_id,t4.title t4_title,t4.version t4_version,t4.entity_gid t4_entity_gid,t5.id t5_id,t5.title t5_title,"
                + "t5.version t5_version " + "FROM test_entity " + "INNER JOIN test_entity_a t1 ON test_entity.entity_aid=t1.id  "
                + "INNER JOIN test_entity_b t2 ON test_entity.entity_bid=t2.id  "
                + "INNER JOIN test_entity_D t7 ON test_entity.entity_did=t7.id  "
                + "INNER JOIN test_entity_c t3 ON test_entity.entity_cid=t3.id  " + "INNER JOIN test_entity_f t6 ON t3.entity_fid=t6.id  "
                + "INNER JOIN test_entity_e t4 ON t3.entity_eid=t4.id  " + "INNER JOIN test_entity_g t5 ON t4.entity_gid=t5.id ");
    }

    @Test
    public void test2() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).orderBy("id ASC")
                .join("#entityA, #entityB, #entityC {#entityE{#entityG},#entityF} ,#entityD").create();

        assertEquals(query1.getConfig().getSql(),
                "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,test_entity.string_field test_entity_string_field,"
                        + "test_entity.date_field test_entity_date_field,test_entity.short_field test_entity_short_field,"
                        + "test_entity.version test_entity_version,test_entity.entity_aid test_entity_entity_aid,"
                        + "test_entity.entity_bid test_entity_entity_bid,test_entity.entity_cid test_entity_entity_cid,"
                        + "test_entity.entity_did test_entity_entity_did,t1.id t1_id,t1.title t1_title,t1.version t1_version,"
                        + "t2.id t2_id,t2.title t2_title,t2.version t2_version,t7.id t7_id,t7.title t7_title,t7.version t7_version,"
                        + "t3.id t3_id,t3.title t3_title,t3.version t3_version,t3.entity_eid t3_entity_eid,t3.entity_fid t3_entity_fid,"
                        + "t6.id t6_id,t6.title t6_title,t6.version t6_version,t4.id t4_id,t4.title t4_title,t4.version t4_version,"
                        + "t4.entity_gid t4_entity_gid,t5.id t5_id,t5.title t5_title,t5.version t5_version " + "FROM test_entity "
                        + "LEFT JOIN test_entity_a t1 ON test_entity.entity_aid=t1.id  "
                        + "LEFT JOIN test_entity_b t2 ON test_entity.entity_bid=t2.id  "
                        + "LEFT JOIN test_entity_D t7 ON test_entity.entity_did=t7.id  "
                        + "LEFT JOIN test_entity_c t3 ON test_entity.entity_cid=t3.id  "
                        + "LEFT JOIN test_entity_f t6 ON t3.entity_fid=t6.id  " + "LEFT JOIN test_entity_e t4 ON t3.entity_eid=t4.id  "
                        + "LEFT JOIN test_entity_g t5 ON t4.entity_gid=t5.id  " + "ORDER BY test_entity.id ASC");
    }

    @Test
    public void test3() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("entityA, entityB, entityC {#entityE{#entityG},#entityF} ,entityD").create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,"
                + "test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,"
                + "test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,"
                + "test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,"
                + "test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_entity_did,"
                + "t1.id t1_id,t1.title t1_title,t1.version t1_version,t2.id t2_id,t2.title t2_title,t2.version t2_version,"
                + "t7.id t7_id,t7.title t7_title,t7.version t7_version,t3.id t3_id,t3.title t3_title,t3.version t3_version,"
                + "t3.entity_eid t3_entity_eid,t3.entity_fid t3_entity_fid,t6.id t6_id,t6.title t6_title,t6.version t6_version,"
                + "t4.id t4_id,t4.title t4_title,t4.version t4_version,t4.entity_gid t4_entity_gid,t5.id t5_id,t5.title t5_title,"
                + "t5.version t5_version " + "FROM test_entity INNER JOIN test_entity_a t1 ON test_entity.entity_aid=t1.id  "
                + "INNER JOIN test_entity_b t2 ON test_entity.entity_bid=t2.id  "
                + "INNER JOIN test_entity_D t7 ON test_entity.entity_did=t7.id  "
                + "INNER JOIN test_entity_c t3 ON test_entity.entity_cid=t3.id  " + "LEFT JOIN test_entity_f t6 ON t3.entity_fid=t6.id  "
                + "LEFT JOIN test_entity_e t4 ON t3.entity_eid=t4.id  " + "LEFT JOIN test_entity_g t5 ON t4.entity_gid=t5.id ");
    }

    @Test
    public void test4() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("#entityA, #entityB, #entityC {entityE{entityG},entityF} ,#entityD").create();

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
                + "LEFT JOIN test_entity_D t7 ON test_entity.entity_did=t7.id  "
                + "LEFT JOIN test_entity_c t3 ON test_entity.entity_cid=t3.id  " + "INNER JOIN test_entity_f t6 ON t3.entity_fid=t6.id  "
                + "INNER JOIN test_entity_e t4 ON t3.entity_eid=t4.id  " + "INNER JOIN test_entity_g t5 ON t4.entity_gid=t5.id ");
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
                + "t3.entity_eid t3_entity_eid,t3.entity_fid t3_entity_fid,t6.id t6_id,t6.title t6_title,t6.version t6_version,"
                + "t4.id t4_id,t4.title t4_title,t4.version t4_version,t4.entity_gid t4_entity_gid,t5.id t5_id,t5.title t5_title,"
                + "t5.version t5_version " + "FROM test_entity " + "LEFT JOIN test_entity_a t1 ON test_entity.entity_aid=t1.id  "
                + "LEFT JOIN test_entity_b t2 ON test_entity.entity_bid=t2.id  "
                + "LEFT JOIN test_entity_D t7 ON test_entity.entity_did=t7.id  "
                + "LEFT JOIN test_entity_c t3 ON test_entity.entity_cid=t3.id  " + "INNER JOIN test_entity_f t6 ON t3.entity_fid=t6.id  "
                + "INNER JOIN test_entity_e t4 ON t3.entity_eid=t4.id  " + "INNER JOIN test_entity_g t5 ON t4.entity_gid=t5.id ");
    }

    @Test
    public void test6() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("#entityA, #entityB, #entityC {entityE{entityG},entityF} ,#entityD").create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,"
                + "test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,"
                + "test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,"
                + "test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,"
                + "test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_entity_did,"
                + "t1.id t1_id,t1.title t1_title,t1.version t1_version,t2.id t2_id,t2.title t2_title,"
                + "t2.version t2_version,t7.id t7_id,t7.title t7_title,t7.version t7_version,t3.id t3_id,"
                + "t3.title t3_title,t3.version t3_version,t3.entity_eid t3_entity_eid,t3.entity_fid t3_entity_fid,"
                + "t6.id t6_id,t6.title t6_title,t6.version t6_version,t4.id t4_id,t4.title t4_title,"
                + "t4.version t4_version,t4.entity_gid t4_entity_gid,t5.id t5_id,t5.title t5_title,t5.version t5_version "
                + "FROM test_entity " + "LEFT JOIN test_entity_a t1 ON test_entity.entity_aid=t1.id  "
                + "LEFT JOIN test_entity_b t2 ON test_entity.entity_bid=t2.id  "
                + "LEFT JOIN test_entity_D t7 ON test_entity.entity_did=t7.id  "
                + "LEFT JOIN test_entity_c t3 ON test_entity.entity_cid=t3.id  " + "INNER JOIN test_entity_f t6 ON t3.entity_fid=t6.id  "
                + "INNER JOIN test_entity_e t4 ON t3.entity_eid=t4.id  " + "INNER JOIN test_entity_g t5 ON t4.entity_gid=t5.id ");
    }

    @Test
    public void test7() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("entityA, entityB, entityC {entityE{entityG},entityF} ,entityD")
                .where(Condition.and(Condition.between("dateField"), Condition.isEqual("id"))).orderBy("dateField ASC").create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,"
                + "test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,"
                + "test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,"
                + "test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,"
                + "test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_entity_did,"
                + "t1.id t1_id,t1.title t1_title,t1.version t1_version,t2.id t2_id,t2.title t2_title,t2.version t2_version,"
                + "t7.id t7_id,t7.title t7_title,t7.version t7_version,t3.id t3_id,t3.title t3_title,t3.version t3_version,"
                + "t3.entity_eid t3_entity_eid,t3.entity_fid t3_entity_fid,t6.id t6_id,t6.title t6_title,t6.version t6_version,"
                + "t4.id t4_id,t4.title t4_title,t4.version t4_version,t4.entity_gid t4_entity_gid,t5.id t5_id,"
                + "t5.title t5_title,t5.version t5_version " + "FROM test_entity "
                + "INNER JOIN test_entity_a t1 ON test_entity.entity_aid=t1.id  "
                + "INNER JOIN test_entity_b t2 ON test_entity.entity_bid=t2.id  "
                + "INNER JOIN test_entity_D t7 ON test_entity.entity_did=t7.id  "
                + "INNER JOIN test_entity_c t3 ON test_entity.entity_cid=t3.id  " + "INNER JOIN test_entity_f t6 ON t3.entity_fid=t6.id  "
                + "INNER JOIN test_entity_e t4 ON t3.entity_eid=t4.id  " + "INNER JOIN test_entity_g t5 ON t4.entity_gid=t5.id  "
                + "WHERE (test_entity.date_field BETWEEN ? AND ?) AND (test_entity.id = ?) " + "ORDER BY test_entity.date_field ASC");
    }

    @Test
    public void test8() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class)
                .join("entityA, entityB, entityC {entityE{entityG},entityF} ,entityD").where(Condition
                        .and(Condition.isEqual("entityA.title"), Condition.isEqual("entityB.title"), Condition.isEqual("entityC.title")))
                .orderBy("dateField ASC").create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,"
                + "test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,"
                + "test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,"
                + "test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,"
                + "test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_entity_did,"
                + "t1.id t1_id,t1.title t1_title,t1.version t1_version,t2.id t2_id,t2.title t2_title,t2.version t2_version,"
                + "t7.id t7_id,t7.title t7_title,t7.version t7_version,t3.id t3_id,t3.title t3_title,t3.version t3_version,"
                + "t3.entity_eid t3_entity_eid,t3.entity_fid t3_entity_fid,t6.id t6_id,t6.title t6_title,t6.version t6_version,"
                + "t4.id t4_id,t4.title t4_title,t4.version t4_version,t4.entity_gid t4_entity_gid,t5.id t5_id,t5.title t5_title,"
                + "t5.version t5_version " + "FROM test_entity " + "INNER JOIN test_entity_a t1 ON test_entity.entity_aid=t1.id  "
                + "INNER JOIN test_entity_b t2 ON test_entity.entity_bid=t2.id  "
                + "INNER JOIN test_entity_D t7 ON test_entity.entity_did=t7.id  "
                + "INNER JOIN test_entity_c t3 ON test_entity.entity_cid=t3.id  " + "INNER JOIN test_entity_f t6 ON t3.entity_fid=t6.id  "
                + "INNER JOIN test_entity_e t4 ON t3.entity_eid=t4.id  " + "INNER JOIN test_entity_g t5 ON t4.entity_gid=t5.id  "
                + "WHERE (t1.title = ?) AND (t2.title = ?) AND (t3.title = ?) " + "ORDER BY test_entity.date_field ASC");
    }

    @Test
    public void test9() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).where(Condition
                .and(Condition.isEqual("entityA.title"), Condition.isEqual("entityB.title"), Condition.isEqual("entityC.title"),
                        Condition.isEqual("entityC.entityE.entityG.title"))).orderBy("entityC.entityF.title ASC").create();

        assertEquals(query1.getConfig().getSql(), "SELECT test_entity.id test_entity_id,test_entity.int_field test_entity_int_field,"
                + "test_entity.string_field test_entity_string_field,test_entity.date_field test_entity_date_field,"
                + "test_entity.short_field test_entity_short_field,test_entity.version test_entity_version,"
                + "test_entity.entity_aid test_entity_entity_aid,test_entity.entity_bid test_entity_entity_bid,"
                + "test_entity.entity_cid test_entity_entity_cid,test_entity.entity_did test_entity_entity_did,t1.id t1_id,"
                + "t1.title t1_title,t1.version t1_version,t2.id t2_id,t2.title t2_title,t2.version t2_version,t3.id t3_id,"
                + "t3.title t3_title,t3.version t3_version,t3.entity_eid t3_entity_eid,t3.entity_fid t3_entity_fid,t6.id t6_id,"
                + "t6.title t6_title,t6.version t6_version,t4.id t4_id,t4.title t4_title,t4.version t4_version,"
                + "t4.entity_gid t4_entity_gid,t5.id t5_id,t5.title t5_title,t5.version t5_version " + "FROM test_entity "
                + "INNER JOIN test_entity_a t1 ON test_entity.entity_aid=t1.id  "
                + "INNER JOIN test_entity_b t2 ON test_entity.entity_bid=t2.id  "
                + "INNER JOIN test_entity_c t3 ON test_entity.entity_cid=t3.id  " + "INNER JOIN test_entity_f t6 ON t3.entity_fid=t6.id  "
                + "INNER JOIN test_entity_e t4 ON t3.entity_eid=t4.id  " + "INNER JOIN test_entity_g t5 ON t4.entity_gid=t5.id  "
                + "WHERE (t1.title = ?) AND (t2.title = ?) AND (t3.title = ?) AND (t5.title = ?) " + "ORDER BY t6.title ASC");
    }

}
