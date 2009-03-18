package com.zzh.json;

import junit.framework.Test;
import junit.framework.TestSuite;

public class JosnAllTest {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for Nutz.json");
		// Json
		suite.addTestSuite(JsonTest.class);
		suite.addTestSuite(JsonCommentTest.class);
		suite.addTestSuite(JsonRecursiveTest.class);
		// $JUnit-END$
		return suite;
	}
}
