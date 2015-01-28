package ru.kwanza.dbtool.orm.impl.fetcher;

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
import ru.kwanza.dbtool.orm.impl.RelationPathScanner;

import java.util.Map;

import static junit.framework.Assert.*;

/**
 * @author Alexander Guzanov
 */

public class RelationPathScannerTest {

    @Test
    public void test() {
        Map<String, Object> scan = new RelationPathScanner("  a           ,  d   , dd,dfddd                 ,   sd").scan();

        assertTrue(scan.containsKey("a"));
        assertTrue(scan.containsKey("d"));
        assertTrue(scan.containsKey("dfddd"));
        assertTrue(scan.containsKey("sd"));
        assertEquals(scan.size(), 5);
    }

    @Test
    public void test_1() {
        Map<String, Object> scan = new RelationPathScanner("  \ta      \n\n     ,  d  \n , dd,dfddd                 ,   sd").scan();

        assertTrue(scan.containsKey("a"));
        assertTrue(scan.containsKey("d"));
        assertTrue(scan.containsKey("dfddd"));
        assertTrue(scan.containsKey("sd"));
        assertEquals(scan.size(), 5);
    }

    @Test
    public void test2() {
        Map<String, Object> scan = new RelationPathScanner("   a   {   b1,b2,b3 {b4  } }, d").scan();
        assertTrue(scan.containsKey("a"));
        assertTrue(scan.containsKey("d"));
        assertEquals(scan.size(), 2);

        scan = (Map<String, Object>) scan.get("a");
        assertTrue(scan.containsKey("b1"));
        assertTrue(scan.containsKey("b2"));
        assertTrue(scan.containsKey("b3"));
        assertEquals(scan.size(), 3);
        scan = (Map<String, Object>) scan.get("b3");
        assertTrue(scan.containsKey("b4"));
        assertEquals(scan.size(), 1);
    }

    public void test3() {
        Map<String, Object> scan = new RelationPathScanner("   a   {   b1,b2  { c1  , c2 },b3 {b4,b5,b6  } }, d").scan();

        assertTrue(scan.containsKey("a"));
        assertTrue(scan.containsKey("d"));
        assertEquals(scan.size(), 2);

        Map<String, Object> scan1 = (Map<String, Object>) scan.get("a");
        assertTrue(scan1.containsKey("b1"));
        assertTrue(scan1.containsKey("b2"));
        assertTrue(scan1.containsKey("b3"));
        assertEquals(scan1.size(), 3);
        Map<String, Object> scan2 = (Map<String, Object>) scan1.get("b2");
        assertTrue(scan2.containsKey("c1"));
        assertTrue(scan2.containsKey("c2"));
        assertEquals(scan2.size(), 2);

        Map<String, Object> scan3 = (Map<String, Object>) scan1.get("b3");
        assertTrue(scan3.containsKey("b4"));
        assertTrue(scan3.containsKey("b5"));
        assertTrue(scan3.containsKey("b6"));
        assertEquals(scan3.size(), 3);

    }

    @Test
    public void test4() {
        Map<String, Object> scan = new RelationPathScanner("   sdfsdf    ").scan();
        assertTrue(scan.containsKey("sdfsdf"));
        assertEquals(scan.size(), 1);
    }

    @Test
    public void test5() {
        Map<String, Object> scan = new RelationPathScanner("asd").scan();
        assertTrue(scan.containsKey("asd"));
        assertEquals(scan.size(), 1);
    }

    @Test
    public void test6() {
        try {
            new RelationPathScanner("a{b,}").scan();
            fail("Expected " + RuntimeException.class);
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void test7() {
        try {
            new RelationPathScanner("a{b,}").scan();
            fail("Expected " + RuntimeException.class);
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void test8() {

        try {
            new RelationPathScanner("a{b}},c,d").scan();
            fail("Expected " + RuntimeException.class);
        } catch (IllegalArgumentException e) {
        }

    }

    @Test
    public void test9() {
        try {
            new RelationPathScanner("a{b},d{},s").scan();
            fail("Expected " + RuntimeException.class);
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void test10() {
        try {
            new RelationPathScanner("a{,}").scan();
            fail("Expected " + RuntimeException.class);
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void test11() {
        try {
            new RelationPathScanner("a{,b,c}").scan();
            fail("Expected " + RuntimeException.class);
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void test12() {
        try {
            new RelationPathScanner("a{b,c}{}").scan();
            fail("Expected " + RuntimeException.class);
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void test13() {
        try {
            new RelationPathScanner("a,").scan();
            fail("Expected " + RuntimeException.class);
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void test14() {
        try {
            new RelationPathScanner("a{").scan();
            fail("Expected " + RuntimeException.class);
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void test15() {
        try {
            new RelationPathScanner("a{}").scan();
            fail("Expected " + RuntimeException.class);
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void test16() {
        try {
            new RelationPathScanner("a,{b}").scan();
            fail("Expected " + RuntimeException.class);
        } catch (IllegalArgumentException e) {
        }
    }


    @Test
    public void test17() {
        new RelationPathScanner("a{b}").scan();
    }

}
