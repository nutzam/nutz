package com.zzh.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zzh.lang.Files;
import com.zzh.lang.Lang;

import junit.framework.TestCase;

public class JsonCommentTest extends TestCase {

	public void testMapSimple() throws FileNotFoundException {
		String cs = "{A:12//C\n}";
		Map<?, ?> map = Json.fromJson(HashMap.class, Lang.inr(cs));
		assertEquals(12, map.get("A"));
	}

	public void testMapSimple2() throws FileNotFoundException {
		String cs = "{A:12//C\n,/*TT*/B:'X'}";
		Map<?, ?> map = Json.fromJson(HashMap.class, Lang.inr(cs));
		assertEquals(12, map.get("A"));
		assertEquals("X", map.get("B"));
	}

	public void testMapSimple3() throws FileNotFoundException {
		String cs = "{\n/*Y*/\nA:4//C\n//B\n,B:'X'}";
		Map<?, ?> map = Json.fromJson(HashMap.class, Lang.inr(cs));
		assertEquals(4, map.get("A"));
		assertEquals("X", map.get("B"));
	}

	public void testMapSimple4() throws FileNotFoundException {
		String cs = "{A:/**Y**/4,B://F\n34}";
		Map<?, ?> map = Json.fromJson(HashMap.class, Lang.inr(cs));
		assertEquals(4, map.get("A"));
		assertEquals(34, map.get("B"));

	}

	public void testMapSimpleComment() throws FileNotFoundException {
		String cs = "/**Y**/true";
		Boolean b = Json.fromJson(Boolean.class, Lang.inr(cs));
		assertTrue(b);
	}

	public void testMap() throws FileNotFoundException {
		File f = Files.findFile("com/zzh/json/cmt-map.txt");
		Map<?, ?> map = Json.fromJson(HashMap.class, new InputStreamReader(new FileInputStream(f)));
		assertEquals(34, map.get("A"));
		assertEquals("XYZ", map.get("B"));
		List<?> arr = (List<?>) map.get("arr");
		assertEquals(3, arr.size());
		assertEquals(34, arr.get(0));
		assertEquals(true, arr.get(1));
		assertEquals("abc", arr.get(2));
	}

	public void testArray2() throws Exception {
		String s = "[1,// :!\n2]:";
		int[] is = Json.fromJson(int[].class, Lang.inr(s));
		assertEquals(1, is[0]);
		assertEquals(2, is[1]);
	}

	public void testArray3() throws Exception {
		String s = "[1// :!\n,2]:";
		int[] is = Json.fromJson(int[].class, Lang.inr(s));
		assertEquals(1, is[0]);
		assertEquals(2, is[1]);
	}

}
