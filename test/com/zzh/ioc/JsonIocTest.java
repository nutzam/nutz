package com.zzh.ioc;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import com.zzh.ioc.json.JsonMappingLoader;
import com.zzh.lang.Mirror;
import com.zzh.lang.Strings;
import com.zzh.lang.meta.Email;

import junit.framework.TestCase;

public class JsonIocTest extends TestCase {

	private Nut nut;

	public static class B {

		public B() {
			super();
		}

		public B(String s) {
			String[] ss = Strings.splitIgnoreBlank(s, ":");
			id = Integer.valueOf(ss[0]);
			name = ss[1];
		}

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

	@Override
	protected void setUp() throws Exception {
		nut = new Nut(new JsonMappingLoader("com/zzh/ioc/objects.txt"));
	}

	public void testFetchA1() {
		A a = nut.getObject(A.class, "a1");
		assertEquals("account", a.getField().getName());
		assertEquals(Email.class, a.mirror.getType());
	}

	public void testFetchA2() {
		A a = nut.getObject(A.class, "a2");
		assertEquals("host", a.getField().getName());
		assertEquals(Email.class, a.mirror.getType());
	}

	public void testFetchA3() {
		A a = nut.getObject(A.class, "a3");
		assertEquals(2, a.bs.length);
		assertEquals(11, a.bs[0].id);
		assertEquals("b1", a.bs[0].name);
		assertEquals(22, a.bs[1].id);
		assertEquals("b2", a.bs[1].name);
	}

	public void testFetchA4() {
		A a = nut.getObject(A.class, "a4");
		assertEquals(2, a.bs.length);
		assertEquals(9, a.bs[0].id);
		assertEquals("f1", a.bs[0].name);
		assertEquals(10, a.bs[1].id);
		assertEquals("uu", a.bs[1].name);
	}

	public void testFetchB1() {
		B b = nut.getObject(null, "b1");
		assertEquals(11, b.id);
		assertEquals("b1", b.name);
	}

	public void testFetchA5() {
		A a = nut.getObject(A.class, "a5");
		assertEquals(2, a.bs.length);
		assertEquals(11, a.bs[0].id);
		assertEquals("b1", a.bs[0].name);
		assertEquals(22, a.bs[1].id);
		assertEquals("b2", a.bs[1].name);
	}

	public void testFetchB_misc() throws NoSuchFieldException {
		B b = nut.getObject(B.class, "b-misc");
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
		IocFile iof = nut.getObject(IocFile.class, "ioc-file1");
		assertTrue(iof.file.isDirectory());
	}

	public static class O {
		private String name;
	}

	public static String getOName() {
		return "xyz";
	}

	public void testCallJava() throws Exception {
		O o = nut.getObject(O.class, "o1");
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

	public void testInjectByArgAsObject() {
		C c = nut.getObject(C.class, "c1");
		assertEquals("abc", c.email.getAccount());
		assertEquals("263.net", c.email.getHost());
	}

	public void testInjectByFieldsAsObject() {
		C c = nut.getObject(C.class, "c2");
		assertEquals("abc", c.email.getAccount());
		assertEquals("263.net", c.email.getHost());
	}

	public static class D {
		private Map<String, C> map;
	}

	public void testMapAttribute() {
		D d = nut.getObject(D.class, "d1");
		assertEquals(2,d.map.size());
		assertEquals("abc@263.net",d.map.get("cc1").email.toString());
		assertEquals("abc@263.net",d.map.get("cc2").email.toString());
	}

}
