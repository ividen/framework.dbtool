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

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import ru.kwanza.toolbox.fieldhelper.Property;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 * @author Kiryl Karatsetski
 */
@ContextConfiguration(locations = "dbtool-orm-mapping-test-config.xml")
public abstract class EntityFieldTest {

    protected abstract Property getproperty() throws Exception;

    @Test
    public void testGetType() throws Exception {
        final Property property = getproperty();
        assertEquals(Long.class, property.getType());
    }

    @Test
    public void testGetter() throws Exception {
        final Property property = getproperty();
        final Entity entity = new Entity();
        entity.setLongValue(1L);

        assertEquals(1L, property.value(entity));
    }

    @Test
    public void testSetter() throws Exception {
        final Property property = getproperty();
        final Entity entity = new Entity();
        property.set(entity, 1L);

        assertEquals(1L, entity.getLongValue().longValue());
    }

    @Test(expected = RuntimeException.class)
    public void testFailGetter() throws Exception {
        final Property property = getproperty();
        final String wrongObject = "wrongObject";
        property.value(wrongObject);
    }

    @Test(expected = RuntimeException.class)
    public void testFailSetter() throws Exception {
        final Property property = getproperty();
        final String wrongObject = "wrongObject";
        final Entity entity = new Entity();
        property.set(entity, wrongObject);
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
