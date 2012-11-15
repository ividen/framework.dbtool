package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.orm.api.Condition;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.api.OrderBy;
import ru.kwanza.dbtool.orm.impl.fetcher.TestEntity;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingRegistryImpl;

import javax.annotation.Resource;

import static junit.framework.Assert.assertEquals;

/**
 * @author Alexander Guzanov
 */

@ContextConfiguration(locations = "oracle-config.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OracleQueryBuilderTest extends AbstractJUnit4SpringContextTests {
    @Resource(name = "dbtool.IEntityManager")
    private IEntityManager em;
    @Resource(name = "dbtool.IEntityMappingRegistry")
    private EntityMappingRegistryImpl registry;

    @Before
    public void init() {
        registry.registerEntityClass(TestEntity.class);
    }

    @Test
    public void testBuildConditions() {
        QueryImpl<TestEntity> query1 = (QueryImpl<TestEntity>) em.queryBuilder(TestEntity.class)
                .where(Condition.and(
                        Condition.in("id"),
                        Condition.like("id"),
                        Condition.isEqual("id"),
                        Condition.isGreater("id"),
                        Condition.isGreaterOrEqual("id"),
                        Condition.isLess("id"),
                        Condition.isLessOrEqual("id"),
                        Condition.isNotNull("id"),
                        Condition.isNull("id"),
                        Condition.between("id"),
                        Condition.notEqual("id"),
                        Condition.notEqual("id")
                )).orderBy(OrderBy.ASC("id"), OrderBy.DESC("stringField")).create();

        assertEquals(query1.getSql(),
                "SELECT id, int_field, string_field, date_field, short_field, version, entity_aid, entity_bid, entity_cid, entity_did " +
                        "FROM test_entity " +
                        "WHERE (id IN (?)) AND (id LIKE ?) AND (id = ?) AND (id > ?) AND (id >= ?) AND (id < ?)" +
                        " AND (id <= ?) AND (id IS NOT NULL) AND (id IS NULL) AND (id BETWEEN ? AND ?) AND (id <> ?)" +
                        " AND (id <> ?) ORDER BY id ASC, string_field DESC ");

        assertEquals(query1.getParamsCount(), 11);
        QueryImpl<TestEntity> query2 = (QueryImpl<TestEntity>) em.queryBuilder(TestEntity.class)
                .where(Condition.or(Condition.in("id"),
                        Condition.like("id"),
                        Condition.isEqual("id"),
                        Condition.isGreater("id"),
                        Condition.isGreaterOrEqual("id"),
                        Condition.isLess("id"),
                        Condition.isLessOrEqual("id"),
                        Condition.isNotNull("id"),
                        Condition.isNull("id"),
                        Condition.between("id"),
                        Condition.notEqual("id"),
                        Condition.notEqual("id")
                )).orderBy(OrderBy.ASC("id"), OrderBy.DESC("stringField")).create();

        assertEquals(query2.getSql(),
                "SELECT id, int_field, string_field, date_field, short_field, " +
                        "version, entity_aid, entity_bid, entity_cid, entity_did FROM test_entity " +
                        "WHERE (id IN (?)) OR (id LIKE ?) OR (id = ?) OR (id > ?) " +
                        "OR (id >= ?) OR (id < ?) OR (id <= ?) OR (id IS NOT NULL) OR (id IS NULL) " +
                        "OR (id BETWEEN ? AND ?) OR (id <> ?) OR (id <> ?) ORDER BY id ASC, string_field DESC ");

        assertEquals(query1.getParamsCount(), 11);
        QueryImpl<TestEntity> query3 = (QueryImpl<TestEntity>) em.queryBuilder(TestEntity.class)
                .where(Condition.and(
                        Condition.or(Condition.in("id"),
                                Condition.like("id"),
                                Condition.isEqual("id"),
                                Condition.isGreater("id")),
                        Condition.or(
                                Condition.isGreaterOrEqual("id"),
                                Condition.isLess("id"),
                                Condition.isLessOrEqual("id"),
                                Condition.isNotNull("id"),
                                Condition.isNull("id")),
                        Condition.between("id"),
                        Condition.or(
                                Condition.notEqual("id"),
                                Condition.notEqual("id"))
                )).orderBy(OrderBy.ASC("id"), OrderBy.DESC("stringField")).create();
        assertEquals(query3.getSql(),
                "SELECT id, int_field, string_field, date_field, short_field, " +
                        "version, entity_aid, entity_bid, entity_cid, entity_did " +
                        "FROM test_entity " +
                        "WHERE " +
                        "((id IN (?)) OR (id LIKE ?) OR (id = ?) OR (id > ?)) " +
                        "AND" +
                        " ((id >= ?) OR (id < ?) OR (id <= ?) OR (id IS NOT NULL) OR (id IS NULL)) " +
                        "AND" +
                        " (id BETWEEN ? AND ?) " +
                        "AND" +
                        " ((id <> ?) OR (id <> ?)) " +
                        "ORDER BY id ASC, string_field DESC ");
        assertEquals(query1.getParamsCount(), 11);
    }

    @Test
    public void testBuildConditionsWithLimit() {
        QueryImpl<TestEntity> query1 = (QueryImpl<TestEntity>) em.queryBuilder(TestEntity.class)
                .setMaxSize(1000)
                .where(Condition.and(Condition.in("id"),
                        Condition.like("id"),
                        Condition.isEqual("id"),
                        Condition.isGreater("id"),
                        Condition.isGreaterOrEqual("id"),
                        Condition.isLess("id"),
                        Condition.isLessOrEqual("id"),
                        Condition.isNotNull("id"),
                        Condition.isNull("id"),
                        Condition.between("id"),
                        Condition.notEqual("id"),
                        Condition.notEqual("id")
                )).orderBy(OrderBy.ASC("id"), OrderBy.DESC("stringField")).create();

        assertEquals(query1.getSql(),
                "SELECT  * FROM (SELECT id, int_field, string_field, date_field, short_field, version, entity_aid, entity_bid, entity_cid, entity_did " +
                        "FROM test_entity " +
                        "WHERE (id IN (?)) AND (id LIKE ?) AND (id = ?) AND (id > ?) AND (id >= ?) AND (id < ?)" +
                        " AND (id <= ?) AND (id IS NOT NULL) AND (id IS NULL) AND (id BETWEEN ? AND ?) AND (id <> ?)" +
                        " AND (id <> ?) ORDER BY id ASC, string_field DESC ) WHERE rownum < ?");

        assertEquals(query1.getParamsCount(), 11);

        QueryImpl<TestEntity> query2 = (QueryImpl<TestEntity>) em.queryBuilder(TestEntity.class)
                .setMaxSize(1000)
                .where(Condition.or(Condition.in("id"),
                        Condition.like("id"),
                        Condition.isEqual("id"),
                        Condition.isGreater("id"),
                        Condition.isGreaterOrEqual("id"),
                        Condition.isLess("id"),
                        Condition.isLessOrEqual("id"),
                        Condition.isNotNull("id"),
                        Condition.isNull("id"),
                        Condition.between("id"),
                        Condition.notEqual("id"),
                        Condition.notEqual("id")
                )).orderBy(OrderBy.ASC("id"), OrderBy.DESC("stringField")).create();


        assertEquals(query2.getSql(),
                "SELECT  * FROM (SELECT id, int_field, string_field, date_field, short_field, " +
                        "version, entity_aid, entity_bid, entity_cid, entity_did FROM test_entity " +
                        "WHERE (id IN (?)) OR (id LIKE ?) OR (id = ?) OR (id > ?) " +
                        "OR (id >= ?) OR (id < ?) OR (id <= ?) OR (id IS NOT NULL) OR (id IS NULL) " +
                        "OR (id BETWEEN ? AND ?) OR (id <> ?) OR (id <> ?) ORDER BY id ASC, string_field DESC ) WHERE rownum < ?");
        assertEquals(query1.getParamsCount(), 11);

        QueryImpl<TestEntity> query3 = (QueryImpl<TestEntity>) em.queryBuilder(TestEntity.class)
                .setMaxSize(1000)
                .where(Condition.and(
                        Condition.or(Condition.in("id"),
                                Condition.like("id"),
                                Condition.isEqual("id"),
                                Condition.isGreater("id")),
                        Condition.or(
                                Condition.isGreaterOrEqual("id"),
                                Condition.isLess("id"),
                                Condition.isLessOrEqual("id"),
                                Condition.isNotNull("id"),
                                Condition.isNull("id")),
                        Condition.between("id"),
                        Condition.or(
                                Condition.notEqual("id"),
                                Condition.notEqual("id"))
                )).orderBy(OrderBy.ASC("id"), OrderBy.DESC("stringField")).create();
        assertEquals(query3.getSql(),
                "SELECT  * FROM (SELECT id, int_field, string_field, date_field, short_field, " +
                        "version, entity_aid, entity_bid, entity_cid, entity_did " +
                        "FROM test_entity " +
                        "WHERE " +
                        "((id IN (?)) OR (id LIKE ?) OR (id = ?) OR (id > ?)) " +
                        "AND" +
                        " ((id >= ?) OR (id < ?) OR (id <= ?) OR (id IS NOT NULL) OR (id IS NULL)) " +
                        "AND" +
                        " (id BETWEEN ? AND ?) " +
                        "AND" +
                        " ((id <> ?) OR (id <> ?)) " +
                        "ORDER BY id ASC, string_field DESC ) WHERE rownum < ?");
        assertEquals(query1.getParamsCount(), 11);
    }

    @Test
    public void testBuildWithouCondition() {
        QueryImpl<TestEntity> query1 = (QueryImpl<TestEntity>) em.queryBuilder(TestEntity.class)
                .setOffset(100)
                .orderBy(OrderBy.ASC("id"), OrderBy.DESC("stringField")).create();
        assertEquals(query1.getSql(),
                "SELECT id, int_field, string_field, date_field, short_field, " +
                        "version, entity_aid, entity_bid, entity_cid, entity_did " +
                        "FROM test_entity " +
                        "ORDER BY id ASC, string_field DESC ");
    }

    @Test
    @ExpectedException(IllegalStateException.class)
    public void testBadBuild_where() {
        QueryImpl<TestEntity> query1 = (QueryImpl<TestEntity>) em.queryBuilder(TestEntity.class)
                .setOffset(100)
                .where(Condition.like("id"))
                .where(Condition.like("id"))
                .orderBy(OrderBy.ASC("id"), OrderBy.DESC("stringField")).create();
    }

    @Test
    @ExpectedException(IllegalStateException.class)
    public void testBadBuild_order() {
        QueryImpl<TestEntity> query1 = (QueryImpl<TestEntity>) em.queryBuilder(TestEntity.class)
                .setOffset(100)
                .where(Condition.like("id"))
                .orderBy(OrderBy.ASC("id"))
                .orderBy(OrderBy.DESC("stringField")).create();
    }
}
