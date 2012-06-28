package org.nutz.aop.asm;

import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.PROTECTED;
import static java.lang.reflect.Modifier.PUBLIC;
import static java.lang.reflect.Modifier.TRANSIENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Test;
import org.nutz.Nutzs;
import org.nutz.aop.ClassAgent;
import org.nutz.aop.ClassDefiner;
import org.nutz.aop.interceptor.LoggingMethodInterceptor;
import org.nutz.aop.javassist.lstn.MethodCounter;
import org.nutz.aop.javassist.lstn.RhinocerosListener;
import org.nutz.aop.javassist.meta.Buffalo;
import org.nutz.aop.javassist.meta.Hippo;
import org.nutz.aop.javassist.meta.Moose;
import org.nutz.aop.javassist.meta.Rhinoceros;
import org.nutz.aop.javassist.meta.Vegetarian;
import org.nutz.aop.javassist.meta.Vegetarians;
import org.nutz.aop.javassist.meta.Vegetarian.BEH;
import org.nutz.aop.matcher.MethodMatcherFactory;
import org.nutz.json.Json;
import org.nutz.lang.Mirror;

public class AsmClassAgentTest {

    @Test
    public void test_duplicate_class_exception() throws Exception {
        int[] cc = new int[4];
        ClassAgent ca = getNewClassAgent();
        ca.addInterceptor(MethodMatcherFactory.matcher(".*"), new MethodCounter(cc));
        ClassAgent ca2 = getNewClassAgent();
        ca2.addInterceptor(MethodMatcherFactory.matcher(".*"), new MethodCounter(cc));

        ClassDefiner cd = Nutzs.cd();

        Class<? extends Moose> c = ca.define(cd, Moose.class);
        Moose m = c.newInstance();
        m.doSomething(BEH.run);
        assertEquals("[2, 2, 0, 0]", Json.toJson(cc));

        Class<? extends Moose> c2 = ca2.define(cd, Moose.class);
        assertEquals(c, c2);
        m = c.newInstance();
        m.doSomething(BEH.run);
        assertEquals("[4, 4, 0, 0]", Json.toJson(cc));
    }

    @Test
    public void test_return_array_method() {
        int[] cc = new int[4];
        Arrays.fill(cc, 0);
        ClassAgent aca = getNewClassAgent();
        aca.addInterceptor(MethodMatcherFactory.matcher("returnArrayMethod"), new MethodCounter(cc));
        Class<? extends Buffalo> c = aca.define(Nutzs.cd(), Buffalo.class);// RA.class;
        Buffalo r = Mirror.me(c).born();
        String[] ss = r.returnArrayMethod();
        assertEquals("[1, 1, 0, 0]", Json.toJson(cc));
        assertEquals(3, ss.length);
    }

    @Test
    public void test_basice_matcher() throws Exception {
        Method method = Vegetarian.class.getDeclaredMethod("doSomething", BEH.class);
        assertTrue(MethodMatcherFactory.matcher().match(method));
        assertTrue(MethodMatcherFactory.matcher(PUBLIC).match(method));
        assertFalse(MethodMatcherFactory.matcher(PROTECTED).match(method));
        assertFalse(MethodMatcherFactory.matcher(TRANSIENT).match(method));
        assertFalse(MethodMatcherFactory.matcher(PRIVATE).match(method));

        method = Vegetarian.class.getDeclaredMethod("run", int.class);
        assertTrue(MethodMatcherFactory.matcher().match(method));
        assertFalse(MethodMatcherFactory.matcher(PUBLIC).match(method));
        assertTrue(MethodMatcherFactory.matcher(PROTECTED).match(method));
        assertFalse(MethodMatcherFactory.matcher(TRANSIENT).match(method));
        assertFalse(MethodMatcherFactory.matcher(PRIVATE).match(method));

        method = Vegetarian.class.getDeclaredMethod("defaultMethod");
        assertTrue(MethodMatcherFactory.matcher().match(method));
        assertFalse(MethodMatcherFactory.matcher(PUBLIC).match(method));
        assertFalse(MethodMatcherFactory.matcher(PROTECTED).match(method));
        assertTrue(MethodMatcherFactory.matcher(TRANSIENT).match(method));
        assertFalse(MethodMatcherFactory.matcher(PRIVATE).match(method));

        method = Vegetarian.class.getDeclaredMethod("privateMethod");
        assertTrue(MethodMatcherFactory.matcher().match(method));
        assertFalse(MethodMatcherFactory.matcher(PUBLIC).match(method));
        assertFalse(MethodMatcherFactory.matcher(PROTECTED).match(method));
        assertFalse(MethodMatcherFactory.matcher(TRANSIENT).match(method));
        assertTrue(MethodMatcherFactory.matcher(PRIVATE).match(method));
    }

    @Test
    public void test_basice_matcher_by_name() throws Exception {
        Method method = Vegetarian.class.getDeclaredMethod("doSomething", BEH.class);
        assertTrue(MethodMatcherFactory.matcher("doSomething").match(method));
    }

    @Test
    public void test_basice_listener() {
        int[] cc = new int[4];
        int[] crun = new int[4];
        Arrays.fill(cc, 0);
        Arrays.fill(crun, 0);
        ClassAgent aca = getNewClassAgent();
        aca.addInterceptor(MethodMatcherFactory.matcher("run"), new MethodCounter(crun));
        aca.addInterceptor(MethodMatcherFactory.matcher(".*"), new MethodCounter(cc));
        aca.addInterceptor(MethodMatcherFactory.matcher("doSomething"), new RhinocerosListener());
        Class<? extends Rhinoceros> c = aca.define(Nutzs.cd(), Rhinoceros.class);// RA.class;
        Rhinoceros r = Mirror.me(c).born();
        r.doSomething(BEH.run);
        r.doSomething(BEH.fight);
        try {
            r.doSomething(BEH.lecture);
            fail();
        }
        catch (Throwable e) {}
        try {
            r.doSomething(BEH.fly);
            fail();
        }
        catch (Throwable e) {}
//        assertEquals("[5, 3, 1, 1]", Json.toJson(cc));
        assertEquals("[1, 1, 0, 0]", Json.toJson(crun));
    }

    @Test
    public void test_basice_matcher_by_mod() {
        int[] cpub = new int[4];
        int[] cpro = new int[4];
        Arrays.fill(cpub, 0);
        Arrays.fill(cpro, 0);
        ClassAgent aca = getNewClassAgent();
        aca.addInterceptor(MethodMatcherFactory.matcher(PUBLIC), new MethodCounter(cpub));
        aca.addInterceptor(MethodMatcherFactory.matcher(PROTECTED), new MethodCounter(cpro));
        Class<? extends Hippo> c = aca.define(Nutzs.cd(), Hippo.class);// RA.class;
        Hippo r = Mirror.me(c).born();
        Vegetarians.run(r, 78);
        r.doSomething(BEH.run);
        try {
            r.doSomething(BEH.lecture);
            fail();
        }
        catch (Throwable e) {}
        try {
            r.doSomething(BEH.fly);
            fail();
        }
        catch (Throwable e) {}
        assertEquals("[3, 1, 1, 1]", Json.toJson(cpub));
        assertEquals("[2, 2, 0, 0]", Json.toJson(cpro));
    }

    public ClassAgent getNewClassAgent() {
        ClassAgent classAgent = new AsmClassAgent();
        classAgent.addInterceptor(MethodMatcherFactory.matcher(".*"), new LoggingMethodInterceptor());
        return classAgent;
    }
}
