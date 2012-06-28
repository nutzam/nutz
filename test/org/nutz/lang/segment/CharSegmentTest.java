package org.nutz.lang.segment;

import static org.junit.Assert.*;

import org.junit.Test;

import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;

public class CharSegmentTest {
    @Test
    public void testNormal() {
        Segment seg = Segments.create("H${4}B");
        seg.set("4", "zzh");
        assertEquals("H${4}B", seg.getOrginalString());
        assertEquals("HzzhB", seg.toString());
        assertEquals(1, seg.keys().size());
        assertEquals(3, seg.values().size());
    }

    @Test
    public void testWrongPPname() {
        Segment seg = Segments.create("H${4}B");
        seg.set("RVT", "zzh");
        assertEquals("HB", seg.toString());
        seg.set("4", "zzh");
        assertEquals("HzzhB", seg.toString());
        assertEquals(1, seg.keys().size());
        assertEquals(3, seg.values().size());
    }

    @Test(expected = RuntimeException.class)
    public void testLackRightBracket() {
        Segments.create("H${4");
    }

    @Test
    public void testLackLeftBracket() {
        Segment seg = Segments.create("H$4}B");
        assertEquals("H$4}B", seg.toString());
        assertEquals(0, seg.keys().size());
        assertEquals(1, seg.values().size());
    }

    @Test
    public void testEscapeChar() {
        Segment seg = Segments.create("H$$4}B");
        assertEquals("H$4}B", seg.toString());
        assertEquals(0, seg.keys().size());
        assertEquals(1, seg.values().size());
    }

    @Test
    public void testEscapeChar2() {
        Segment seg = Segments.create("H$$$4}B");
        assertEquals("H$$4}B", seg.toString());
    }

    @Test
    public void testEscapeChar3() {
        Segment seg = Segments.create("H$$$$4}B");
        assertEquals("H$$4}B", seg.toString());
    }

    @Test
    public void testAtTheEnd() {
        Segment seg = Segments.create("H${4}");
        seg.set("4", "zzh");
        assertEquals("Hzzh", seg.toString());
        assertEquals(1, seg.keys().size());
        assertEquals(2, seg.values().size());
    }

    @Test
    public void testAtTheBegin() {
        Segment seg = Segments.create("${4}B");
        seg.set("4", "zzh");
        assertEquals("zzhB", seg.toString());
        assertEquals(1, seg.keys().size());
        assertEquals(2, seg.values().size());
    }

    @Test
    public void testTwoPoints() {
        Segment seg = Segments.create("A${1}B${2}C");
        seg.set("1", "$p1");
        seg.set("2", "$p2");
        assertEquals("A$p1B$p2C", seg.toString());
        assertEquals(2, seg.keys().size());
        assertEquals(5, seg.values().size());
    }

    @Test
    public void testTwoSamePoints() {
        Segment seg = Segments.create("A${1}B${1}C");
        seg.set("1", "$p1");
        assertEquals("A$p1B$p1C", seg.toString());
        assertEquals(1, seg.keys().size());
        assertEquals(5, seg.values().size());
    }

    @Test
    public void testThreePoints() {
        Segment seg = Segments.create("A${1}B${1}C${2}D");
        seg.set("1", "$p1");
        seg.set("2", "$p2");
        assertEquals("A$p1B$p1C$p2D", seg.toString());
        assertEquals(2, seg.keys().size());
        assertEquals(7, seg.values().size());
    }

    @Test
    public void testClearAll() {
        Segment seg = Segments.create("A${1}B${1}C${2}D");
        seg.set("1", "T1");
        seg.set("2", "T2");
        seg.clearAll();
        assertEquals("ABCD", seg.toString());
    }

    @Test
    public void testCloseDynamicMark_E1() {
        Segment seg = Segments.create("A${1}}}B");
        seg.set("1", "T1");
        assertEquals("AT1}}B", seg.toString());
    }

    @Test
    public void testBorn() {
        Segment seg = Segments.create("A${a}B");
        seg.set("a", "A");
        assertEquals("AAB", seg.toString());
        Segment seg2 = (CharSegment) seg.born();
        assertEquals("AB", seg2.toString());
        assertEquals("AAB", seg.toString());
    }

    @Test
    public void testClone() {
        Segment seg = Segments.create("A${a}B");
        seg.set("a", "A");
        assertEquals("AAB", seg.toString());
        Segment seg2 = (CharSegment) seg.clone();
        seg.set("a", "FF");
        assertEquals("AAB", seg2.toString());
        assertEquals("AFFB", seg.toString());
    }

    @Test
    public void testTrueFalse() {
        Segment seg = Segments.create("true:[${true}]\tfalse:[${false}]");
        seg.set("true", true);
        seg.set("false", false);
        assertEquals("true:[true]\tfalse:[false]", seg.toString());
    }

    @Test
    public void testKeys() {
        Segment seg = Segments.create("-${A}-${B}-${A}-${B}-");
        assertEquals(2, seg.keys().size());
        seg.set("A", "[a]");
        seg.set("B", "[b]");
        assertEquals("-[a]-[b]-[a]-[b]-", seg.toString());
    }

    @Test
    public void testAddPP1() {
        Segment seg = Segments.create("-${A}-");
        seg.add("A", "A");
        seg.add("A", "B");
        seg.add("A", "C");
        assertEquals("-ABC-", seg.toString());
        seg.set("A", "HH");
        assertEquals("-HH-", seg.toString());
    }

    @Test
    public void testAddPP2() {
        Segment seg = Segments.create("-${A}-${B}-");
        seg.add("A", "A");
        seg.add("A", "B");
        seg.add("A", "C");
        seg.add("B", "H");
        seg.add("B", "M");
        assertEquals("-ABC-HM-", seg.toString());
        seg.set("B", "ZZH");
        assertEquals("-ABC-ZZH-", seg.toString());
    }

    @Test
    public void testAddPP3() {
        Segment seg = Segments.create("-${A}-${B}-");
        Segment sub = Segments.create("[${V}]");
        Segment sub2 = Segments.create("%${V}%");
        seg.add("A", sub);
        seg.add("A", sub2);
        seg.set("B", "***");
        sub.set("V", "#");
        sub2.set("V", "@");
        assertEquals("-[#]%@%-***-", seg.toString());
        seg.set("B", "ZZH");
        assertEquals("-[#]%@%-ZZH-", seg.toString());
    }

    @Test
    public void testChineseChar() {
        String s = new StringBuilder().append((char) Integer.parseInt("6211", 16)).toString();
        Segment seg = Segments.create(s);
        assertTrue(s.equals(seg.toString()));
    }

    public void test_blankKeys() {
        Segment seg = Segments.create("1${A}2${B}3${C}4${D}5");
        // assertEquals(4, seg.blankKeys().size());

        seg.set("A", 34);
        seg.set("D", "GG");
        // assertEquals(2, seg.blankKeys().size());
    }
}
