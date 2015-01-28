package ru.kwanza.dbtool.core;

/*
 * #%L
 * dbtool-core
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
import junit.framework.TestCase;
import ru.kwanza.dbtool.core.util.UpdateUtil;
import ru.kwanza.toolbox.fieldhelper.FieldHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static ru.kwanza.dbtool.core.util.UpdateUtil.getSortedList;

/**
 * @author Dmitry Zagorovsky
 */
public class UnitTestUpdateUtil extends TestCase {

    public void testObjectByKeyComparator() throws Exception {
        UpdateUtil.ObjectByKeyComparator<TestObject, Integer> comparator =
                new UpdateUtil.ObjectByKeyComparator<TestObject, Integer>(TestObject.KEY);
        Assert.assertEquals(0, comparator.compare(new TestObject(1), new TestObject(1)));
        Assert.assertEquals(-1, comparator.compare(new TestObject(1), new TestObject(2)));
        Assert.assertEquals(1, comparator.compare(new TestObject(2), new TestObject(1)));
    }

    public void testGetSortedListByList() throws Exception {
        TestObject testObject1 = new TestObject(1);
        TestObject testObject2 = new TestObject(2);
        TestObject testObject3 = new TestObject(3);
        TestObject testObject4 = new TestObject(4);

        List<TestObject> inList = asList(testObject2, testObject1, testObject4, testObject3);
        List<TestObject> outList = asList(testObject1, testObject2, testObject3, testObject4);

        Assert.assertEquals(outList, getSortedList(inList, TestObject.KEY));
    }

    public void testGetSortedListBySet() throws Exception {
        TestObject testObject1 = new TestObject(1);
        TestObject testObject2 = new TestObject(2);
        TestObject testObject3 = new TestObject(3);
        TestObject testObject4 = new TestObject(4);

        Set<TestObject> inSet = new HashSet<TestObject>(asList(testObject2, testObject1, testObject4, testObject3));
        List<TestObject> outList = asList(testObject1, testObject2, testObject3, testObject4);

        Assert.assertEquals(outList, getSortedList(inSet, TestObject.KEY));
    }

    private static class TestObject {

        private Integer id;

        private TestObject(Integer id) {
            this.id = id;
        }

        public static final FieldHelper.Field<TestObject, Integer> KEY = new FieldHelper.Field<TestObject, Integer>() {
            public Integer value(TestObject object) {
                return object == null ? null : object.id;
            }
        };

    }

}
