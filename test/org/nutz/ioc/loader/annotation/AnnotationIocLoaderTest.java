package org.nutz.ioc.loader.annotation;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.annotation.meta.ClassA;

public class AnnotationIocLoaderTest {
	
	Ioc ioc = new NutIoc(new AnnotationIocLoader("org.nutz.ioc.loader.annotation"));

	@Test
	public void testGetName() {
		assertNotNull(ioc.getNames());
		assertTrue(ioc.getNames().length > 0);
	}

	@Test
	public void testHas() {
		assertTrue(ioc.has("classA"));
	}

	@Test
	public void testLoad() {
		assertTrue(ioc.get(null, "classA") instanceof ClassA);
	}

}
