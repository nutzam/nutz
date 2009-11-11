package org.nutz.lang;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;

import org.junit.Test;

import org.nutz.NutzEnum;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.test.meta.Base;

public class MirrorTest {

	public static class TDMFGADM {
		public String toString() {
			return super.toString();
		}

		public Object get() {
			return "";
		}
	}

	public static class TDMFGADMII extends TDMFGADM {
		public String toString() {
			return super.toString();
		}

		@Override
		public String get() {
			return super.get().toString();
		}

	}

	@Test
	public void test_duplicate_method_for_getAllDeclareMethods() {
		Method[] ms = Mirror.me(TDMFGADMII.class).getAllDeclaredMethodsWithoutTop();
		assertEquals(2, ms.length);
	}

	abstract class A<T> {}

	abstract class B<X, Y> {}

	@Test
	public void testOneParam() {
		A<String> a = new A<String>() {};
		assertEquals(String.class, Mirror.getTypeParams(a.getClass())[0]);

	}

	@Test
	public void testTwoParam() {
		B<Integer, String> b = new B<Integer, String>() {};
		assertEquals(Integer.class, Mirror.getTypeParams(b.getClass())[0]);
		assertEquals(String.class, Mirror.getTypeParams(b.getClass())[1]);
	}

	@Test
	public void testWrapper() {
		assertTrue(Mirror.me(Integer.class).isWrpperOf(int.class));
		assertFalse(Mirror.me(Integer.class).isWrpperOf(float.class));
		assertTrue(Mirror.me(Float.class).isWrpperOf(float.class));
	}

	@Test
	public void testCanCastToDirectly() {
		assertTrue(Mirror.me(Integer.class).canCastToDirectly(int.class));
		assertTrue(Mirror.me(int.class).canCastToDirectly(Integer.class));
		assertTrue(Mirror.me(String.class).canCastToDirectly(CharSequence.class));
		assertTrue(Mirror.me(String.class).canCastToDirectly(String.class));
		assertTrue(Mirror.me(Boolean.class).canCastToDirectly(boolean.class));
		assertTrue(Mirror.me(boolean.class).canCastToDirectly(Boolean.class));
		assertTrue(Mirror.me(int.class).canCastToDirectly(short.class));

		assertFalse(Mirror.me(int.class).canCastToDirectly(Short.class));
		assertFalse(Mirror.me(CharSequence.class).canCastToDirectly(String.class));
		assertFalse(Mirror.me(String.class).canCastToDirectly(StringBuilder.class));
		assertFalse(Mirror.me(String.class).canCastToDirectly(StringBuilder.class));
		assertFalse(Mirror.me(boolean.class).canCastToDirectly(float.class));
		assertFalse(Mirror.me(boolean.class).canCastToDirectly(short.class));

		assertTrue(Mirror.me(Character.class).canCastToDirectly(char.class));
		assertTrue(Mirror.me(Character.class).canCastToDirectly(Character.class));
		assertTrue(Mirror.me(char.class).canCastToDirectly(Character.class));
	}

	@Test
	public void testGetWrpperClass() {
		assertEquals(Boolean.class, Mirror.me(Boolean.class).getWrapperClass());
		assertEquals(Boolean.class, Mirror.me(boolean.class).getWrapperClass());
		assertEquals(Integer.class, Mirror.me(Integer.class).getWrapperClass());
		assertEquals(Integer.class, Mirror.me(int.class).getWrapperClass());
		assertEquals(Float.class, Mirror.me(Float.class).getWrapperClass());
		assertEquals(Float.class, Mirror.me(float.class).getWrapperClass());
		assertEquals(Long.class, Mirror.me(Long.class).getWrapperClass());
		assertEquals(Long.class, Mirror.me(long.class).getWrapperClass());
		assertEquals(Double.class, Mirror.me(Double.class).getWrapperClass());
		assertEquals(Double.class, Mirror.me(double.class).getWrapperClass());
		assertEquals(Byte.class, Mirror.me(Byte.class).getWrapperClass());
		assertEquals(Byte.class, Mirror.me(byte.class).getWrapperClass());
		assertEquals(Short.class, Mirror.me(Short.class).getWrapperClass());
		assertEquals(Short.class, Mirror.me(short.class).getWrapperClass());
		assertEquals(Character.class, Mirror.me(Character.class).getWrapperClass());
		assertEquals(Character.class, Mirror.me(char.class).getWrapperClass());
	}

	@Test
	public void testExtractBoolean() {
		assertEquals(boolean.class, Mirror.me(boolean.class).extractTypes()[0]);
		assertEquals(Boolean.class, Mirror.me(boolean.class).extractTypes()[1]);
	}

	@Test
	public void testExtractEnum() {
		assertEquals(NutzEnum.class, Mirror.me(NutzEnum.Dao.getClass()).extractTypes()[0]);
		assertEquals(Enum.class, Mirror.me(NutzEnum.Dao.getClass()).extractTypes()[1]);
	}

	@Test
	public void testExtractChar() {
		Class<?>[] types = Mirror.me(char.class).extractTypes();
		assertEquals(3, types.length);
		assertEquals(char.class, types[0]);
		assertEquals(Character.class, types[1]);
	}

	public static class F {
		@Id
		String id;
	}

	public static class SubF extends F {
		@Name
		String id;
	}

	public static class FF {

		public FF(String myId) {
			fid = myId;
		}

		public FF(F f, String myId) {
			fid = f.id + myId;
		}

		String fid;
	}

	@Test
	public void test_get_fields() {
		Field[] fields = Mirror.me(SubF.class).getFields();
		assertEquals(1, fields.length);
		assertNotNull(fields[0].getAnnotation(Name.class));
	}

	@Test
	public void testBorn_innerClassNested() {
		F f = new F();
		f.id = "haha";
		FF ff = Mirror.me(FF.class).born(f, "!!!");
		assertEquals("haha!!!", ff.fid);
	}

	@Test
	public void testBorn_innerClassDefaultNested() {
		FF ff = Mirror.me(FF.class).born("!!!");
		assertEquals("!!!", ff.fid);
	}

	public static class DS {
		public DS(int id, String... values) {
			this.id = id;
			this.values = values;
		}

		private int id;
		private String[] values;
	}

	@Test
	public void testBornByStaticDynamiceArgs() {
		DS ds = Mirror.me(DS.class).born(23, new String[] { "TT", "FF" });
		assertEquals(23, ds.id);
		assertEquals("FF", ds.values[1]);
	}

	@Test
	public void testBornByStaticNullDynamiceArgs() {
		DS ds = Mirror.me(DS.class).born(23);
		assertEquals(23, ds.id);
		assertEquals(0, ds.values.length);
	}

	public static class DD {
		public DD(int id, String... values) {
			this.id = id;
			this.values = values;
		}

		private int id;
		private String[] values;
	}

	@Test
	public void testBornByInnerDynamiceArgs() {
		DD ds = Mirror.me(DD.class).born(23, new String[] { "TT", "FF" });
		assertEquals(23, ds.id);
		assertEquals("FF", ds.values[1]);
	}

	@Test
	public void testBornByInnerNullDynamiceArgs() {
		DD ds = Mirror.me(DD.class).born(23);
		assertEquals(23, ds.id);
		assertEquals(0, ds.values.length);
	}

	@Test
	public void testBornByInnerOuterDynamiceArgs() {
		DD ds = Mirror.me(DD.class).born(23);
		assertEquals(23, ds.id);
		assertEquals(0, ds.values.length);
	}

	@Test
	public void testBornByParent() {
		NullPointerException e = new NullPointerException();
		RuntimeException e2 = Mirror.me(RuntimeException.class).born(e);
		assertTrue(e2.getCause() == e);
	}

	@Test
	public void testBornByStatic() {
		Calendar c = Mirror.me(Calendar.class).born();
		assertNotNull(c);
		Integer ii = Mirror.me(Integer.class).born(34);
		assertTrue(34 == ii);
	}

	public static class DDD {
		public String[] args;
		public String[] x_args;

		public DDD(String... args) {
			this.args = args;
		}

		public void x(String... args) {
			this.x_args = args;
		}
	}

	@Test
	public void testBornByDynamicArgs() throws Exception {
		DDD d = Mirror.me(DDD.class).born((Object) Lang.array("abc", "bcd"));
		assertEquals(2, d.args.length);
		assertEquals("abc", d.args[0]);
		assertEquals("bcd", d.args[1]);
	}

	@Test
	public void testInvokeByDynamicArgs() throws Exception {
		DDD d = Mirror.me(DDD.class).born((Object) Lang.array("abc", "bcd"));
		Mirror.me(DDD.class).invoke(d, "x", (Object[]) Lang.array("F", "Z"));
		assertEquals(2, d.x_args.length);
		assertEquals("F", d.x_args[0]);
		assertEquals("Z", d.x_args[1]);
	}

	@Test
	public void testBornByDynamicArgsNull() throws Exception {
		DDD d = Mirror.me(DDD.class).born();
		assertEquals(0, d.args.length);
	}

	@Test
	public void testBornByDynamicArgsObjectArray() throws Exception {
		Object[] args = new Object[2];
		args[0] = "A";
		args[1] = "B";
		DDD d = Mirror.me(DDD.class).born(args);
		assertEquals(2, d.args.length);
		assertEquals("A", d.args[0]);
		assertEquals("B", d.args[1]);
		d = Mirror.me(DDD.class).born("A", "B");
		assertEquals(2, d.args.length);
		assertEquals("A", d.args[0]);
		assertEquals("B", d.args[1]);
	}

	public static int testStaticMethod(long l) {
		return (int) (l * 10);
	}

	@Test
	public void testInvokeStatic() {
		int re = (Integer) Mirror.me(this.getClass()).invoke(null, "testStaticMethod", 45L);
		assertEquals(450, re);
	}

	public boolean testMethod(String s) {
		return Boolean.valueOf(s);
	}

	@Test
	public void testInvoke() {
		boolean re = (Boolean) Mirror.me(this.getClass()).invoke(this, "testMethod", "true");
		assertTrue(re);
	}

	public static int invokeByInt(int abc) {
		return abc * 10;
	}

	@Test
	public void testInvokeByWrapper() {
		int re = (Integer) Mirror.me(this.getClass()).invoke(this, "invokeByInt", 23);
		assertEquals(230, re);
	}

	public static class SV {
		private int id;
		private char cc;
	}

	@Test
	public void testSetValue() {
		SV sv = new SV();
		Mirror.me(SV.class).setValue(sv, "id", 200);
		Mirror.me(SV.class).setValue(sv, "cc", 'T');
		assertEquals(200, sv.id);
		assertEquals('T', sv.cc);
		Mirror.me(SV.class).setValue(sv, "id", null);
		Mirror.me(SV.class).setValue(sv, "cc", null);
		assertEquals(0, sv.id);
		assertEquals(0, (int) sv.cc);
	}

	@Test
	public void set_null_value_by_invoking() {
		Base base = Base.make("Temp");
		Mirror<Base> mirror = Mirror.me(Base.class);
		mirror.invoke(base, "setName", (Object) null);
		assertNull(base.getName());
		base.setName("FYZ");
		try {
			mirror.invoke(base, "setName", (Object[]) null);
			fail();
		} catch (Exception e) {}
	}

}
