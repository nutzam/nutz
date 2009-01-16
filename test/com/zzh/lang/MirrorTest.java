package com.zzh.lang;

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

}
