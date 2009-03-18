package com.zzh.lang;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.zzh.lang.random.ArrayRandomTest;
import com.zzh.lang.segment.CharSegmentTest;
import com.zzh.lang.util.LinkedCharArrayTest;

public class TestAllLang {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for Nutz.Lang");
		// $JUnit-BEGIN$
		// DAO
		suite.addTestSuite(MirrorTest.class);
		suite.addTestSuite(LangTest.class);
		suite.addTestSuite(CharSegmentTest.class);
		suite.addTestSuite(ArrayRandomTest.class);
		suite.addTestSuite(LinkedCharArrayTest.class);
		// $JUnit-END$
		return suite;
	}
}
