package com.zzh.dao;

import com.zzh.dao.entity.EntityHolderTest;
import com.zzh.dao.impl.AllTypeTest;
import com.zzh.dao.impl.DaoPersonTest;
import com.zzh.dao.impl.DynamicTableNameTest;
import com.zzh.dao.impl.FileSQLManagerTest;
import com.zzh.dao.impl.ManyOneTest;
import com.zzh.dao.impl.NotDaoTest2;
import com.zzh.dao.impl.NutDaoTest;
import com.zzh.service.tree.TreeServiceTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Prepare a database with URL: jdbc:mysql://localhost:3306/zzhtest support user
 * root@123456
 * 
 * @author zozoh
 * 
 */
public class TestDao {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for Nutz.Dao");
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
		// $JUnit-END$
		return suite;
	}

}
