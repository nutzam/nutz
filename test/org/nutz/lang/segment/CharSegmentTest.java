package org.nutz.lang.segment;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;

public class CharSegmentTest {
	@Test
	public void testNormal() {
		CharSegment seg = new CharSegment("H${4}B");
		seg.set("4", "zzh");
		assertEquals("HzzhB", seg.toString());
		assertEquals(1, seg.keys().size());
		assertEquals(3, seg.values().size());
	}

	@Test
	public void testWrongPPname() {
		CharSegment seg = new CharSegment("H${4}B");
		seg.set("RVT", "zzh");
		assertEquals("HB", seg.toString());
		seg.set("4", "zzh");
		assertEquals("HzzhB", seg.toString());
		assertEquals(1, seg.keys().size());
		assertEquals(3, seg.values().size());
	}

	@Test
	public void testLackRightBracket() {
		CharSegment seg = new CharSegment("H${4");
		seg.set("4", "TTTT");
		assertEquals("H${4", seg.toString());
		assertEquals(0, seg.keys().size());
		assertEquals(1, seg.values().size());
	}

	@Test
	public void testLackRightBracket2() {
		CharSegment seg = new CharSegment("H${R}${4");
		seg.set("R", "A");
		assertEquals("HA${4", seg.toString());
		assertEquals(1, seg.keys().size());
		assertEquals(3, seg.values().size());
	}

	@Test
	public void testLackLeftBracket() {
		CharSegment seg = new CharSegment("H$4}B");
		assertEquals("H$4}B", seg.toString());
		assertEquals(0, seg.keys().size());
		assertEquals(1, seg.values().size());
	}

	@Test
	public void testEscapeChar() {
		CharSegment seg = new CharSegment("H$$4}B");
		assertEquals("H$4}B", seg.toString());
		assertEquals(0, seg.keys().size());
		assertEquals(1, seg.values().size());
	}

	@Test
	public void testEscapeChar2() {
		CharSegment seg = new CharSegment("H$$$4}B");
		assertEquals("H$$4}B", seg.toString());
	}

	@Test
	public void testEscapeChar3() {
		CharSegment seg = new CharSegment("H$$$$4}B");
		assertEquals("H$$4}B", seg.toString());
	}

	@Test
	public void testAtTheEnd() {
		CharSegment seg = new CharSegment("H${4}");
		seg.set("4", "zzh");
		assertEquals("Hzzh", seg.toString());
		assertEquals(1, seg.keys().size());
		assertEquals(2, seg.values().size());
	}

	@Test
	public void testAtTheBegin() {
		CharSegment seg = new CharSegment("${4}B");
		seg.set("4", "zzh");
		assertEquals("zzhB", seg.toString());
		assertEquals(1, seg.keys().size());
		assertEquals(2, seg.values().size());
	}

	@Test
	public void testTwoPoints() {
		CharSegment seg = new CharSegment("A${1}B${2}C");
		seg.set("1", "$p1");
		seg.set("2", "$p2");
		assertEquals("A$p1B$p2C", seg.toString());
		assertEquals(2, seg.keys().size());
		assertEquals(5, seg.values().size());
	}

	@Test
	public void testTwoSamePoints() {
		CharSegment seg = new CharSegment("A${1}B${1}C");
		seg.set("1", "$p1");
		assertEquals("A$p1B$p1C", seg.toString());
		assertEquals(1, seg.keys().size());
		assertEquals(5, seg.values().size());
	}

	@Test
	public void testThreePoints() {
		CharSegment seg = new CharSegment("A${1}B${1}C${2}D");
		seg.set("1", "$p1");
		seg.set("2", "$p2");
		assertEquals("A$p1B$p1C$p2D", seg.toString());
		assertEquals(2, seg.keys().size());
		assertEquals(7, seg.values().size());
	}

	@Test
	public void testClearAll() {
		CharSegment seg = new CharSegment("A${1}B${1}C${2}D");
		seg.set("1", "T1");
		seg.set("2", "T2");
		seg.clearAll();
		assertEquals("ABCD", seg.toString());
	}

	@Test
	public void testCloseDynamicMark_E1() {
		CharSegment seg = new CharSegment("A${1}}}B");
		seg.set("1", "T1");
		assertEquals("AT1}}B", seg.toString());
	}

	@Test
	public void testBorn() {
		CharSegment seg = new CharSegment("A${a}B");
		seg.set("a", "A");
		assertEquals("AAB", seg.toString());
		CharSegment seg2 = (CharSegment) seg.born();
		assertEquals("AB", seg2.toString());
		assertEquals("AAB", seg.toString());
	}

	@Test
	public void testClone() {
		CharSegment seg = new CharSegment("A${a}B");
		seg.set("a", "A");
		assertEquals("AAB", seg.toString());
		CharSegment seg2 = (CharSegment) seg.clone();
		seg.set("a", "FF");
		assertEquals("AAB", seg2.toString());
		assertEquals("AFFB", seg.toString());
	}

	@Test
	public void testTrueFalse() {
		CharSegment seg = new CharSegment("true:[${true}]\tfalse:[${false}]");
		seg.set("true", true);
		seg.set("false", false);
		assertEquals("true:[true]	false:[false]", seg.toString());
	}

	@Test
	public void testKeys() {
		CharSegment seg = new CharSegment("-${A}-${B}-${A}-${B}-");
		assertEquals(2, seg.keys().size());
		seg.set("A", "[a]");
		seg.set("B", "[b]");
		assertEquals("-[a]-[b]-[a]-[b]-", seg.toString());
	}

	@Test
	public void testGetIndexs() {
		Segment seg = new CharSegment("${A}|${B}|${A}|${B}");
		List<Integer> indexes = seg.getIndex("A");
		assertEquals(0, indexes.get(0).intValue());
		assertEquals(2, indexes.get(1).intValue());
	}

	@Test
	public void testAddPP1() {
		CharSegment seg = new CharSegment("-${A}-");
		seg.add("A", "A");
		seg.add("A", "B");
		seg.add("A", "C");
		assertEquals("-ABC-", seg.toString());
		seg.set("A", "HH");
		assertEquals("-HH-", seg.toString());
	}

	@Test
	public void testAddPP2() {
		CharSegment seg = new CharSegment("-${A}-${B}-");
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
		CharSegment seg = new CharSegment("-${A}-${B}-");
		CharSegment sub = new CharSegment("[${V}]");
		CharSegment sub2 = new CharSegment("%${V}%");
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
		Segment seg = new CharSegment(s);
		assertTrue(s.equals(seg.toString()));
	}
}
