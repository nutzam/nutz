package org.nutz.mvc.impl;

import static org.nutz.mvc.impl.LoadingsImpl.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import my.controllers.Test2Controller;
import my.controllers.submodule.Test4Controller;
import nocontrollers.Test5Controller;

import org.junit.Assert;
import org.junit.Test;
import org.nutz.lang.Strings;
import org.nutz.mvc.ActionInfo;

import controllers.TestContro;
import controllers.TestController;
import controllers.submodule.Test3Controller;

public class LoadingsImplTest {

	@Test
	public void testUtil(){
		Assert.assertEquals("",getContrllerModule(TestController.class));
		Assert.assertEquals("submodule",getContrllerModule(Test3Controller.class));
		Assert.assertEquals("",getContrllerModule(Test2Controller.class));
		Assert.assertEquals("submodule",getContrllerModule(Test4Controller.class));
		Assert.assertEquals("",getContrllerModule(LoadingsImpl.class));
		Assert.assertEquals("",getContrllerModule(Test5Controller.class));
		
		Assert.assertEquals("test", getControllerName(TestController.class));
		Assert.assertEquals("testContro",getControllerName(TestContro.class));
		Assert.assertEquals("test3",getControllerName(Test3Controller.class));
		Assert.assertEquals("test2",getControllerName(Test2Controller.class));
		Assert.assertEquals("test4",getControllerName(Test4Controller.class));
	}
	@Test
	public void testClass(){
		Class<?> clazz = TestController.class;
		Class<?> clazz3 = Test3Controller.class;
		Class<?> clazz2 = Test2Controller.class;
		Class<?> clazz4 = Test4Controller.class;
		ActionInfo ai = null;
		ai = createInfo(clazz);
		Assert.assertNull(ai.getPaths());
		Assert.assertEquals("jsp:views.test.default",ai.getOkView());
		ai = createInfo(clazz2);
		Assert.assertNull(ai.getPaths());
		Assert.assertEquals("jsp:views.test2.default",ai.getOkView());
		ai = createInfo(clazz3);
		Assert.assertNull(ai.getPaths());
		Assert.assertEquals("jsp:views.submodule.test3.default",ai.getOkView());
		ai = createInfo(clazz4);
		Assert.assertNull(ai.getPaths());
		Assert.assertEquals("jsp:views.submodule.test4.default",ai.getOkView());
	}
	@Test
	public void testMethod() throws SecurityException, NoSuchMethodException{
		List<Class<?>> testClasses = new ArrayList<Class<?>>();
		testClasses.add(TestController.class);
		testClasses.add(Test2Controller.class);
		testClasses.add(Test3Controller.class);
		testClasses.add(Test4Controller.class);
		
		for(Class<?> clazz: testClasses){
			String clazzPre =  getContrllerModule(clazz);
			if(Strings.isEmpty(clazzPre)){
				clazzPre = "/" + getControllerName(clazz);
			}else{
				clazzPre = "/"+ clazzPre  + "/" + getControllerName(clazz);
			}
			
			ActionInfo ai = createInfo(clazz);
			Method hello = clazz.getMethod("hello");
			ActionInfo mai = createInfo(hello,clazz);
			Assert.assertEquals("/helloAt", mai.getPaths()[0]);
			Assert.assertEquals(ai.getOkView().replace("default", "hello"), mai.getOkView());
			
			Method test1 = clazz.getMethod("test1",String.class);
			mai = createInfo(test1,clazz);
			Assert.assertEquals(clazzPre+"/test1", mai.getPaths()[0]);
			Assert.assertEquals("jsp:views.test1", mai.getOkView());
			
			Method test3 = clazz.getMethod("test3");
			mai = createInfo(test3,clazz);
			Assert.assertEquals(clazzPre+"/test3", mai.getPaths()[0]);
			Assert.assertEquals(ai.getOkView().replace("default", "test3"), mai.getOkView());
			
		}
		Class<?> clazz = TestContro.class;
		ActionInfo ai = createInfo(clazz);
		Method hello = clazz.getMethod("hello");
		ActionInfo mai = createInfo(hello,clazz);
		Assert.assertEquals("/helloAt" , mai.getPaths()[0]);
		Assert.assertNull(mai.getOkView());
		
		Method test1 = clazz.getMethod("test1",String.class);
		mai = createInfo(test1,clazz);
		Assert.assertNull( mai.getPaths());
		Assert.assertEquals("jsp:views.test1", mai.getOkView());
		
		Method test3 = clazz.getMethod("test3");
		mai = createInfo(test3,clazz);
		Assert.assertNull(mai.getPaths());
		Assert.assertNull(mai.getOkView());
	}
}
