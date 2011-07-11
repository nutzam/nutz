package org.nutz.lang;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.json.Json;

public class LangTest {

	public static class A {
		private int id;
		private String name;

		public A() {}

		public A(int id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	public static class B {
		private List<A> as;
		private A[] aa;
		private Map<String, A> amap;
	}

	@Test
	public void test_equals_simple() {
		assertTrue(Lang.equals(null, null));
		assertTrue(Lang.equals("abc", "abc"));
		assertTrue(Lang.equals((short) 21, 21));
		assertTrue(Lang.equals(21.0, 21.0f));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test_array2map() {
		A a = new A(1, "a");
		A b = new A(2, "b");
		Map<Integer, A> m1 = Lang.array2map(HashMap.class, new A[]{a, b}, "id");
		assertEquals(a, m1.get(1));
		assertEquals(b, m1.get(2));
		Map<String, A> m2 = Lang.array2map(HashMap.class, new A[]{a, b}, "name");
		assertEquals(a, m2.get("a"));
		assertEquals(b, m2.get("b"));

		Map<Integer, A> m3 = Lang.array2map(HashMap.class, null, "id");
		assertNull(m3);
		Map<Integer, A> m4 = Lang.array2map(HashMap.class, new A[]{}, "id");
		assertTrue(m4.isEmpty());
	}

	@Test
	public void test_map2object() throws FailToCastObjectException {
		Map<String, Object> map = Lang.map("{id:23,name:'zzh'}");
		A a = Lang.map2Object(map, A.class);
		assertEquals(23, a.id);
		assertEquals("zzh", a.name);

		map = Lang.map("{aa:[{id:23,name:'zzh'},{id:5,name:'xyz'}]}");
		B b = Lang.map2Object(map, B.class);
		assertEquals(23, b.aa[0].id);
		assertEquals("xyz", b.aa[1].name);

		map = Lang.map("{as:[{id:23,name:'zzh'},{id:5,name:'xyz'}]}");
		b = Lang.map2Object(map, B.class);
		assertEquals(23, b.as.get(0).id);
		assertEquals("xyz", b.as.get(1).name);

		map = Lang.map("{amap:{z:{id:23,name:'zzh'},x:{id:5,name:'xyz'}}}");
		b = Lang.map2Object(map, B.class);
		assertEquals(23, b.amap.get("z").id);
		assertEquals("xyz", b.amap.get("x").name);

	}

	@Test
	public void test_map2map() {
		Map<?, ?> map = (Map<?, ?>) Json.fromJson(Lang.inr("{id:23,name:'zzh'}"));
		Map<?, ?> map2 = Lang.map2Object(map, Map.class);
		assertTrue(Lang.equals(map, map2));
	}

	@Test
	public void test_char_reader() throws IOException {
		Reader r = Lang.inr("AB");
		assertEquals('A', (char) r.read());
		assertEquals('B', (char) r.read());
		assertEquals(-1, r.read());
	}

	@Test
	public void test_char_writer() throws IOException {
		StringBuilder sb = new StringBuilder();
		Writer w = Lang.opw(sb);
		w.write("AB");
		assertEquals("AB", sb.toString());
	}

	@Test
	public void test_map_equles() {
		Map<?, ?> map1 = Lang.map("{a:1,b:2}");
		Map<?, ?> map2 = Lang.map("{b:2,a:1}");

		assertTrue(Lang.equals(map1, map2));

		map1 = Lang.map("{a:'1',b:2}");
		map2 = Lang.map("{b:2,a:1}");

		assertFalse(Lang.equals(map1, map2));
	}

	@Test
	public void test_string_array2array() {
		assertNull(Lang.array2array(null, int.class));
		assertTrue(((int[]) Lang.array2array(new String[]{}, int.class)).length == 0);
		int[] is = (int[]) Lang.array2array(Lang.array("10", "20"), int.class);
		assertEquals(10, is[0]);
		assertEquals(20, is[1]);
	}

	@Test
	public void test_array2_object_array() {
		String[] s1 = {"a", "2", "2.0", null};
		Class<?>[] c1 = {String.class, int.class, float.class, Object.class};
		Object[] objs = Lang.array2ObjectArray(s1, c1);
		assertEquals("a", objs[0]);
		assertEquals(2, objs[1]);
		assertTrue(objs[2] instanceof Float);
		assertEquals(2.0f, objs[2]);
		assertNull(objs[3]);

		assertNull(Lang.array2ObjectArray(null, c1));
		assertTrue(Lang.array2ObjectArray(new String[]{}, c1).length == 0);
	}

	@Test
	public void test_concat() {
		String[] ss = Lang.array("A", "B");
		assertEquals("A--B", Lang.concat("--", ss).toString());
	}

	@Test
	public void test_concat4_offset_len() {
		assertEquals("", Lang.concat(0, 2, "-", null).toString());
		assertEquals("", Lang.concat(0, 2, "-", new String[]{}).toString());
		assertEquals("a-b", Lang.concat(0, 2, "-", new String[]{"a", "b"}).toString());
		assertEquals("b", Lang.concat(1, 1, "-", new String[]{"a", "b"}).toString());
		assertEquals("c", Lang.concat(2, 2, "-", new String[]{"a", "b", "c"}).toString());
		assertEquals("", Lang.concat(2, 2, "-", new String[]{"a", "b"}).toString());
		assertEquals("", Lang.concat(1, -1, "-", new String[]{"a", "b"}).toString());
	}

	public static class BC {
		String name;
		CB cb;
	}

	public static class CB {
		String code;
		BC bc;
	}

	@Test
	public void test_obj2map() {
		BC bc = new BC();
		bc.name = "B";
		CB cb = new CB();
		cb.code = "C";
		bc.cb = cb;
		cb.bc = bc;
		Map<String, Object> map = Lang.obj2map(bc);
		assertEquals("B", map.get("name"));
		assertEquals("C", ((Map<?, ?>) map.get("cb")).get("code"));
		assertNull(((Map<?, ?>) map.get("cb")).get("bc"));
	}

	@Test
	public void test_readAll() {
		String src = "!!我要测试-->密码";
		String dest = Lang.readAll(new InputStreamReader(Lang.ins(src)));
		String dest2 = Lang.readAll(Lang.inr(src));
		assertEquals(src, dest);
		assertEquals(src, dest2);
	}

	@Ignore("测试平台不一定为Windows")
	@Test
	public void test_isWin() {
		assertTrue(Lang.isWin());
	}

	@Test
	public void test_merge() {
		String[] a1 = {};
		String[] a2 = {"a", "b"};
		String[] a3 = {"c"};
		assertArrayEquals(new String[]{"a", "b", "c"}, Lang.merge(a1, a2, a3));
		String[] b1 = new String[]{};
		String[] b2 = null;
		assertArrayEquals(null, Lang.merge(b1, b2));
	}

	@Test
	public void test_array_first() {
		assertArrayEquals(new String[]{"a"}, Lang.arrayFirst("a", null));
		assertArrayEquals(new String[]{"a"}, Lang.arrayFirst("a", new String[]{}));
		assertArrayEquals(new String[]{"a", "b"}, Lang.arrayFirst("a", new String[]{"b"}));
	}

	@Test
	public void test_array_last() {
		assertArrayEquals(new String[]{"a"}, Lang.arrayLast(null, "a"));
		assertArrayEquals(new String[]{"a"}, Lang.arrayLast(new String[]{}, "a"));
		assertArrayEquals(new String[]{"b", "a"}, Lang.arrayLast(new String[]{"b"}, "a"));
	}

	@Test
	public void test_parse_boolean() {
		assertFalse(Lang.parseBoolean(null));
		assertFalse(Lang.parseBoolean("false"));
		assertTrue(Lang.parseBoolean("on"));
		assertTrue(Lang.parseBoolean("1"));
		assertTrue(Lang.parseBoolean("yes"));
		assertTrue(Lang.parseBoolean("some str"));
	}

	@Test
	public void test_first4_map_collection() {
		assertNull(Lang.first(new HashMap<String, String>()));
		Map<?, ?> map1 = Lang.map("{a:1,b:2}");
		assertTrue(map1.entrySet().contains(Lang.first(map1)));

		assertNull(Lang.first(new ArrayList<String>()));
		List<Object> l = Lang.list4("[1,2,3,4]");
		assertEquals(1, Lang.first(l));
	}

	@Test
	public void test_length() {
		assertEquals(0, Lang.length(null));
		assertEquals(1, Lang.length(12));
		assertEquals(11, Lang.length("hello,world"));
		String[] arr = Lang.array("a", "b");
		assertEquals(2, Lang.length(Lang.map("1:'bb',5:'aa'")));
		assertEquals(2, Lang.length(arr));
		assertEquals(2, Lang.length(Arrays.asList(arr)));
	}

	@Test
	public void test_2bytes() {
		char[] cs = {'a', 'b', 'c', 150};
		assertArrayEquals(new byte[]{97, 98, 99, -106}, Lang.toBytes(cs));
		int[] is = {'a', 'b', 'c', 150};
		assertArrayEquals(new byte[]{97, 98, 99, -106}, Lang.toBytes(is));
	}
	
	@Test
	public void test_str2number() {
		Long re = (Long) Lang.str2number(""+Long.MAX_VALUE);
		assertEquals(Long.MAX_VALUE, re.longValue());
		assertEquals(Integer.MAX_VALUE, Lang.str2number(""+Integer.MAX_VALUE));
		assertEquals(0, Lang.str2number("0"));
		assertEquals(0.1, Lang.str2number("0.1"));
		assertEquals(0.1, Lang.str2number("0.1d"));
		assertEquals(0.1f, Lang.str2number("0.1f"));
		assertEquals(2147483648L, Lang.str2number("2147483648"));
		assertEquals(2147483648L, Lang.str2number("2147483648l"));
	}
}
