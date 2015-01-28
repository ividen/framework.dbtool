package ru.kwanza.dbtool.orm.api;

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

import junit.framework.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author Alexander Guzanov
 */
public class IfTest {

    @Test
    public void testIsEquals() {
        If c = If.isEqual("name");
        Assert.assertEquals("name", c.getPropertyName());
        Assert.assertEquals(If.Type.IS_EQUAL, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertNull(c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());

        c = If.isEqual("name1", "n");
        Assert.assertEquals("name1", c.getPropertyName());
        Assert.assertEquals(If.Type.IS_EQUAL, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertEquals("n", c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());

        c = If.isEqual("name2", If.valueOf("TestName"));
        Assert.assertEquals("name2", c.getPropertyName());
        Assert.assertEquals(If.Type.IS_EQUAL, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertNull(c.getParamName());
        Assert.assertEquals("TestName", c.getValue());
        Assert.assertNull(c.getSql());
    }

    @Test
    public void testNotEquals() {
        If c = If.notEqual("name");
        Assert.assertEquals("name", c.getPropertyName());
        Assert.assertEquals(If.Type.NOT_EQUAL, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertNull(c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());

        c = If.notEqual("name1", "n");
        Assert.assertEquals("name1", c.getPropertyName());
        Assert.assertEquals(If.Type.NOT_EQUAL, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertEquals("n", c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());

        c = If.notEqual("name2", If.valueOf("TestName"));
        Assert.assertEquals("name2", c.getPropertyName());
        Assert.assertEquals(If.Type.NOT_EQUAL, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertNull(c.getParamName());
        Assert.assertEquals("TestName", c.getValue());
        Assert.assertNull(c.getSql());
    }

    @Test
    public void testIsGreater() {
        If c = If.isGreater("value");
        Assert.assertEquals("value", c.getPropertyName());
        Assert.assertEquals(If.Type.IS_GREATER, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertNull(c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());

        c = If.isGreater("value1", "n");
        Assert.assertEquals("value1", c.getPropertyName());
        Assert.assertEquals(If.Type.IS_GREATER, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertEquals("n", c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());

        c = If.isGreater("value2", If.valueOf(1l));
        Assert.assertEquals("value2", c.getPropertyName());
        Assert.assertEquals(If.Type.IS_GREATER, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertNull(c.getParamName());
        Assert.assertEquals(1l, c.getValue());
        Assert.assertNull(c.getSql());
    }

    @Test
    public void testIsLess() {
        If c = If.isLess("value");
        Assert.assertEquals("value", c.getPropertyName());
        Assert.assertEquals(If.Type.IS_LESS, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertNull(c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());

        c = If.isLess("value1", "n");
        Assert.assertEquals("value1", c.getPropertyName());
        Assert.assertEquals(If.Type.IS_LESS, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertEquals("n", c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());

        c = If.isLess("value2", If.valueOf(1l));
        Assert.assertEquals("value2", c.getPropertyName());
        Assert.assertEquals(If.Type.IS_LESS, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertNull(c.getParamName());
        Assert.assertEquals(1l, c.getValue());
        Assert.assertNull(c.getSql());
    }

    @Test
    public void testIsGreaterOrEquals() {
        If c = If.isGreaterOrEqual("value");
        Assert.assertEquals("value", c.getPropertyName());
        Assert.assertEquals(If.Type.IS_GREATER_OR_EQUAL, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertNull(c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());

        c = If.isGreaterOrEqual("value1", "n");
        Assert.assertEquals("value1", c.getPropertyName());
        Assert.assertEquals(If.Type.IS_GREATER_OR_EQUAL, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertEquals("n", c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());

        c = If.isGreaterOrEqual("value2", If.valueOf(1l));
        Assert.assertEquals("value2", c.getPropertyName());
        Assert.assertEquals(If.Type.IS_GREATER_OR_EQUAL, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertNull(c.getParamName());
        Assert.assertEquals(1l, c.getValue());
        Assert.assertNull(c.getSql());
    }

    @Test
    public void testIsLessOrEquals() {
        If c = If.isLessOrEqual("value");
        Assert.assertEquals("value", c.getPropertyName());
        Assert.assertEquals(If.Type.IS_LESS_OR_EQUAL, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertNull(c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());

        c = If.isLessOrEqual("value1", "n");
        Assert.assertEquals("value1", c.getPropertyName());
        Assert.assertEquals(If.Type.IS_LESS_OR_EQUAL, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertEquals("n", c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());

        c = If.isLessOrEqual("value2", If.valueOf(1l));
        Assert.assertEquals("value2", c.getPropertyName());
        Assert.assertEquals(If.Type.IS_LESS_OR_EQUAL, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertNull(c.getParamName());
        Assert.assertEquals(1l, c.getValue());
        Assert.assertNull(c.getSql());
    }

    @Test
    public void testIn() {
        If c = If.in("id");
        Assert.assertEquals("id", c.getPropertyName());
        Assert.assertEquals(If.Type.IN, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertNull(c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());

        c = If.in("id1", "n");
        Assert.assertEquals("id1", c.getPropertyName());
        Assert.assertEquals(If.Type.IN, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertEquals("n", c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());

        final List<Long> value = Arrays.asList(1l);
        c = If.in("id2", If.valueOf(value));
        Assert.assertEquals("id2", c.getPropertyName());
        Assert.assertEquals(If.Type.IN, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertNull(c.getParamName());
        Assert.assertEquals(value, c.getValue());
        Assert.assertNull(c.getSql());
    }

    @Test
    public void testLike() {
        If c = If.like("name");
        Assert.assertEquals("name", c.getPropertyName());
        Assert.assertEquals(If.Type.LIKE, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertNull(c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());

        c = If.like("name1", "n");
        Assert.assertEquals("name1", c.getPropertyName());
        Assert.assertEquals(If.Type.LIKE, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertEquals("n", c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());

        c = If.like("name2", If.valueOf("%TestName%"));
        Assert.assertEquals("name2", c.getPropertyName());
        Assert.assertEquals(If.Type.LIKE, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertNull(c.getParamName());
        Assert.assertEquals("%TestName%", c.getValue());
        Assert.assertNull(c.getSql());
    }

    @Test
    public void testBetween() {
        If c = If.between("value");
        Assert.assertEquals("value", c.getPropertyName());
        Assert.assertEquals(If.Type.BETWEEN, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertNull(c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());
    }

    @Test
    public void testIsNull() {
        If c = If.isNull("value");
        Assert.assertEquals("value", c.getPropertyName());
        Assert.assertEquals(If.Type.IS_NULL, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertNull(c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());
    }

    @Test
    public void testIsNotNull() {
        If c = If.isNotNull("value");
        Assert.assertEquals("value", c.getPropertyName());
        Assert.assertEquals(If.Type.IS_NOT_NULL, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertNull(c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());
    }

    @Test
    public void testAnd() {
        If c = If.and(If.isEqual("id"), If.isLess("value"));
        Assert.assertNull(c.getPropertyName());
        Assert.assertEquals(If.Type.AND, c.getType());
        Assert.assertNotNull(c.getChilds());
        Assert.assertEquals(2, c.getChilds().length);
        Assert.assertEquals("id", c.getChilds()[0].getPropertyName());
        Assert.assertEquals(If.Type.IS_EQUAL, c.getChilds()[0].getType());
        Assert.assertNull(c.getChilds()[0].getChilds());
        Assert.assertNull(c.getChilds()[0].getParamName());
        Assert.assertNull(c.getChilds()[0].getValue());
        Assert.assertNull(c.getChilds()[0].getSql());

        Assert.assertEquals("value", c.getChilds()[1].getPropertyName());
        Assert.assertEquals(If.Type.IS_LESS, c.getChilds()[1].getType());
        Assert.assertNull(c.getChilds()[1].getChilds());
        Assert.assertNull(c.getChilds()[1].getParamName());
        Assert.assertNull(c.getChilds()[1].getValue());
        Assert.assertNull(c.getChilds()[1].getSql());

        Assert.assertNull(c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());
    }

    @Test
    public void testOr() {
        If c = If.or(If.isEqual("id"), If.isLess("value"));
        Assert.assertNull(c.getPropertyName());
        Assert.assertEquals(If.Type.OR, c.getType());
        Assert.assertNotNull(c.getChilds());
        Assert.assertEquals(2, c.getChilds().length);
        Assert.assertEquals("id", c.getChilds()[0].getPropertyName());
        Assert.assertEquals(If.Type.IS_EQUAL, c.getChilds()[0].getType());
        Assert.assertNull(c.getChilds()[0].getChilds());
        Assert.assertNull(c.getChilds()[0].getParamName());
        Assert.assertNull(c.getChilds()[0].getValue());
        Assert.assertNull(c.getChilds()[0].getSql());

        Assert.assertEquals("value", c.getChilds()[1].getPropertyName());
        Assert.assertEquals(If.Type.IS_LESS, c.getChilds()[1].getType());
        Assert.assertNull(c.getChilds()[1].getChilds());
        Assert.assertNull(c.getChilds()[1].getParamName());
        Assert.assertNull(c.getChilds()[1].getValue());
        Assert.assertNull(c.getChilds()[1].getSql());

        Assert.assertNull(c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());
    }

    @Test
    public void testNot() {
        If c = If.not(If.isEqual("id"));
        Assert.assertNull(c.getPropertyName());
        Assert.assertEquals(If.Type.NOT, c.getType());
        Assert.assertNotNull(c.getChilds());
        Assert.assertEquals(1, c.getChilds().length);
        Assert.assertEquals("id", c.getChilds()[0].getPropertyName());
        Assert.assertEquals(If.Type.IS_EQUAL, c.getChilds()[0].getType());
        Assert.assertNull(c.getChilds()[0].getChilds());
        Assert.assertNull(c.getChilds()[0].getParamName());
        Assert.assertNull(c.getChilds()[0].getValue());
        Assert.assertNull(c.getChilds()[0].getSql());

        Assert.assertNull(c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertNull(c.getSql());
    }

    @Test
    public void testCreateNative() {
        If c = If.createNative("test_entity.id=10");
        Assert.assertNull(c.getPropertyName());
        Assert.assertEquals(If.Type.NATIVE, c.getType());
        Assert.assertNull(c.getChilds());
        Assert.assertNull(c.getParamName());
        Assert.assertNull(c.getValue());
        Assert.assertEquals("test_entity.id=10", c.getSql());
    }

}
