package org.nutz.ioc.loader.annotation;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.loader.annotation.meta.ClassController;
import org.nutz.ioc.meta.IocObject;
import org.nutz.mvc.annotation.IocObj;

public class AnnotationIocLoader2Test {

	IocLoader iocLoader = new AnnotationIocLoader2("org.nutz.ioc.loader.annotation.meta");


	@Test
	public void testLoad() throws Throwable {
		IocObject iocObject = iocLoader.load(null, ClassController.class.getName());
		assertNotNull(iocObject);
		assertNotNull(iocObject.getFields());
		assertTrue(iocObject.getFields().length == 1);
		assertEquals("copyOfClassService", iocObject.getFields()[0].getName());
		assertEquals("refer", iocObject.getFields()[0].getValue().getType());
		assertEquals("copyOfClassService", iocObject.getFields()[0].getValue().getValue());
		
		iocObject = iocLoader.load(null, "copyOfClassService");
		assertNotNull(iocObject);
		try{
			iocObject = iocLoader.load(null, "classC");
			assertTrue(false);
		}catch (ObjectLoadException e) {
			assertTrue(true);
		}
		
		
	}

}
