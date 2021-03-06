package ru.kwanza.dbtool.orm.impl.querybuilder;

/*
 * #%L
 * dbtool-orm
 * %%
 * Copyright (C) 2015 Kwanza
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.orm.api.If;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.impl.fetcher.TestEntity;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingRegistry;

import javax.annotation.Resource;

import static junit.framework.Assert.assertEquals;

/**
 * @author Alexander Guzanov
 */

@ContextConfiguration(locations = "mysql-querybuilder-config.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class MySQLQueryBuilderTest extends AbstractJUnit4SpringContextTests {
    @Resource(name = "dbtool.IEntityManager")
    private IEntityManager em;
    @Resource(name = "dbtool.IEntityMappingRegistry")
    private EntityMappingRegistry registry;

    @Test
    public void testBuildConditions() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).where(If
                .and(If.in("id"), If.like("id"), If.isEqual("id"), If.isGreater("id"), If.isGreaterOrEqual("id"), If.isLess("id"),
                        If.isLessOrEqual("id"), If.isNotNull("id"), If.isNull("id"), If.between("id"), If.notEqual("id"),
                        If.not(If.notEqual("id")), If.createNative("Exists(select * from test_entity where id=:id)"))).orderBy("id").orderBy("stringField DESC")
                .create();

        assertEquals(query1.getConfig().getSql(),
                "SELECT id,int_field,string_field,date_field,short_field,version,entity_aid,entity_bid,entity_cid,entity_did " +
                        "FROM test_entity " +
                        "WHERE (test_entity.id IN (?)) AND (test_entity.id LIKE ?) AND (test_entity.id = ?) AND (test_entity.id > ?) AND (test_entity.id >= ?) AND (test_entity.id < ?)" +
                        " AND (test_entity.id <= ?) AND (test_entity.id IS NOT NULL) AND (test_entity.id IS NULL) AND (test_entity.id BETWEEN ? AND ?) AND (test_entity.id <> ?)" +
                        " AND (NOT (test_entity.id <> ?)) AND (Exists(select * from test_entity where id=?)) ORDER BY test_entity.id ASC,test_entity.string_field DESC");

        assertEquals(query1.getConfig().getParamsHolder().getCount(),12);
        AbstractQuery<TestEntity> query2 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).where(If
                .or(If.in("id"), If.like("id"), If.isEqual("id"), If.isGreater("id"), If.isGreaterOrEqual("id"), If.isLess("id"),
                        If.isLessOrEqual("id"), If.isNotNull("id"), If.isNull("id"), If.between("id"), If.notEqual("id"), If.notEqual("id"))).orderBy("id")
                .orderBy("stringField DESC").create();

        assertEquals(query2.getConfig().getSql(),"SELECT id,int_field,string_field,date_field,short_field," +
                "version,entity_aid,entity_bid,entity_cid,entity_did FROM test_entity " +
                "WHERE (test_entity.id IN (?)) OR (test_entity.id LIKE ?) OR (test_entity.id = ?) OR (test_entity.id > ?) " +
                "OR (test_entity.id >= ?) OR (test_entity.id < ?) OR (test_entity.id <= ?) OR (test_entity.id IS NOT NULL) OR (test_entity.id IS NULL) " +
                "OR (test_entity.id BETWEEN ? AND ?) OR (test_entity.id <> ?) OR (test_entity.id <> ?) ORDER BY test_entity.id ASC,test_entity.string_field DESC");

        assertEquals(query1.getConfig().getParamsHolder().getCount(),12);
        AbstractQuery<TestEntity> query3 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).where(
                If.and(If.not(If.or(If.in("id"), If.like("id"), If.isEqual("id"), If.isGreater("id"))),
                        If.or(If.isGreaterOrEqual("id"), If.isLess("id"), If.isLessOrEqual("id"), If.isNotNull("id"), If.isNull("id")),
                        If.between("id"), If.or(If.notEqual("id"), If.notEqual("id"))))
                .orderBy("id").orderBy("stringField DESC").create();
        assertEquals(query3.getConfig().getSql(),"SELECT id,int_field,string_field,date_field,short_field," +
                "version,entity_aid,entity_bid,entity_cid,entity_did " +
                "FROM test_entity " +
                "WHERE " +
                "(NOT ((test_entity.id IN (?)) OR (test_entity.id LIKE ?) OR (test_entity.id = ?) OR (test_entity.id > ?))) " +
                "AND" +
                " ((test_entity.id >= ?) OR (test_entity.id < ?) OR (test_entity.id <= ?) OR (test_entity.id IS NOT NULL) OR (test_entity.id IS NULL)) " +
                "AND" +
                " (test_entity.id BETWEEN ? AND ?) " +
                "AND" +
                " ((test_entity.id <> ?) OR (test_entity.id <> ?)) " +
                "ORDER BY test_entity.id ASC,test_entity.string_field DESC");
        assertEquals(query3.getConfig().getParamsHolder().getCount(),11);
    }

    @Test
    public void testBuildConditionsWithLimit() {
        AbstractQuery<TestEntity> query1 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).where(If
                .and(If.in("id"), If.like("id"), If.isEqual("id"), If.isGreater("id"), If.isGreaterOrEqual("id"), If.isLess("id"),
                        If.isLessOrEqual("id"), If.isNotNull("id"), If.isNull("id"), If.between("id"), If.notEqual("id"), If.notEqual("id"))).orderBy("id")
                .orderBy("stringField DESC").create();

        assertEquals(query1.getConfig().getSql(),
                "SELECT id,int_field,string_field,date_field,short_field,version,entity_aid,entity_bid,entity_cid,entity_did " +
                        "FROM test_entity " +
                        "WHERE (test_entity.id IN (?)) AND (test_entity.id LIKE ?) AND (test_entity.id = ?) AND (test_entity.id > ?) AND (test_entity.id >= ?) AND (test_entity.id < ?)" +
                        " AND (test_entity.id <= ?) AND (test_entity.id IS NOT NULL) AND (test_entity.id IS NULL) AND (test_entity.id BETWEEN ? AND ?) AND (test_entity.id <> ?)" +
                        " AND (test_entity.id <> ?) ORDER BY test_entity.id ASC,test_entity.string_field DESC");

        assertEquals(query1.getConfig().getParamsHolder().getCount(),11);

        AbstractQuery<TestEntity> query2 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).where(If
                .or(If.in("id"), If.like("id"), If.isEqual("id"), If.isGreater("id"), If.isGreaterOrEqual("id"), If.isLess("id"),
                        If.isLessOrEqual("id"), If.isNotNull("id"), If.isNull("id"), If.between("id"), If.notEqual("id"), If.notEqual("id"))).orderBy("id")
                .orderBy("stringField DESC").create();

        assertEquals(query2.getConfig().getSql(),"SELECT id,int_field,string_field,date_field,short_field," +
                "version,entity_aid,entity_bid,entity_cid,entity_did FROM test_entity " +
                "WHERE (test_entity.id IN (?)) OR (test_entity.id LIKE ?) OR (test_entity.id = ?) OR (test_entity.id > ?) " +
                "OR (test_entity.id >= ?) OR (test_entity.id < ?) OR (test_entity.id <= ?) OR (test_entity.id IS NOT NULL) OR (test_entity.id IS NULL) " +
                "OR (test_entity.id BETWEEN ? AND ?) OR (test_entity.id <> ?) OR (test_entity.id <> ?) ORDER BY test_entity.id ASC,test_entity.string_field DESC");
        assertEquals(query1.getConfig().getParamsHolder().getCount(),11);

        AbstractQuery<TestEntity> query3 = (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).where(If
                .and(If.or(If.in("id"), If.like("id"), If.isEqual("id"), If.isGreater("id")),
                        If.or(If.isGreaterOrEqual("id"), If.isLess("id"), If.isLessOrEqual("id"), If.isNotNull("id"), If.isNull("id")),
                        If.between("id"), If.or(If.notEqual("id"), If.notEqual("id")))).orderBy("id").orderBy("stringField DESC")
                .create();
        assertEquals(query3.getConfig().getSql(),"SELECT id,int_field,string_field,date_field,short_field," +
                "version,entity_aid,entity_bid,entity_cid,entity_did " +
                "FROM test_entity " +
                "WHERE " +
                "((test_entity.id IN (?)) OR (test_entity.id LIKE ?) OR (test_entity.id = ?) OR (test_entity.id > ?)) " +
                "AND" +
                " ((test_entity.id >= ?) OR (test_entity.id < ?) OR (test_entity.id <= ?) OR (test_entity.id IS NOT NULL) OR (test_entity.id IS NULL)) " +
                "AND" +
                " (test_entity.id BETWEEN ? AND ?) " +
                "AND" +
                " ((test_entity.id <> ?) OR (test_entity.id <> ?)) " +
                "ORDER BY test_entity.id ASC,test_entity.string_field DESC");
        assertEquals(query1.getConfig().getParamsHolder().getCount(),11);
    }

    @Test
    public void testBuildWithouCondition() {
        AbstractQuery<TestEntity> query1 =
                (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).orderBy("id").orderBy("stringField DESC").create();
        assertEquals(query1.getConfig().getSql(),"SELECT id,int_field,string_field,date_field,short_field," +
                "version,entity_aid,entity_bid,entity_cid,entity_did " +
                "FROM test_entity " +
                "ORDER BY test_entity.id ASC,test_entity.string_field DESC");
    }

    @Test(expected = IllegalStateException.class)
    public void testBadBuild_where() {
        AbstractQuery<TestEntity> query1 =
                (AbstractQuery<TestEntity>) em.queryBuilder(TestEntity.class).where(If.like("id")).where(If.like("id"))
                        .orderBy("id").orderBy("stringField DESC").create();
    }
}
