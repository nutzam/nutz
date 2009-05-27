package com.zzh.ioc;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zzh.ioc.json.JsonLoader;
import com.zzh.lang.Mirror;
import com.zzh.lang.Strings;
import com.zzh.lang.meta.Email;
import com.zzh.ioc.impl.NutIoc;

public class JsonIocTest {

	private Ioc ioc;

	public static class B {

		public B() {}

		public B(String s) {
			String[] ss = Strings.splitIgnoreBlank(s, ":");
			id = Integer.valueOf(ss[0]);
			name = ss[1];
		}

		public Ioc ioc;
		public int id;
		public String name;
		public List<Object> objs;
	}

	public static class A {

		private Mirror<?> mirror;

		private Field field;

		public B[] bs;

		public A(String className) throws ClassNotFoundException {
			this.mirror = Mirror.me(Class.forName(className));
		}

		public void setField(String name) throws Exception {
			field = mirror.getField(name);
		}

		public Field getField() {
			return field;
		}

	}

	@Before
	public void setUp() throws Exception {
		ioc = new NutIoc(new JsonLoader("com/zzh/ioc/objects.js"));
	}

	@After
	public void tearDown() throws Exception {
		if (null != null)
			ioc.depose();
	}

	@Test
	public void try_to_fetch_A1() {
		A a = ioc.get(A.class, "a1");
		assertEquals("account", a.getField().getName());
		assertEquals(Email.class, a.mirror.getType());
	}

	@Test
	public void try_to_fetch_A2() {
		A a = ioc.get(A.class, "a2");
		assertEquals("host", a.getField().getName());
		assertEquals(Email.class, a.mirror.getType());
	}

	@Test
	public void try_to_fetch_A3() {
		A a = ioc.get(A.class, "a3");
		assertEquals(2, a.bs.length);
		assertEquals(11, a.bs[0].id);
		assertEquals("b1", a.bs[0].name);
		assertEquals(ioc,a.bs[0].ioc);
		assertEquals(22, a.bs[1].id);
		assertEquals("b2", a.bs[1].name);
		assertEquals(ioc,a.bs[1].ioc);
	}

	@Test
	public void try_to_fetch_A4() {
		A a = ioc.get(A.class, "a4");
		assertEquals(2, a.bs.length);
		assertEquals(9, a.bs[0].id);
		assertEquals("f1", a.bs[0].name);
		assertEquals(10, a.bs[1].id);
		assertEquals("uu", a.bs[1].name);
	}

	@Test
	public void try_to_fetch_B1() {
		B b = ioc.get(null, "b1");
		assertEquals(11, b.id);
		assertEquals("b1", b.name);
		assertTrue(ioc == b.ioc);
	}

	@Test
	public void try_to_fetch_A5() {
		A a = ioc.get(A.class, "a5");
		assertEquals(3, a.bs.length);
		assertEquals(11, a.bs[0].id);
		assertEquals("b1", a.bs[0].name);
		assertEquals(22, a.bs[1].id);
		assertEquals("b2", a.bs[1].name);
		assertNull(a.bs[2]);
	}

	@Test
	public void trt_to_fetch_B_misc() throws NoSuchFieldException {
		B b = ioc.get(B.class, "b-misc");
		assertEquals(4, b.objs.size());
		assertEquals("ss", b.objs.get(0).toString());
		assertEquals(23, b.objs.get(1));
		assertEquals(Email.class, b.objs.get(2).getClass());
		assertEquals("zozoh@263.net", b.objs.get(2).toString());
		A a = (A) b.objs.get(3);
		assertEquals(Mirror.me(Email.class).getField("account"), a.field);
	}

	public static class IocFile {
		File file;

		public IocFile(File f) {
			this.file = f;
		}

		public void setFile(File file) {
			this.file = file;
		}

	}

	public void testIocFileByArgs() throws Exception {
		IocFile iof = ioc.get(IocFile.class, "ioc-file1");
		assertTrue(iof.file.isDirectory());
	}

	public static class O {
		private String name;
	}

	public static String getOName() {
		return "xyz";
	}

	public void testCallJava() throws Exception {
		O o = ioc.get(O.class, "o1");
		assertEquals("xyz", o.name);
	}

	public static class C {

		private Email email;

		public static C getInstance() {
			return new C(null);
		}

		public C(Email email) {
			this.email = email;
		}
	}

	@Test
	public void inject_by_Arg_as_object() {
		C c = ioc.get(C.class, "c1");
		assertEquals("abc", c.email.getAccount());
		assertEquals("263.net", c.email.getHost());
	}

	@Test
	public void inject_by_Fields_as_object() {
		C c = ioc.get(C.class, "c2");
		assertEquals("abc", c.email.getAccount());
		assertEquals("263.net", c.email.getHost());
	}

	public static class D {
		private Map<String, C> map;
	}

	@Test
	public void test_map_attribute() {
		D d = ioc.get(D.class, "d1");
		assertEquals(2, d.map.size());
		assertEquals("abc@263.net", d.map.get("cc1").email.toString());
		assertEquals("abc@263.net", d.map.get("cc2").email.toString());
	}

	@Test
	public void set_value_pojo_dont_has_the_field() {
		Fruit durian = ioc.get(Fruit.class, "durian");
		assertEquals("[Durian]", durian.getName());
		assertEquals(68, durian.getPrice());
	}

}
