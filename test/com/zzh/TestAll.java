package com.zzh;

import com.zzh.castor.CastorTest;
import com.zzh.dao.entity.EntityHolderTest;
import com.zzh.dao.impl.AllTypeTest;
import com.zzh.dao.impl.DaoPersonTest;
import com.zzh.dao.impl.DynamicTableNameTest;
import com.zzh.dao.impl.FileSQLManagerTest;
import com.zzh.dao.impl.ManyOneTest;
import com.zzh.dao.impl.NotDaoTest2;
import com.zzh.dao.impl.NutDaoTest;
import com.zzh.ioc.DatabaseNutTest;
import com.zzh.ioc.JsonIocTest;
import com.zzh.json.JsonCommentTest;
import com.zzh.json.JsonRecursiveTest;
import com.zzh.json.JsonTest;
import com.zzh.lang.random.ArrayRandomTest;
import com.zzh.lang.LangTest;
import com.zzh.lang.MirrorTest;
import com.zzh.lang.segment.CharSegmentTest;
import com.zzh.lang.util.LinkedCharArrayTest;
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
		suite.addTestSuite(DynamicTableNameTest.class);
		suite.addTestSuite(EntityHolderTest.class);
		suite.addTestSuite(FileSQLManagerTest.class);
		suite.addTestSuite(NutDaoTest.class);
		suite.addTestSuite(DaoPersonTest.class);
		suite.addTestSuite(ManyOneTest.class);
		suite.addTestSuite(TreeServiceTest.class);
		suite.addTestSuite(AllTypeTest.class);
		suite.addTestSuite(NotDaoTest2.class);
		// Nut
		suite.addTestSuite(DatabaseNutTest.class);
		suite.addTestSuite(JsonIocTest.class);
		// Json
		suite.addTestSuite(JsonTest.class);
		suite.addTestSuite(JsonCommentTest.class);
		suite.addTestSuite(JsonRecursiveTest.class);
		// Castors
		suite.addTestSuite(CastorTest.class);
		// Lang
		suite.addTestSuite(MirrorTest.class);
		suite.addTestSuite(LangTest.class);
		suite.addTestSuite(CharSegmentTest.class);
		suite.addTestSuite(ArrayRandomTest.class);
		suite.addTestSuite(LinkedCharArrayTest.class);
		// Transaction
		suite.addTestSuite(TransactionTest.class);
		// $JUnit-END$
		return suite;
	}

}
