package com.zzh.ioc;

import junit.framework.Test;
import junit.framework.TestSuite;

public class IocTest {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for Nutz.Ioc");
		// $JUnit-BEGIN$
		// Nut
		suite.addTestSuite(DatabaseNutTest.class);
		suite.addTestSuite(JsonIocTest.class);
		// $JUnit-END$
		return suite;
	}
}
