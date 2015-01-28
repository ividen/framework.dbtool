package ru.kwanza.dbtool.orm.impl.mapping;

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

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;
import ru.kwanza.dbtool.orm.impl.mapping.entities.*;
import ru.kwanza.toolbox.fieldhelper.FieldHelper;
import ru.kwanza.toolbox.fieldhelper.Property;

import javax.annotation.Resource;
import java.sql.Types;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author Kiryl Karatsetski
 */
@ContextConfiguration(locations = "dbtool-orm-mapping-test-config.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class EntityMappingTest extends AbstractJUnit4SpringContextTests {

    private static final Class PAYMENT_TRX_CLASS = PaymentTrx.class;
    private static final String PAYMENT_TRX_NAME = "PaymentTrx";
    private static final String PAYMENT_TRX_TABLE_NAME = "payment_trx";

    private static final Collection<String> PAYMENT_TRX_COLUMNS = new LinkedHashSet<String>() {{
        add("id");
        add("pcid");
        add("version");
        add("parent_trx_id");
        add("started_at");
        add("finished_at");
        add("result_code");
        add("extended_code");
        add("amount");
        add("agent_id");
        add("description");
    }};

    private static final Collection<String> PAYMENT_TRX_FIELD_PROPERTY_NAMES = new LinkedHashSet<String>() {{
        add("id");
        add("pcid");
        add("version");
        add("parentTrxId");
        add("startedAt");
        add("finishedAt");
        add("resultCode");
        add("extendedCode");
        add("amount");
        add("agentId");
        add("description");
    }};

    private static final Collection<String> PAYMENT_TRX_FETCH_PROPERTY_NAMES = new LinkedHashSet<String>() {{
        add("agent");
    }};

    private static final int PAYMENT_TRX_COLUMN_COUNT = PAYMENT_TRX_COLUMNS.size();

    private static final int PAYMENT_TRX_ID_FIELD_COUNT = 1;
    private static final int PAYMENT_TRX_FETCH_MAPPING_COUNT = 1;

    private static final String AMOUNT = "amount";
    private static final String AGENT = "agent";

    @Resource(name = "dbtool.IEntityMappingRegistry")
    private IEntityMappingRegistry entityMappingRegistry;

    @Before
    public void registerPaymentTrx() {
        entityMappingRegistry.registerEntityClass(PaymentTrx.class);
        entityMappingRegistry.registerEntityClass(Agent.class);
    }

    @Test()
    public void validateCorrectEntityMappingTest() throws Exception {
        entityMappingRegistry.registerEntityClass(Agent.class);
    }

    @Test
    public void registerEntityTest() throws Exception {
        assertEquals(PaymentTrx.class, entityMappingRegistry.getEntityType(PAYMENT_TRX_NAME).getEntityClass());
        assertEquals(PAYMENT_TRX_NAME, entityMappingRegistry.getEntityType(PAYMENT_TRX_CLASS).getName());
        assertEquals(PAYMENT_TRX_TABLE_NAME, entityMappingRegistry.getEntityType(PAYMENT_TRX_CLASS).getTableName());
        assertEquals(PAYMENT_TRX_TABLE_NAME, entityMappingRegistry.getEntityType(PAYMENT_TRX_NAME).getTableName());
    }

    @Test()
    public void registerDuplicateEntityTest() throws Exception {
        registerPaymentTrx();
    }

    @Test(expected = RuntimeException.class)
    public void registerNotEntityTest() throws Exception {
        entityMappingRegistry.registerEntityClass(NotEntity.class);
    }

    @Test(expected = RuntimeException.class)
    public void registerEntityWithDuplicateColumnTest() throws Exception {
        entityMappingRegistry.registerEntityClass(EntityWithDuplicateColumn.class);
    }

    @Test(expected = RuntimeException.class)
    public void registerEntityWithDuplicateFieldPropertyTest() throws Exception {
        entityMappingRegistry.registerEntityClass(EntityWithDuplicateFieldProperty.class);
    }

    @Test(expected = RuntimeException.class)
    public void registerEntityWithDuplicateFetchPropertyTest() throws Exception {
        entityMappingRegistry.registerEntityClass(EntityWithDuplicateFetchProperty.class);
    }

    @Test
    public void getColumnNamesTest() throws Exception {
        final Collection<String> columnNamesByEntityClass = FieldHelper
                .getFieldCollection(entityMappingRegistry.getEntityType(PAYMENT_TRX_CLASS).getFields(),
                        FieldHelper.<FieldMapping,String>construct(FieldMapping.class, "column"));

        final Collection<String> columnNamesByEntityName = FieldHelper
                .getFieldCollection(entityMappingRegistry.getEntityType(PAYMENT_TRX_NAME).getFields(),
                        FieldHelper.<FieldMapping,String>construct(FieldMapping.class, "column"));

        assertNotNull(columnNamesByEntityClass);
        assertNotNull(columnNamesByEntityName);


        for (String s : columnNamesByEntityName) {
            assertTrue(PAYMENT_TRX_COLUMNS.contains(s));
        }

        for (String s : columnNamesByEntityClass) {
            assertTrue(PAYMENT_TRX_COLUMNS.contains(s));
        }
    }

    @Test
    public void getFieldMappingTest() throws Exception {
        final Collection<IFieldMapping> fieldMappingByEntityClass = entityMappingRegistry.getEntityType(PAYMENT_TRX_CLASS).getFields();
        final Collection<IFieldMapping> fieldMappingByEntityName = entityMappingRegistry.getEntityType(PAYMENT_TRX_NAME).getFields();

        assertNotNull(fieldMappingByEntityClass);
        assertNotNull(fieldMappingByEntityName);

        assertEqualsCollection(fieldMappingByEntityClass, fieldMappingByEntityName);

        assertEquals(PAYMENT_TRX_COLUMN_COUNT, fieldMappingByEntityClass.size());
        assertEquals(PAYMENT_TRX_COLUMN_COUNT, fieldMappingByEntityName.size());
    }

    @Test
    public void getIdFieldsTest() throws Exception {
        final IFieldMapping idFieldsByEntityClass = entityMappingRegistry.getEntityType(PAYMENT_TRX_CLASS).getIdField();
        IFieldMapping idFieldsByEntityName = entityMappingRegistry.getEntityType(PAYMENT_TRX_NAME).getIdField();

        assertNotNull(idFieldsByEntityClass);
        assertNotNull(idFieldsByEntityName);

        assertEquals(idFieldsByEntityClass, idFieldsByEntityName);

        assertEquals("id", idFieldsByEntityClass.getColumn());
        assertEquals("id", idFieldsByEntityName.getColumn());
    }

    @Test
    public void getVersionFieldTest() throws Exception {
        final IFieldMapping versionFieldByEntityClass = entityMappingRegistry.getEntityType(PAYMENT_TRX_CLASS).getVersionField();
        final IFieldMapping versionFieldByEntityName = entityMappingRegistry.getEntityType(PAYMENT_TRX_NAME).getVersionField();

        assertNotNull(versionFieldByEntityClass);
        assertNotNull(versionFieldByEntityName);

        assertEquals(versionFieldByEntityClass, versionFieldByEntityName);
    }

    @Test
    public void getFetchMappingTest() throws Exception {
        final Collection<IRelationMapping> relationMappingByEntityClases =
                entityMappingRegistry.getEntityType(PAYMENT_TRX_CLASS).getRelations();
        final Collection<IRelationMapping> relationMappingByEntityName =
                entityMappingRegistry.getEntityType(PAYMENT_TRX_NAME).getRelations();

        assertNotNull(relationMappingByEntityClases);
        assertNotNull(relationMappingByEntityName);

        assertEqualsCollection(relationMappingByEntityClases, relationMappingByEntityName);

        assertEquals(PAYMENT_TRX_FETCH_MAPPING_COUNT, relationMappingByEntityClases.size());
        assertEquals(PAYMENT_TRX_FETCH_MAPPING_COUNT, relationMappingByEntityName.size());
    }

    private  void assertEqualsCollection(Collection c1, Collection c2){
        for (Object o : c1) {
            assertTrue(c2.contains(o));
        }

        for (Object o : c2) {
            assertTrue(c1.contains(o));
        }
    }

    @Test
    public void getFieldMappingByPropertyNameTest() throws Exception {
        final Collection<IFieldMapping> fieldMappingByEntityClass = entityMappingRegistry.getEntityType(PAYMENT_TRX_CLASS).getFields();
        final Collection<IFieldMapping> fieldMappingByEntityName = entityMappingRegistry.getEntityType(PAYMENT_TRX_NAME).getFields();

        assertNotNull(fieldMappingByEntityClass);
        assertNotNull(fieldMappingByEntityName);

        final Iterator<IFieldMapping> iteratorByEntityClass = fieldMappingByEntityClass.iterator();
        final Iterator<IFieldMapping> iteratorByEntityName = fieldMappingByEntityName.iterator();

        for (String propertyName : PAYMENT_TRX_FIELD_PROPERTY_NAMES) {

            final IFieldMapping expectedFieldMappingByEntityClass = iteratorByEntityClass.next();
            final IFieldMapping expectedFieldMappingByEntityName = iteratorByEntityName.next();

            assertEquals(propertyName, expectedFieldMappingByEntityClass.getName());
            assertEquals(propertyName, expectedFieldMappingByEntityName.getName());

            final IFieldMapping fieldMappingByPropertyNameByEntityClass =
                    entityMappingRegistry.getEntityType(PAYMENT_TRX_CLASS).getField(propertyName);
            final IFieldMapping fieldMappingByPropertyNameByEntityName =
                    entityMappingRegistry.getEntityType(PAYMENT_TRX_NAME).getField(propertyName);

            assertNotNull(fieldMappingByPropertyNameByEntityClass);
            assertNotNull(fieldMappingByPropertyNameByEntityName);

            assertEquals(fieldMappingByPropertyNameByEntityClass, fieldMappingByPropertyNameByEntityName);

            assertEquals(expectedFieldMappingByEntityClass, fieldMappingByPropertyNameByEntityClass);
            assertEquals(expectedFieldMappingByEntityName, fieldMappingByPropertyNameByEntityName);
        }
    }

    @Test
    public void getFetchMappingByPropertyNameTest() throws Exception {
        final Collection<IRelationMapping> relationMappingByEntityClases =
                entityMappingRegistry.getEntityType(PAYMENT_TRX_CLASS).getRelations();
        final Collection<IRelationMapping> relationMappingByEntityName =
                entityMappingRegistry.getEntityType(PAYMENT_TRX_NAME).getRelations();

        assertNotNull(relationMappingByEntityClases);
        assertNotNull(relationMappingByEntityName);

        final Iterator<IRelationMapping> iteratorByEntityClass = relationMappingByEntityClases.iterator();
        final Iterator<IRelationMapping> iteratorByEntityName = relationMappingByEntityName.iterator();

        for (String propertyName : PAYMENT_TRX_FETCH_PROPERTY_NAMES) {

            final IRelationMapping expectedRelationMappingByEntityClass = iteratorByEntityClass.next();
            final IRelationMapping expectedRelationMappingByEntityName = iteratorByEntityName.next();

            assertEquals(propertyName, expectedRelationMappingByEntityClass.getName());
            assertEquals(propertyName, expectedRelationMappingByEntityName.getName());

            final IRelationMapping relationMappingByPropertyNameByEntityClass =
                    entityMappingRegistry.getEntityType(PAYMENT_TRX_CLASS).getRelation(propertyName);
            final IRelationMapping relationMappingByPropertyNameByEntityName =
                    entityMappingRegistry.getEntityType(PAYMENT_TRX_NAME).getRelation(propertyName);

            assertNotNull(relationMappingByPropertyNameByEntityClass);
            assertNotNull(relationMappingByPropertyNameByEntityName);

            assertEquals(relationMappingByPropertyNameByEntityClass, relationMappingByPropertyNameByEntityName);

            assertEquals(expectedRelationMappingByEntityClass, relationMappingByPropertyNameByEntityClass);
            assertEquals(expectedRelationMappingByEntityName, relationMappingByPropertyNameByEntityName);
        }
    }

    @Test
    public void fieldMappingTest() throws Exception {
        final IFieldMapping fieldMapping = entityMappingRegistry.getEntityType(PAYMENT_TRX_CLASS).getField(AMOUNT);
        assertNotNull(fieldMapping);
        assertFieldMapping(fieldMapping, AMOUNT, AMOUNT, Types.BIGINT, true, Long.class);
    }

    @Test
    public void fetchMappingTest() throws Exception {
        final IRelationMapping relationMapping = entityMappingRegistry.getEntityType(PAYMENT_TRX_CLASS).getRelation(AGENT);
        assertNotNull(relationMapping);
        assertFetchMapping(relationMapping, AGENT);
    }

    private void assertFieldMapping(IFieldMapping fieldMapping, String propertyName, String columnName, int type, boolean autoGenerated,
                                    Class fieldType) {
        assertNotNull(fieldMapping);
        assertEquals(propertyName, fieldMapping.getName());
        assertEquals(columnName, fieldMapping.getColumn());
        assertEquals(type, fieldMapping.getType());

        final Property entityFiled = fieldMapping.getProperty();

        assertNotNull(entityFiled);
        assertEquals(fieldType, entityFiled.getType());
    }

    private void assertFetchMapping(IRelationMapping relationMapping, String propertyName) {
        assertNotNull(relationMapping);
        assertEquals(propertyName, relationMapping.getName());

        final Property propertyField = relationMapping.getKeyProperty();
        final Property fetchField = relationMapping.getProperty();

        assertNotNull(propertyField);
        assertNotNull(fetchField);
    }
}
