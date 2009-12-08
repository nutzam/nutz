package org.nutz.aop.javassist;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Test;

import org.nutz.aop.Aop;
import org.nutz.aop.ClassAgent;
import org.nutz.aop.javassist.lstn.MethodCounter;
import org.nutz.aop.javassist.lstn.RhinocerosListener;
import org.nutz.aop.javassist.meta.Vegetarians;
import org.nutz.aop.javassist.meta.Vegetarian.BEH;
import org.nutz.aop.javassist.meta.Buffalo;
import org.nutz.aop.javassist.meta.Moose;
import org.nutz.aop.javassist.meta.Rhinoceros;
import org.nutz.aop.javassist.meta.Hippo;
import org.nutz.aop.javassist.meta.Vegetarian;

import static org.junit.Assert.*;
import static java.lang.reflect.Modifier.*;

import org.nutz.json.Json;
import org.nutz.lang.Mirror;

public class JavassistClassAgentTest {

	@Test
	public void test_duplicate_class_exception() throws Exception {
		int[] cc = new int[4];
		ClassAgent ca = getNewClassAgent();
		ca.addListener(Aop.matcher(".*"), new MethodCounter(cc));
		ClassAgent ca2 = getNewClassAgent();
		ca2.addListener(Aop.matcher(".*"), new MethodCounter(cc));

		Class<? extends Moose> c = ca.define(Moose.class);
		Moose m = c.newInstance();
		m.doSomething(BEH.run);
		assertEquals("[2, 2, 0, 0]", Json.toJson(cc));
		
		Class<? extends Moose> c2 = ca2.define(Moose.class);
		assertEquals(c,c2);
		m = c.newInstance();
		m.doSomething(BEH.run);
		assertEquals("[4, 4, 0, 0]", Json.toJson(cc));
	}

	@Test
	public void test_return_array_method() {
		int[] cc = new int[4];
		Arrays.fill(cc, 0);
		ClassAgent aca = getNewClassAgent();
		aca.addListener(Aop.matcher("returnArrayMethod"), new MethodCounter(cc));
		Class<? extends Buffalo> c = aca.define(Buffalo.class);// RA.class;
		Buffalo r = Mirror.me(c).born();
		String[] ss = r.returnArrayMethod();
		assertEquals("[1, 1, 0, 0]", Json.toJson(cc));
		assertEquals(3, ss.length);
	}

	@Test
	public void test_basice_matcher() throws Exception {
		Method method = Vegetarian.class.getDeclaredMethod("doSomething", BEH.class);
		assertTrue(Aop.matcher().match(method));
		assertTrue(Aop.matcher(PUBLIC).match(method));
		assertFalse(Aop.matcher(PROTECTED).match(method));
		assertFalse(Aop.matcher(TRANSIENT).match(method));
		assertFalse(Aop.matcher(PRIVATE).match(method));

		method = Vegetarian.class.getDeclaredMethod("run", int.class);
		assertTrue(Aop.matcher().match(method));
		assertFalse(Aop.matcher(PUBLIC).match(method));
		assertTrue(Aop.matcher(PROTECTED).match(method));
		assertFalse(Aop.matcher(TRANSIENT).match(method));
		assertFalse(Aop.matcher(PRIVATE).match(method));

		method = Vegetarian.class.getDeclaredMethod("defaultMethod");
		assertTrue(Aop.matcher().match(method));
		assertFalse(Aop.matcher(PUBLIC).match(method));
		assertFalse(Aop.matcher(PROTECTED).match(method));
		assertTrue(Aop.matcher(TRANSIENT).match(method));
		assertFalse(Aop.matcher(PRIVATE).match(method));

		method = Vegetarian.class.getDeclaredMethod("privateMethod");
		assertTrue(Aop.matcher().match(method));
		assertFalse(Aop.matcher(PUBLIC).match(method));
		assertFalse(Aop.matcher(PROTECTED).match(method));
		assertFalse(Aop.matcher(TRANSIENT).match(method));
		assertTrue(Aop.matcher(PRIVATE).match(method));
	}

	@Test
	public void test_basice_matcher_by_name() throws Exception {
		Method method = Vegetarian.class.getDeclaredMethod("doSomething", BEH.class);
		assertTrue(Aop.matcher("doSomething").match(method));
	}

	@Test
	public void test_basice_listener() {
		int[] cc = new int[4];
		int[] crun = new int[4];
		Arrays.fill(cc, 0);
		Arrays.fill(crun, 0);
		ClassAgent aca = getNewClassAgent();
		aca.addListener(Aop.matcher(".*"), new MethodCounter(cc));
		aca.addListener(Aop.matcher("run"), new MethodCounter(crun));
		aca.addListener(Aop.matcher("doSomething"), new RhinocerosListener());
		Class<? extends Rhinoceros> c = aca.define(Rhinoceros.class);// RA.class;
		Rhinoceros r = Mirror.me(c).born();
		r.doSomething(BEH.run);
		r.doSomething(BEH.fight);
		try {
			r.doSomething(BEH.lecture);
			fail();
		} catch (Throwable e) {}
		try {
			r.doSomething(BEH.fly);
			fail();
		} catch (Throwable e) {}
		assertEquals("[5, 3, 1, 1]", Json.toJson(cc));
		assertEquals("[1, 1, 0, 0]", Json.toJson(crun));
	}

	@Test
	public void test_basice_matcher_by_mod() {
		int[] cpub = new int[4];
		int[] cpro = new int[4];
		Arrays.fill(cpub, 0);
		Arrays.fill(cpro, 0);
		ClassAgent aca = getNewClassAgent();
		aca.addListener(Aop.matcher(PUBLIC), new MethodCounter(cpub));
		aca.addListener(Aop.matcher(PROTECTED), new MethodCounter(cpro));
		Class<? extends Hippo> c = aca.define(Hippo.class);// RA.class;
		Hippo r = Mirror.me(c).born();
		Vegetarians.run(r, 78);
		r.doSomething(BEH.run);
		try {
			r.doSomething(BEH.lecture);
			fail();
		} catch (Throwable e) {}
		try {
			r.doSomething(BEH.fly);
			fail();
		} catch (Throwable e) {}
		assertEquals("[3, 1, 1, 1]", Json.toJson(cpub));
		assertEquals("[2, 2, 0, 0]", Json.toJson(cpro));
	}

	
	public ClassAgent getNewClassAgent(){
		return new JavassistClassAgent();
	}
}
