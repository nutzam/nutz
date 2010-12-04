package org.nutz.lang.reflect.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.dao.DB;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.reflect.FastBean;

public class FastBeanFactoryTest {

	@Test
	public void testReset() {
		FastBeanFactory factory = new FastBeanFactory();
		FastBean fastBean = factory.get(PojoMe.class);
		PojoMe pojo = (PojoMe)fastBean.newInstance();
		fastBean.setter(pojo, "a", 14356);
		assertEquals(fastBean.getter(pojo, "a"), 14356);
		fastBean.setter(pojo, "b", (short)14356);
		assertEquals(fastBean.getter(pojo, "b"), (short)14356);
		fastBean.setter(pojo, "c", 132465326L);
		assertEquals(fastBean.getter(pojo, "c"), 132465326L);
		fastBean.setter(pojo, "d", (byte)45);
		assertEquals(fastBean.getter(pojo, "d"), (byte)45);
		fastBean.setter(pojo, "e", (char)621);
		assertEquals(fastBean.getter(pojo, "e"), (char)621);
		fastBean.setter(pojo, "f", (double)1.3245);
		assertEquals(fastBean.getter(pojo, "f"), (double)1.3245);
		fastBean.setter(pojo, "g", (float)234.364);
		assertEquals(fastBean.getter(pojo, "g"), (float)234.364);
		fastBean.setter(pojo, "h", true);
		assertEquals(fastBean.getter(pojo, "h"), true);
		fastBean.setter(pojo, "obj", this);
		assertEquals(fastBean.getter(pojo, "obj"), this);
		fastBean.setter(pojo, "db", DB.MYSQL);
		assertEquals(fastBean.getter(pojo, "db"), DB.MYSQL);
		fastBean.setter(pojo, "at", null);
		assertEquals(fastBean.getter(pojo, "at"), null);
		String[] array = new String[]{};
		fastBean.setter(pojo, "array", array);
		assertEquals(fastBean.getter(pojo, "array"), array);
	}

	@Test
	public void testMakeFastBean() throws Throwable {
		FastBeanFactory factory = new FastBeanFactory();
		FastBean fastBean = factory.get(PojoMe.class);
		fastBean.newInstance();
		PojoMe me = null;
		
		for (int i = 0; i < 110000; i++) {
			me = PojoMe.class.newInstance();
		}
		for (int i = 0; i < 110000; i++) {
			me = (PojoMe)fastBean.newInstance();
		}
		
		Stopwatch sw = Stopwatch.begin();
		
		for (int i = 0; i < 11000000; i++) {
			me = PojoMe.class.newInstance();
		}
		sw.stop();
		System.out.println("反射 " + sw.getDuration());
		sw.start();
		for (int i = 0; i < 11000000; i++) {
			me = (PojoMe)fastBean.newInstance();
		}sw.stop();
		System.out.println("FastClass " + sw.getDuration());
		System.out.println(PojoMe.count);
		System.out.println(me);
	}

//	@Test
//	public void testOther() throws Throwable{
//		//ASMifierClassVisitor.main(new String[]{"org.nutz.lang.reflect.impl.FastBean2"});
//	}
}
