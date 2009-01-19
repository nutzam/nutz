package com.zzh.util;

import java.util.List;

import com.zzh.lang.ClassUtils;

import junit.framework.TestCase;

public class ClassUtilsTest extends TestCase {

	public void testFindImplementationClassesInClasspath() {
		List<Class<ClassUtilsTest>> classes = ClassUtils.findImplementationClassesInClasspath(
				ClassUtilsTest.class, TestCase.class.getClassLoader());
		assertNotNull(classes);
		assertEquals(1, classes.size());
		assertEquals(classes.get(0), ClassUtilsTest.class);
	}

	public void testFindImplementationClassesInClasspathWithSecifiedFilter() {
		ClassUtils.setExcludedPackages("nutagi.util,");
		List<Class<ClassUtilsTest>> classes = ClassUtils.findImplementationClassesInClasspath(
				ClassUtilsTest.class, TestCase.class.getClassLoader());
		assertNotNull(classes);
		assertEquals(0, classes.size());
	}

	public void testFindImplementationClassesInClasspathWithEmptyFilter() {
		ClassUtils.setExcludedPackages(null);
		List<Class<ClassUtilsTest>> classes = ClassUtils.findImplementationClassesInClasspath(
				ClassUtilsTest.class, TestCase.class.getClassLoader());
		assertNotNull(classes);
		assertEquals(1, classes.size());
		assertEquals(classes.get(0), ClassUtilsTest.class);
	}

}
