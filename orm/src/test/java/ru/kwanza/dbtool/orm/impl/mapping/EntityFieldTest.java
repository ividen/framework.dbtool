package ru.kwanza.dbtool.orm.impl.mapping;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 * @author Kiryl Karatsetski
 */
public abstract class EntityFieldTest {

    protected abstract EntityField getEntityField() throws Exception;

    @Test
    public void testGetType() throws Exception {
        final EntityField entityField = getEntityField();
        assertEquals(Long.class, entityField.getType());
    }

    @Test
    public void testGetter() throws Exception {
        final EntityField entityField = getEntityField();
        final Entity entity = new Entity();
        entity.setLongValue(1L);

        assertEquals(1L, entityField.getValue(entity));
    }

    @Test
    public void testSetter() throws Exception {
        final EntityField entityField = getEntityField();
        final Entity entity = new Entity();
        entityField.setValue(entity, 1L);

        assertEquals(1L, entity.getLongValue().longValue());
    }

    @Test(expected = RuntimeException.class)
    public void testFailGetter() throws Exception {
        final EntityField entityField = getEntityField();
        final String wrongObject = "wrongObject";
        entityField.getValue(wrongObject);
    }

    @Test(expected = RuntimeException.class)
    public void testFailSetter() throws Exception {
        final EntityField entityField = getEntityField();
        final String wrongObject = "wrongObject";
        final Entity entity = new Entity();
        entityField.setValue(entity, wrongObject);
    }

    protected Field getField() throws NoSuchFieldException {
        return Entity.class.getDeclaredField("longValue");
    }

    protected Method getGetter() throws NoSuchMethodException {
        return Entity.class.getMethod("getLongValue");
    }

    protected Method getSetter() throws NoSuchMethodException {
        return Entity.class.getMethod("setLongValue", Long.class);
    }

    protected class Entity {

        private Long longValue;

        public Long getLongValue() {
            return longValue;
        }

        public void setLongValue(Long longValue) {
            this.longValue = longValue;
        }
    }
}
