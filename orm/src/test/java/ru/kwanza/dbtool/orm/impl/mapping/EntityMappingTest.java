package ru.kwanza.dbtool.orm.impl.mapping;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import ru.kwanza.dbtool.orm.impl.mapping.entities.*;

import javax.annotation.Resource;
import java.sql.Types;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

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
    }

    @Test
    public void validateEntityMappingTest() throws Exception {
        entityMappingRegistry.registerEntityClass(Agent.class);
        entityMappingRegistry.validateEntityMapping();
    }

    @Test
    @ExpectedException(RuntimeException.class)
    public void validateIncorrectEntityMappingTest() throws Exception {
        entityMappingRegistry.validateEntityMapping();
    }

    @Test
    public void registerEntityTest() throws Exception {
        assertEquals(PaymentTrx.class, entityMappingRegistry.getEntityClass(PAYMENT_TRX_NAME));
        assertEquals(PAYMENT_TRX_NAME, entityMappingRegistry.getEntityName(PAYMENT_TRX_CLASS));
        assertEquals(PAYMENT_TRX_TABLE_NAME, entityMappingRegistry.getTableName(PAYMENT_TRX_CLASS));
        assertEquals(PAYMENT_TRX_TABLE_NAME, entityMappingRegistry.getTableName(PAYMENT_TRX_NAME));
    }

    @Test
    @ExpectedException(RuntimeException.class)
    public void registerDuplicateEntityTest() throws Exception {
        registerPaymentTrx();
    }

    @Test
    @ExpectedException(RuntimeException.class)
    public void registerNotEntityTest() throws Exception {
        entityMappingRegistry.registerEntityClass(NotEntity.class);
    }

    @Test
    @ExpectedException(RuntimeException.class)
    public void registerEntityWithDuplicateColumnTest() throws Exception {
        entityMappingRegistry.registerEntityClass(EntityWithDuplicateColumn.class);
    }

    @Test
    @ExpectedException(RuntimeException.class)
    public void registerEntityWithDuplicateFieldPropertyTest() throws Exception {
        entityMappingRegistry.registerEntityClass(EntityWithDuplicateFieldProperty.class);
    }

    @Test
    @ExpectedException(RuntimeException.class)
    public void registerEntityWithDuplicateFetchPropertyTest() throws Exception {
        entityMappingRegistry.registerEntityClass(EntityWithDuplicateFetchProperty.class);
    }

    @Test
    public void getColumnNamesTest() throws Exception {
        final Collection<String> columnNamesByEntityClass = entityMappingRegistry.getColumnNames(PAYMENT_TRX_CLASS);
        final Collection<String> columnNamesByEntityName = entityMappingRegistry.getColumnNames(PAYMENT_TRX_NAME);

        assertNotNull(columnNamesByEntityClass);
        assertNotNull(columnNamesByEntityName);

        assertEquals(PAYMENT_TRX_COLUMNS, columnNamesByEntityClass);
        assertEquals(PAYMENT_TRX_COLUMNS, columnNamesByEntityName);
    }

    @Test
    public void getFieldMappingTest() throws Exception {
        final Collection<FieldMapping> fieldMappingByEntityClass = entityMappingRegistry.getFieldMappings(PAYMENT_TRX_CLASS);
        final Collection<FieldMapping> fieldMappingByEntityName = entityMappingRegistry.getFieldMappings(PAYMENT_TRX_NAME);

        assertNotNull(fieldMappingByEntityClass);
        assertNotNull(fieldMappingByEntityName);

        assertEquals(fieldMappingByEntityClass, fieldMappingByEntityName);

        assertEquals(PAYMENT_TRX_COLUMN_COUNT, fieldMappingByEntityClass.size());
        assertEquals(PAYMENT_TRX_COLUMN_COUNT, fieldMappingByEntityName.size());
    }

    @Test
    public void getIdFieldsTest() throws Exception {
        final Collection<FieldMapping> idFieldsByEntityClass = entityMappingRegistry.getIdFields(PAYMENT_TRX_CLASS);
        Collection<FieldMapping> idFieldsByEntityName = entityMappingRegistry.getIdFields(PAYMENT_TRX_NAME);

        assertNotNull(idFieldsByEntityClass);
        assertNotNull(idFieldsByEntityName);

        assertEquals(idFieldsByEntityClass, idFieldsByEntityName);

        assertEquals(PAYMENT_TRX_ID_FIELD_COUNT, idFieldsByEntityClass.size());
        assertEquals(PAYMENT_TRX_ID_FIELD_COUNT, idFieldsByEntityName.size());
    }

    @Test
    public void getVersionFieldTest() throws Exception {
        final FieldMapping versionFieldByEntityClass = entityMappingRegistry.getVersionField(PAYMENT_TRX_CLASS);
        final FieldMapping versionFieldByEntityName = entityMappingRegistry.getVersionField(PAYMENT_TRX_NAME);

        assertNotNull(versionFieldByEntityClass);
        assertNotNull(versionFieldByEntityName);

        assertEquals(versionFieldByEntityClass, versionFieldByEntityName);
    }

    @Test
    public void getFetchMappingTest() throws Exception {
        final Collection<FetchMapping> fetchMappingByEntityClass = entityMappingRegistry.getFetchMapping(PAYMENT_TRX_CLASS);
        final Collection<FetchMapping> fetchMappingByEntityName = entityMappingRegistry.getFetchMapping(PAYMENT_TRX_NAME);

        assertNotNull(fetchMappingByEntityClass);
        assertNotNull(fetchMappingByEntityName);

        assertEquals(fetchMappingByEntityClass, fetchMappingByEntityName);

        assertEquals(PAYMENT_TRX_FETCH_MAPPING_COUNT, fetchMappingByEntityClass.size());
        assertEquals(PAYMENT_TRX_FETCH_MAPPING_COUNT, fetchMappingByEntityName.size());
    }

    @Test
    public void getFieldMappingByPropertyNameTest() throws Exception {
        final Collection<FieldMapping> fieldMappingByEntityClass = entityMappingRegistry.getFieldMappings(PAYMENT_TRX_CLASS);
        final Collection<FieldMapping> fieldMappingByEntityName = entityMappingRegistry.getFieldMappings(PAYMENT_TRX_NAME);

        assertNotNull(fieldMappingByEntityClass);
        assertNotNull(fieldMappingByEntityName);

        final Iterator<FieldMapping> iteratorByEntityClass = fieldMappingByEntityClass.iterator();
        final Iterator<FieldMapping> iteratorByEntityName = fieldMappingByEntityName.iterator();

        for (String propertyName : PAYMENT_TRX_FIELD_PROPERTY_NAMES) {

            final FieldMapping expectedFieldMappingByEntityClass = iteratorByEntityClass.next();
            final FieldMapping expectedFieldMappingByEntityName = iteratorByEntityName.next();

            assertEquals(propertyName, expectedFieldMappingByEntityClass.getName());
            assertEquals(propertyName, expectedFieldMappingByEntityName.getName());

            final FieldMapping fieldMappingByPropertyNameByEntityClass =
                    entityMappingRegistry.getFieldMappingByPropertyName(PAYMENT_TRX_CLASS, propertyName);
            final FieldMapping fieldMappingByPropertyNameByEntityName =
                    entityMappingRegistry.getFieldMappingByPropertyName(PAYMENT_TRX_NAME, propertyName);

            assertNotNull(fieldMappingByPropertyNameByEntityClass);
            assertNotNull(fieldMappingByPropertyNameByEntityName);

            assertEquals(fieldMappingByPropertyNameByEntityClass, fieldMappingByPropertyNameByEntityName);

            assertEquals(expectedFieldMappingByEntityClass, fieldMappingByPropertyNameByEntityClass);
            assertEquals(expectedFieldMappingByEntityName, fieldMappingByPropertyNameByEntityName);
        }
    }

    @Test
    public void getFetchMappingByPropertyNameTest() throws Exception {
        final Collection<FetchMapping> fetchMappingByEntityClass = entityMappingRegistry.getFetchMapping(PAYMENT_TRX_CLASS);
        final Collection<FetchMapping> fetchMappingByEntityName = entityMappingRegistry.getFetchMapping(PAYMENT_TRX_NAME);

        assertNotNull(fetchMappingByEntityClass);
        assertNotNull(fetchMappingByEntityName);

        final Iterator<FetchMapping> iteratorByEntityClass = fetchMappingByEntityClass.iterator();
        final Iterator<FetchMapping> iteratorByEntityName = fetchMappingByEntityName.iterator();

        for (String propertyName : PAYMENT_TRX_FETCH_PROPERTY_NAMES) {

            final FetchMapping expectedFetchMappingByEntityClass = iteratorByEntityClass.next();
            final FetchMapping expectedFetchMappingByEntityName = iteratorByEntityName.next();

            assertEquals(propertyName, expectedFetchMappingByEntityClass.getName());
            assertEquals(propertyName, expectedFetchMappingByEntityName.getName());

            final FetchMapping fetchMappingByPropertyNameByEntityClass =
                    entityMappingRegistry.getFetchMappingByPropertyName(PAYMENT_TRX_CLASS, propertyName);
            final FetchMapping fetchMappingByPropertyNameByEntityName =
                    entityMappingRegistry.getFetchMappingByPropertyName(PAYMENT_TRX_NAME, propertyName);

            assertNotNull(fetchMappingByPropertyNameByEntityClass);
            assertNotNull(fetchMappingByPropertyNameByEntityName);

            assertEquals(fetchMappingByPropertyNameByEntityClass, fetchMappingByPropertyNameByEntityName);

            assertEquals(expectedFetchMappingByEntityClass, fetchMappingByPropertyNameByEntityClass);
            assertEquals(expectedFetchMappingByEntityName, fetchMappingByPropertyNameByEntityName);
        }
    }

    @Test
    public void fieldMappingTest() throws Exception {
        final FieldMapping fieldMapping = entityMappingRegistry.getFieldMappingByPropertyName(PAYMENT_TRX_CLASS, AMOUNT);
        assertNotNull(fieldMapping);
        assertFieldMapping(fieldMapping, AMOUNT, AMOUNT, Types.BIGINT, true, FieldImpl.class, Long.class);
    }

    @Test
    public void fetchMappingTest() throws Exception {
        final FetchMapping fetchMapping = entityMappingRegistry.getFetchMappingByPropertyName(PAYMENT_TRX_CLASS, AGENT);
        assertNotNull(fetchMapping);
        assertFetchMapping(fetchMapping, AGENT, FieldImpl.class, MethodImpl.class);
    }

    private void assertFieldMapping(FieldMapping fieldMapping, String propertyName, String columnName, int type, boolean autoGenerated,
                                    Class fieldClass, Class fieldType) {
        assertNotNull(fieldMapping);
        assertEquals(propertyName, fieldMapping.getName());
        assertEquals(columnName, fieldMapping.getColumn());
        assertEquals(type, fieldMapping.getType());
        assertEquals(autoGenerated, fieldMapping.isAutoGenerated());

        final EntityField entityFiled = fieldMapping.getEntityFiled();

        assertNotNull(entityFiled);
        assertEquals(fieldType, entityFiled.getType());
        assertEquals(fieldClass, entityFiled.getClass());
    }

    private void assertFetchMapping(FetchMapping fetchMapping, String propertyName, Class propertyFieldClass, Class fetchFieldClass) {
        assertNotNull(fetchMapping);
        assertEquals(propertyName, fetchMapping.getName());

        final EntityField propertyField = fetchMapping.getPropertyField();
        final EntityField fetchField = fetchMapping.getFetchField();

        assertNotNull(propertyField);
        assertNotNull(fetchField);

        assertEquals(propertyFieldClass, propertyField.getClass());
        assertEquals(fetchFieldClass, fetchField.getClass());
    }
}
