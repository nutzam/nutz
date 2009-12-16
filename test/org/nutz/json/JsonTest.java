package org.nutz.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nutz.dao.test.meta.Base;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.stream.StringInputStream;
import org.nutz.lang.stream.StringOutputStream;

@SuppressWarnings("unchecked")
public class JsonTest {

	@Test
	public void field_name_with_colon() {
		Map<?, ?> map = (Map<?, ?>) Json.fromJson("{'i\"d:':6};");
		assertEquals(6, map.get("i\"d:"));
	}

	@Test
	public void with_var_ioc_as_prefix() {
		Map<?, ?> map = (Map<?, ?>) Json.fromJson("var ioc = {id:6};");
		assertEquals(6, map.get("id"));
	}

	@Test
	public void when_name_has_unsupport_char() {
		Map map = new HashMap();
		map.put("/tt", 123);
		assertEquals("{\"/tt\":123}", Json.toJson(map, JsonFormat.compact().setQuoteName(false)));
	}

	@Test
	public void when_name_has_number_char_at_first() {
		Map map = new HashMap();
		map.put("3T", 123);
		assertEquals("{\"3T\":123}", Json.toJson(map, JsonFormat.compact().setQuoteName(false)));
	}

	@Test
	public void testSimpleObject() {
		assertEquals("6.5", Json.toJson(6.5));
		assertEquals("\"json\"", Json.toJson("json"));
		int[] ints = new int[0];
		assertEquals("[]", Json.toJson(ints));
		ints = new int[1];
		ints[0] = 65;
		assertEquals("[65]", Json.toJson(ints));
		assertEquals(65, Json.fromJson(Lang.inr("65")));
		assertEquals(Float.valueOf("65"), Json.fromJson(float.class, Lang.inr("65")));
		assertEquals(ints[0], Json.fromJson(int[].class, Lang.inr("[65]"))[0]);
	}

	@Test
	public void testBoolean() {
		assertTrue(Json.fromJson(boolean.class, Lang.inr("true")));
		assertFalse(Json.fromJson(boolean.class, Lang.inr("false")));
		assertTrue(((Boolean) Json.fromJson(Lang.inr("true"))).booleanValue());
		assertFalse(((Boolean) Json.fromJson(Lang.inr("false"))).booleanValue());
	}

	@Test
	public void testFloat() {
		assertEquals(Float.valueOf(2.3f), Json.fromJson(float.class, Lang.inr("2.3")));
		assertEquals((Float) 2.3f, Json.fromJson(Float.class, Lang.inr("2.3")));
		assertEquals(Float.valueOf(.3f), Json.fromJson(float.class, Lang.inr(".3")));
	}

	@Test
	public void testLongg() {
		assertEquals(87L, Json.fromJson(long.class, Lang.inr("87")).longValue());
		assertEquals(87L, ((Long) Json.fromJson(Lang.inr("87L"))).longValue());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testDatetime() {
		java.util.Date date = Json.fromJson(java.util.Date.class, Lang
				.inr("\"2008-05-16 14:35:43\""));
		assertEquals(108, date.getYear());
		assertEquals(4, date.getMonth());
		assertEquals(16, date.getDate());
		assertEquals(14, date.getHours());
		assertEquals(35, date.getMinutes());
		assertEquals(43, date.getSeconds());
	}

	@Test
	public void testSimpleAbc() {
		String s = "{\"id\":45,\"name\":'xyz'}";
		Abc abc = Json.fromJson(Abc.class, Lang.inr(s));
		assertEquals(45, abc.id);
		assertEquals("xyz", abc.name);
	}

	@Test
	public void testAllTypesInMap() throws FileNotFoundException {
		Map<String, Object> map = (Map<String, Object>) Json.fromJson(new InputStreamReader(
				getClass().getResourceAsStream("/org/nutz/json/types.txt")));
		assertTrue((Boolean) map.get("true"));
		assertFalse((Boolean) map.get("false"));
		assertNull(map.get("null"));
		assertTrue(34 == (Integer) map.get("int"));
		assertTrue(67L == (Long) map.get("long"));
		assertTrue(7.69 == (Double) map.get("double"));
		assertTrue(8.79f == (Float) map.get("float"));
		List<?> ary = (List<?>) map.get("array");
		assertEquals(2, ary.size());
		assertEquals("abc", ary.get(0));
		List<?> coll = ary;
		assertTrue(45 == (Integer) coll.get(1));
	}

	@Test
	public void testSimpleString() {
		String s = (String) Json.fromJson(Lang.inr(""));
		assertEquals(null, s);

		s = (String) Json.fromJson(Lang.inr("\"\""));
		assertEquals("", s);
	}

	@Test
	public void testSimpleMap() {
		String s = "{id:45,m:{x:1},name:'xyz'}";
		Map map = (Map) Json.fromJson(Lang.inr(s));
		assertEquals(45, map.get("id"));
		assertEquals("xyz", map.get("name"));
	}

	@Test
	public void testSimpleMap2() {
		String s = "{f:false,t:true,H:30}";
		Map map = (Map) Json.fromJson(Lang.inr(s));
		assertTrue((Boolean) map.get("t"));
		assertFalse((Boolean) map.get("f"));
		assertEquals(30, map.get("H"));
	}

	@Test
	public void testSimpleMap3() {
		String s = "{ary:[1,2],t:true,H:30}";
		Map map = (Map) Json.fromJson(Lang.inr(s));
		List<?> list = (List<?>) map.get("ary");
		assertEquals(2, list.size());
		assertTrue((Boolean) map.get("t"));
		assertEquals(30, map.get("H"));
	}

	@Test
	public void testSimpleMap4() {
		String s = "{id:45,name:'',txt:\"\"}";
		Map map = (Map) Json.fromJson(Lang.inr(s));
		assertEquals(45, map.get("id"));
		assertEquals("", map.get("name"));
		assertEquals("", map.get("txt"));
	}

	@Test
	public void testMap() throws FileNotFoundException {
		Map<String, Object> map = Json.fromJson(HashMap.class, 
				getFileAsInputStreamReader("org/nutz/json/map.txt"));
		assertEquals("value1", map.get("a1"));
		assertEquals(35, map.get("a2"));
		assertEquals((double) 4.7, map.get("a3"));
		Map m1 = (Map) map.get("m1");
		assertEquals(12, m1.get("x"));
		assertEquals(45, m1.get("y"));
		Map m12 = (Map) m1.get("m12");
		assertEquals("haha", m12.get("w1"));
		assertEquals("fuck", m12.get("w2"));
		Map m2 = (Map) map.get("m2");
		assertEquals("good", m2.get("today"));
		assertEquals("nice", m2.get("tomy"));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSimplePersonObject() throws Exception {
		Person p = Json.fromJson(Person.class, 
				getFileAsInputStreamReader("org/nutz/json/simplePerson.txt"));
		assertEquals("youoo", p.getName());
		assertEquals("YouChunSheng", p.getRealname());
		assertEquals(69, p.getAge());
		assertEquals(40, p.getBirthday().getYear());
		assertEquals(7, p.getBirthday().getMonth());
		assertEquals(15, p.getBirthday().getDate());
	}

	@Test
	public void testPersonObject() throws Exception {
		Person p = Json.fromJson(Person.class, 
				getFileAsInputStreamReader("org/nutz/json/person.txt"));
		StringBuilder sb = new StringBuilder();
		Writer w = new OutputStreamWriter(new StringOutputStream(sb));
		w.write(p.dump());
		w.write("\n");
		w.write(p.getFather().dump());
		w.write("\n");
		w.write(p.getCompany().getName());
		w.write("\n");
		w.write(p.getCompany().getCreator().dump());
		w.close();

		assertTrue(Streams.equals(new StringInputStream(sb), 
				getClass().getResourceAsStream("/org/nutz/json/person.expect.txt")));
	}

	@Test
	public void testSimpleArray() throws Exception {
		String[] expAry = {"abc", "bbc", "fff"};
		String s = String.format("[%s]", Lang.concatBy("\"%s\"", ',', expAry));
		String[] reAry = Json.fromJson(String[].class, Lang.inr(s));
		assertTrue(Arrays.equals(expAry, reAry));
	}

	@Test
	public void test_parse_simple_empty_array() throws Exception {
		Object[] objs = Json.fromJson(Object[].class, "[]");
		assertEquals(0, objs.length);
	}

	@Test
	public void testSimpleArraySingleInteger() throws Exception {
		String s = "[2]";
		int[] ary = Json.fromJson(int[].class, Lang.inr(s));
		assertEquals(1, ary.length);
		assertEquals(2, ary[0]);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSimpleArraySingleDate() throws Exception {
		String s = "[\"2008-8-1\"]";
		java.sql.Date[] ary = Json.fromJson(java.sql.Date[].class, Lang.inr(s));
		assertEquals(1, ary.length);
		assertEquals(108, ary[0].getYear());
		assertEquals(7, ary[0].getMonth());
		assertEquals(1, ary[0].getDate());
	}

	@Test
	public void testSimpleArraySingleObject() throws Exception {
		String s = "[{\"id\":24,\"name\":\"RRR\"}]";
		Abc[] ary = Json.fromJson(Abc[].class, Lang.inr(s));
		assertEquals(1, ary.length);
		assertEquals(24, ary[0].id);
		assertEquals("RRR", ary[0].name);
	}

	@Test
	public void testSimpleObjectArray() throws Exception {
		String s = "[{\"id\":3,\"name\":\"A\"},{\"id\":10,\"name\":\"B\"}]";
		Abc[] ary = Json.fromJson(Abc[].class, Lang.inr(s));
		assertEquals(2, ary.length);
		assertEquals(3, ary[0].id);
		assertEquals(10, ary[1].id);
		assertEquals("A", ary[0].name);
		assertEquals("B", ary[1].name);
	}

	@Test
	public void testNiceModeSimple() throws Exception {
		String s = "{id:45,name:\"x{y:12,t:'yzy'}z\"}";
		Abc abc = Json.fromJson(Abc.class, Lang.inr(s));
		assertEquals(45, abc.id);
		assertEquals("x{y:12,t:'yzy'}z", abc.name);

		s = "{id:45,name:'\"X\"'}";
		abc = Json.fromJson(Abc.class, Lang.inr(s));
		assertEquals(45, abc.id);
		assertEquals("\"X\"", abc.name);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testParseNullFieldObject() throws Exception {
		Person p = Json.fromJson(Person.class, 
						getFileAsInputStreamReader("org/nutz/json/personNull.txt"));
		assertEquals("youoo", p.getName());
		assertEquals("YouChunSheng", p.getRealname());
		assertEquals(69, p.getAge());
		assertEquals(40, p.getBirthday().getYear());
		assertEquals(7, p.getBirthday().getMonth());
		assertEquals(15, p.getBirthday().getDate());
	}

	@Test
	public void testPrintJsonObject() throws Exception {
		Person p = Json.fromJson(Person.class, 
				getFileAsInputStreamReader("org/nutz/json/person.txt"));
		String json = Json.toJson(p, JsonFormat.nice());
		Person p2 = Json.fromJson(Person.class, Lang.inr(json));
		assertEquals(p.getName(), p2.getName());
		assertEquals(p.getRealname(), p2.getRealname());
		assertEquals(p.getAge(), p2.getAge());
		assertEquals(p.getBirthday(), p2.getBirthday());
		assertEquals(p.getFather().getName(), p2.getFather().getName());
		assertEquals(p.getFather().getRealname(), p2.getFather().getRealname());
		assertEquals(p.getFather().getAge(), p2.getFather().getAge());
		assertEquals(p.getFather().getBirthday(), p2.getFather().getBirthday());
		assertEquals(p.getCompany().getName(), p2.getCompany().getName());
		assertEquals(p.getCompany().getCreator().getName(), p2.getCompany().getCreator().getName());
		assertEquals(p.getCompany().getCreator().getRealname(), p2.getCompany().getCreator()
				.getRealname());
		assertEquals(p.getCompany().getCreator().getAge(), p2.getCompany().getCreator().getAge());
		assertEquals(p.getCompany().getCreator().getFather(), p2.getCompany().getCreator()
				.getFather());
		assertEquals(p.getCompany().getCreator().getBirthday(), p2.getCompany().getCreator()
				.getBirthday());
	}

	@Test
	public void testFilterField() throws Exception {
		Person p = Json.fromJson(Person.class, getFileAsInputStreamReader("org/nutz/json/person.txt"));
		String json = Json.toJson(p, JsonFormat.nice().setActived("^name$"));
		Person p2 = Json.fromJson(Person.class, Lang.inr(json));
		assertEquals(p.getName(), p2.getName());
		assertNull(p2.getRealname());
		assertNull(p2.getBirthday());
		assertNull(p2.getFather());
		assertNull(p2.getCompany());
		assertEquals(0, p2.getAge());
	}

	@Test
	public void testFilterField2() throws Exception {
		Person p = Json.fromJson(Person.class, getFileAsInputStreamReader("org/nutz/json/person.txt"));
		String json = Json.toJson(p, JsonFormat.nice().setLocked("realname|father|company"));
		Person p2 = Json.fromJson(Person.class, Lang.inr(json));
		assertNull(p2.getRealname());
		assertEquals(p.getName(), p2.getName());
		assertEquals(p.getAge(), p2.getAge());
		assertEquals(p.getBirthday(), p2.getBirthday());
	}

	public static class Project {
		public int id;
		public String name;
		public String alias;

		public boolean equals(Object obj) {
			Project p = (Project) obj;
			return id == p.id & name.equals(p.name) & alias.equals(p.alias);
		}

	}

	@Test
	public void testOutpuProjectsAsList() throws Exception {
		Project p = new Project();
		p.id = 1;
		p.name = "nutz";
		p.alias = "Nutz Framework";
		Project p2 = Json.fromJson(Project.class, Json.toJson(p));
		assertTrue(p.equals(p2));
	}

	@Test
	public void testUndefined() throws Exception {
		String exp = "{id:45,name:'GG',alias:undefined}";
		Project p = Json.fromJson(Project.class, Lang.inr(exp));
		assertEquals(45, p.id);
		assertEquals("GG", p.name);
		assertNull(p.alias);
	}

	public static class X {
		public int id;
		public XT type;
	}

	public static enum XT {
		A, B
	}

	@Test
	public void testEnumOutput() throws Exception {
		X x = new X();
		x.id = 5;
		x.type = XT.B;
		X x2 = Json.fromJson(X.class, Json.toJson(x));
		assertEquals(x.id, x2.id);
		assertEquals(x.type, x2.type);
	}

	@Test
	public void testEmptyMap() throws Exception {
		Map<?, ?> map = (Map<?, ?>) Json.fromJson(Lang.inr("{}"));
		assertEquals(0, map.size());
		map = (Map<?, ?>) Json.fromJson(Lang.inr("  {/*rrrrrrrr*/   }"));
		assertEquals(0, map.size());
	}

	@Test
	public void testEmptyObject() throws Exception {
		X x = Json.fromJson(X.class, Lang.inr("{}"));
		assertEquals(0, x.id);
		assertNull(x.type);
	}

	@Test
	public void test_output_not_quote_name() {
		Base b = Base.make("Red");
		String json = Json.toJson(b, JsonFormat.compact().setQuoteName(false));
		Base b2 = Json.fromJson(Base.class, json);
		assertEquals(b.getCountryId(), b2.getCountryId());
		assertEquals(b.getLevel(), b2.getLevel());
		assertEquals(b.getName(), b2.getName());
	}

	static class A {
		List<String> list1;
		List<String> list2;
	}

	@Test
	public void testDuplicateArrayList() {
		A a = new A();
		a.list1 = new ArrayList<String>();
		a.list1.add("aaa");
		a.list2 = new ArrayList<String>();
		a.list2.add("aaa");
		String json = Json.toJson(a, JsonFormat.compact().setQuoteName(false));
		String exp = "{list2:[\"aaa\"],list1:[\"aaa\"]}";
		assertEquals(exp, json);
	}

	@Test
	public void test_special_char() {
		String s = "\\|\n|\r|\t";
		String exp = "\"\\\\|\\n|\\r|\\t\"";
		assertEquals(exp, Json.toJson(s));
		assertEquals(s, Json.fromJson(exp));
	}

	@Test
	public void test_number_output() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("a", "123");
		String re = Json.toJson(map, JsonFormat.compact().setQuoteName(false));
		assertEquals("{a:\"123\"}", re);
	}

	@Test
	public void test_dollar_as_name() {
		Map<String, Object> map = (Map<String, Object>) Json.fromJson("{$a:-23,b:-2.7}");
		Integer i = (Integer) map.get("$a");
		assertEquals(-23, i.intValue());
		Double d = (Double) map.get("b");
		assertEquals(-2.7, d.floatValue(), 3);
	}
	
	private InputStreamReader getFileAsInputStreamReader(String fileName){
		if(! fileName.startsWith("/"))
			fileName = "/" + fileName;
		return new InputStreamReader(getClass().getResourceAsStream(fileName));
	}
}
