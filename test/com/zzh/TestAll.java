package com.zzh;

import com.zzh.castor.CastorTest;
import com.zzh.dao.entity.EntityHolderTest;
import com.zzh.dao.impl.DaoPersonTest;
import com.zzh.dao.impl.FileSQLManagerTest;
import com.zzh.dao.impl.ManyOneTest;
import com.zzh.dao.impl.NutDaoTest;
import com.zzh.ioc.DatabaseNutTest;
import com.zzh.ioc.JsonNutTest;
import com.zzh.json.JsonTest;
import com.zzh.lang.MirrorTest;
import com.zzh.lang.segment.CharSegmentTest;
import com.zzh.service.tree.TreeServiceTest;
import com.zzh.trans.TransactionTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Prepare a database with URL: jdbc:mysql://localhost:3306/zzhtest support user
 * root@123456
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
		suite.addTestSuite(NutDaoTest.class);
		suite.addTestSuite(DaoPersonTest.class);
		suite.addTestSuite(ManyOneTest.class);
		suite.addTestSuite(TreeServiceTest.class);
		// Ioc
		suite.addTestSuite(DatabaseNutTest.class);
		suite.addTestSuite(JsonNutTest.class);
		// Json
		suite.addTestSuite(JsonTest.class);
		// Castors
		suite.addTestSuite(CastorTest.class);
		// Lang
		suite.addTestSuite(MirrorTest.class);
		suite.addTestSuite(CharSegmentTest.class);
		// Transaction
		suite.addTestSuite(TransactionTest.class);
		// $JUnit-END$
		return suite;
	}

}
