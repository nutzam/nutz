package com.zzh;

import com.zzh.dao.entity.EntityHolderTest;
import com.zzh.dao.impl.FileSQLManagerTest;
import com.zzh.dao.impl.NutDaoTest;
import com.zzh.dao.impl.NutSQLMakerTest;
import com.zzh.json.JsonTest;
import com.zzh.lang.MirrorTest;
import com.zzh.lang.types.CastorTest;
import com.zzh.mvc.MvcSupportTest;
import com.zzh.segment.CharSegmentTest;
import com.zzh.trans.TransactionTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Prepare a database with URL: jdbc:mysql://localhost:3306/zzhtest support
 * user root@123456
 * 
 * @author zozoh
 * 
 */
public class TestAll {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for Nutz");
		// $JUnit-BEGIN$
		// DAO
		suite.addTestSuite(EntityHolderTest.class);
		suite.addTestSuite(FileSQLManagerTest.class);
		suite.addTestSuite(NutSQLMakerTest.class);
		suite.addTestSuite(NutDaoTest.class);
		// Json
		suite.addTestSuite(JsonTest.class);
		// Lang
		suite.addTestSuite(CastorTest.class);
		suite.addTestSuite(MirrorTest.class);
		// MVC
		suite.addTestSuite(MvcSupportTest.class);
		// Parsers
		suite.addTestSuite(CharSegmentTest.class);
		// Utils
		// suite.addTestSuite(ClassUtilsTest.class);
		// Transaction
		suite.addTestSuite(TransactionTest.class);
		// $JUnit-END$
		return suite;
	}

}
