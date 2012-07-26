package org.nutz.mvc.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;
import org.nutz.ioc.loader.annotation.Inject;
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
	}
	
}
