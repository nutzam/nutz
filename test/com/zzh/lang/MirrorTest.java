package com.zzh.lang;

import com.zzh.ioc.FailToMakeObjectException;

import junit.framework.TestCase;

public class MirrorTest extends TestCase {

	abstract class A<T> {
	}

	abstract class B<X, Y> {
	}

	public void testOneParam() {
		A<String> a = new A<String>() {
		};
		assertEquals(String.class, Mirror.getTypeParams(a.getClass())[0]);

	}

	public void testTwoParam() {
		B<Integer, String> b = new B<Integer, String>() {
		};
		assertEquals(Integer.class, Mirror.getTypeParams(b.getClass())[0]);
		assertEquals(String.class, Mirror.getTypeParams(b.getClass())[1]);
	}

	public void testWrapper() {
		assertTrue(Mirror.me(Integer.class).isWrpperOf(int.class));
		assertFalse(Mirror.me(Integer.class).isWrpperOf(float.class));
		assertTrue(Mirror.me(Float.class).isWrpperOf(float.class));
	}

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
		
		assertTrue(Mirror.me(FailToMakeObjectException.class).canCastToDirectly(Throwable.class));
	}

	public void testGetWrpperClass() {
		assertEquals(Boolean.class, Mirror.me(Boolean.class).getWrpperClass());
		assertEquals(Boolean.class, Mirror.me(boolean.class).getWrpperClass());
		assertEquals(Integer.class, Mirror.me(Integer.class).getWrpperClass());
		assertEquals(Integer.class, Mirror.me(int.class).getWrpperClass());
		assertEquals(Float.class, Mirror.me(Float.class).getWrpperClass());
		assertEquals(Float.class, Mirror.me(float.class).getWrpperClass());
		assertEquals(Long.class, Mirror.me(Long.class).getWrpperClass());
		assertEquals(Long.class, Mirror.me(long.class).getWrpperClass());
		assertEquals(Double.class, Mirror.me(Double.class).getWrpperClass());
		assertEquals(Double.class, Mirror.me(double.class).getWrpperClass());
		assertEquals(Byte.class, Mirror.me(Byte.class).getWrpperClass());
		assertEquals(Byte.class, Mirror.me(byte.class).getWrpperClass());
		assertEquals(Short.class, Mirror.me(Short.class).getWrpperClass());
		assertEquals(Short.class, Mirror.me(short.class).getWrpperClass());
		assertEquals(Character.class, Mirror.me(Character.class).getWrpperClass());
		assertEquals(Character.class, Mirror.me(char.class).getWrpperClass());
	}

	public void testExtractBoolean() {
		assertEquals(Boolean.class, Mirror.me(boolean.class).extractType());
	}

	public class F {
		String id;
	}

	public class FF {

		public FF(String myId) {
			fid = myId;
		}

		public FF(F f, String myId) {
			fid = f.id + myId;
		}

		String fid;
	}

	public void testBorn_innerClassNested() {
		F f = new F();
		f.id = "haha";
		FF ff = Mirror.me(FF.class).born(f, "!!!");
		assertEquals("haha!!!", ff.fid);
	}

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

	public void testBornByStaticDynamiceArgs() {
		DS ds = Mirror.me(DS.class).born(23, new String[] { "TT", "FF" });
		assertEquals(23, ds.id);
		assertEquals("FF", ds.values[1]);
	}

	public void testBornByStaticNullDynamiceArgs() {
		DS ds = Mirror.me(DS.class).born(23);
		assertEquals(23, ds.id);
		assertEquals(0, ds.values.length);
	}

	public class DD {
		public DD(int id, String... values) {
			this.id = id;
			this.values = values;
		}

		private int id;
		private String[] values;
	}

	public void testBornByInnerDynamiceArgs() {
		DD ds = Mirror.me(DD.class).born(23, new String[] { "TT", "FF" });
		assertEquals(23, ds.id);
		assertEquals("FF", ds.values[1]);
	}

	public void testBornByInnerNullDynamiceArgs() {
		DD ds = Mirror.me(DD.class).born(23);
		assertEquals(23, ds.id);
		assertEquals(0, ds.values.length);
	}

	public void testBornByInnerOuterDynamiceArgs() {
		DD ds = Mirror.me(DD.class).born(new MirrorTest(), 23);
		assertEquals(23, ds.id);
		assertEquals(0, ds.values.length);
	}

	public void testBornByParent() {
		NullPointerException e = new NullPointerException();
		FailToMakeObjectException e2 = Mirror.me(FailToMakeObjectException.class).born(e);
		assertTrue(e2.getCause() == e);
	}

}
