package ru.kwanza.dbtool.orm.impl.fetcher;

import junit.framework.TestCase;

import java.util.Map;

/**
 * @author Alexander Guzanov
 */

public class RelationPathScannerTest extends TestCase {

    public void test() {
        Map<String,Object> scan = new RelationPathScanner("  a           ,  d   , dd,dfddd                 ,   sd").scan();

        assertTrue(scan.containsKey("a"));
        assertTrue(scan.containsKey("d"));
        assertTrue(scan.containsKey("dfddd"));
        assertTrue(scan.containsKey("sd"));
        assertEquals(scan.size(),5);
    }

    public void test2() {
        Map<String, Object> scan = new RelationPathScanner("   a   {   b1,b2,b3 {b4  } }, d").scan();
        assertTrue(scan.containsKey("a"));
        assertTrue(scan.containsKey("d"));
        assertEquals(scan.size(),2);

        scan = (Map<String, Object>) scan.get("a");
        assertTrue(scan.containsKey("b1"));
        assertTrue(scan.containsKey("b2"));
        assertTrue(scan.containsKey("b3"));
        assertEquals(scan.size(),3);
        scan = (Map<String, Object>) scan.get("b3");
        assertTrue(scan.containsKey("b4"));
        assertEquals(scan.size(),1);
    }

    public void test3() {
        Map<String, Object> scan =new RelationPathScanner("   a   {   b1,b2  { c1  , c2 },b3 {b4,b5,b6  } }, d").scan();

        assertTrue(scan.containsKey("a"));
        assertTrue(scan.containsKey("d"));
        assertEquals(scan.size(),2);

        Map<String, Object>  scan1 = (Map<String, Object>) scan.get("a");
        assertTrue(scan1.containsKey("b1"));
        assertTrue(scan1.containsKey("b2"));
        assertTrue(scan1.containsKey("b3"));
        assertEquals(scan1.size(),3);
        Map<String, Object>  scan2 = (Map<String, Object>) scan1.get("b2");
        assertTrue(scan2.containsKey("c1"));
        assertTrue(scan2.containsKey("c2"));
        assertEquals(scan2.size(),2);

        Map<String, Object>  scan3 = (Map<String, Object>) scan1.get("b3");
        assertTrue(scan3.containsKey("b4"));
        assertTrue(scan3.containsKey("b5"));
        assertTrue(scan3.containsKey("b6"));
        assertEquals(scan3.size(),3);

    }

    public void test4() {
        Map<String, Object> scan = new RelationPathScanner("   sdfsdf    ").scan();
        assertTrue(scan.containsKey("sdfsdf"));
        assertEquals(scan.size(),1);
    }

    public void test5() {
        Map<String, Object> scan = new RelationPathScanner("asd").scan();
        assertTrue(scan.containsKey("asd"));
        assertEquals(scan.size(),1);
    }

    public void test6() {
        try {
            new RelationPathScanner("a{b,}").scan();
            fail("Expected " + RuntimeException.class);
        } catch (IllegalArgumentException e) {

        }
    }

    public void test7() {
        try {
            new RelationPathScanner("a{b,}").scan();
            fail("Expected " + RuntimeException.class);
        } catch (IllegalArgumentException e) {

        }
    }

    public void test8() {

        try {
            new RelationPathScanner("a{b}},c,d").scan();
            fail("Expected " + RuntimeException.class);
        } catch (IllegalArgumentException e) {
        }

    }

    public void test9() {
        try {
            new RelationPathScanner("a{b},d{},s").scan();
            fail("Expected " + RuntimeException.class);
        } catch (IllegalArgumentException e) {
        }
    }

    public void test10() {
        try {
            new RelationPathScanner("a{,}").scan();
            fail("Expected " + RuntimeException.class);
        } catch (IllegalArgumentException e) {
        }
    }

    public void test11() {
        try {
            new RelationPathScanner("a{,b,c}").scan();
            fail("Expected " + RuntimeException.class);
        } catch (IllegalArgumentException e) {
        }
    }

    public void test12() {
        try {
            new RelationPathScanner("a{b,c}{}").scan();
            fail("Expected " + RuntimeException.class);
        } catch (IllegalArgumentException e) {
        }
    }


}
