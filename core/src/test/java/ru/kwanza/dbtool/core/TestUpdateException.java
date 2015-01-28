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

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

/**
 * @author Guzanov Alexander
 */
public class TestUpdateException extends TestCase {

    public void testUpdateException() {
        List<TestEntity> list = Arrays.asList(new TestEntity(1, "Value 1"), new TestEntity(2, "Value 2"), new TestEntity(3, "Value 3"));
        List<TestEntity> list2 = Arrays.asList(new TestEntity(4, "Value 4"), new TestEntity(5, "Value 5"));
        List<TestEntity> list3 = Arrays.asList(new TestEntity(6, "Value 6"));

        try {
            throw new UpdateException("Error!",null,null,null);
        } catch (UpdateException e) {
            assertEquals(e.getMessage(),"Error!");
            assertEquals(e.getConstrainted().size(),0);
            assertEquals(e.getOptimistic().size(),0);
            assertEquals(e.getUpdated().size(),0);
        }

        try {
            throw new UpdateException("Error!",list,list2,list3);
        } catch (UpdateException e) {
            assertEquals(e.getMessage(),"Error!");
            assertEquals(e.getConstrainted(),list);
            assertEquals(e.getOptimistic(),list2);
            assertEquals(e.getUpdated(),list3);
        }
    }
}
