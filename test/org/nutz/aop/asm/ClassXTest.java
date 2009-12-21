package org.nutz.aop.asm;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.nutz.Nutzs;
import org.nutz.aop.AbstractMethodInterceptor;
import org.nutz.aop.ClassAgent;
import org.nutz.aop.ClassDefiner;
import org.nutz.aop.RegexMethodMatcher;
import org.nutz.aop.asm.test.Aop1;
import org.nutz.aop.asm.test.MyMethodInterceptor;
import org.nutz.lang.Mirror;

public class ClassXTest {

	ClassAgent classAgent;

	@Before
	public void setUp() {
		classAgent = new AsmClassAgent();
		classAgent.addListener(new RegexMethodMatcher(".*"), new AbstractMethodInterceptor() {});
		classAgent.addListener(new RegexMethodMatcher(".*"), new MyMethodInterceptor());
		classAgent.addListener(new RegexMethodMatcher(".*"), new MyMethodInterceptor());
		classAgent.addListener(new RegexMethodMatcher(".*"), new AbstractMethodInterceptor() {});
	}

	@Test
	public void testCreat() {
		classAgent.define(Nutzs.cd(), Object.class);
		classAgent.define(Nutzs.cd(), getClass());
	}

	@Test(expected = RuntimeException.class)
	public void testInterface() {
		classAgent.define(Nutzs.cd(), Runnable.class);
	}

	@Test
	public void testDupAop() {
		Class<Aop1> klass = Aop1.class;
		for (int i = 0; i < 10000; i++) {
			klass = classAgent.define(Nutzs.cd(), klass);
		}
		assertFalse(Aop1.class == klass);
	}

	@Test
	public void testBorn() {
		Class<Aop1> klass = classAgent.define(Nutzs.cd(), Aop1.class);
		Aop1 a1 = Mirror.me(klass).born("Nut");
		a1.returnObjectArray();
	}

	@Test
	public void testCreate2() throws Throwable {
		ClassDefiner cd = Nutzs.cd();

		Class<?> obj = classAgent.define(cd, Aop1.class);
		Class<?> obj2 = classAgent.define(cd, Aop1.class);
		assertEquals(obj, obj2);
	}

	@Test
	public void testConstructors() {
		getNewInstance(Aop1.class);
	}

	@Test
	public void testConstructor2() {
		Class<Aop1> newClass = classAgent.define(Nutzs.cd(), Aop1.class);
		assertTrue(newClass.getDeclaredConstructors().length > 0);
	}

	@Test
	public void testReturnPrimitive() throws Throwable {
		Aop1 a1 = classAgent.define(Nutzs.cd(), Aop1.class).getConstructor(String.class)
				.newInstance("Nutz");
		a1.returnLong();
		a1.returnBoolean();
		a1.returnByte();
		a1.returnChar();
		a1.returnFloat();
		a1.returnShort();
		a1.returnDouble();
	}

	@Test
	public void testReturnPrimitiveArray() {
		Aop1 a1 = getNewInstance(Aop1.class);
		a1.returnIntArray();
		a1.returnLongArray();
		a1.returnBooleanArray();
		a1.returnByteArray();
		a1.returnCharArray();
		a1.returnFloatArray();
		a1.returnShortArray();
		a1.returnDoubleArray();
	}

	@Test
	public void testReturnObject() throws Throwable {
		Aop1 a1 = getNewInstance(Aop1.class);
		a1.returnString();
		a1.returnObjectArray();
		a1.getRunnable();
		a1.getEnum();
	}

	@Test(expected = Error.class)
	public void testThrowError() throws Throwable {
		Aop1 a1 = getNewInstance(Aop1.class);
		a1.throwError();
	}

	@Test(expected = Exception.class)
	public void testThrowException() throws Throwable {
		Aop1 a1 = getNewInstance(Aop1.class);
		a1.throwException();
	}

	@Test
	public void testArgs() throws Throwable {
		Aop1 a1 = getNewInstance(Aop1.class);
		a1.nonArgsVoid();
		a1.argsVoid("Wendal is the best!");
		a1.mixObjectsVoid("Arg1", new Object(), 1, null);
		a1.mixArgsVoid("XX", "WendalXXX", 0, 'c', 1L, 9090L);
		a1.mixArgsVoid2("Aop1", Boolean.TRUE, 8888, 'p', 34L, false, 'b', "Gp", null, null, 23L,
				90L, 78L);
		String result = String.valueOf(a1.mixArgsVoid4("WendalXXX"));
		assertEquals("WendalXXX", result);
	}

	private <T> T getNewInstance(Class<T> klass) {
		Class<T> newClass = classAgent.define(Nutzs.cd(), klass);
		Mirror<T> mirror = Mirror.me(newClass);
		T obj = mirror.born("Nutz");
		System.out.println(obj.getClass().getSuperclass());
		return obj;
	}
}
