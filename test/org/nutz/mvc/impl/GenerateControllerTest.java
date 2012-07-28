package org.nutz.mvc.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.annotation.AnnotationIocLoader2;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.lang.Mirror;

import controllers.OrdersController;
import controllers.TestController;

public class GenerateControllerTest {

	@Test
	public void testDump() throws Exception{
		Class<?> clazz = GenerateController.dump(OrdersController.class);
		Method m = clazz.getMethod("index");
		Assert.assertNotNull(m);
		Field f = clazz.getDeclaredField("dao");
		Assert.assertNotNull(f);
		Assert.assertEquals("org.nutz.dao.Dao",f.getType().getName());
		Mirror<?> mirror = Mirror.me(clazz);
		clazz.getDeclaredFields();
		Field[] fields = mirror.getFields(Inject.class);
		Assert.assertEquals(0, fields.length);
		Method[] methods = clazz.getMethods();
		for(Method m1 : methods){
			//m1.getAnnotation(Inject.class);
			m1.getAnnotations();
		}
//		Object module = Mirror.me(clazz).born();
//		Object module2 = new OrdersController();
		
		// init by ioc
		NutIoc ioc = new NutIoc(new ComboIocLoader(new JsonLoader("dao.js"),new AnnotationIocLoader2("controllers")));
		Object module = ioc.get(clazz, clazz.getName());
		Method m2 = clazz.getMethod("create", null);
		m2.invoke(module, new Object[]{});
	}
	
}
