package com.zzh;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.zzh.dao.mysql.MysqlTest;
import com.zzh.dao.psql.PostgresqlTest;

public class TestSpecialDatabase {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for Nutz.Dao for special DB");
		// $JUnit-BEGIN$
		suite.addTestSuite(MysqlTest.class);
		suite.addTestSuite(PostgresqlTest.class);
		// $JUnit-END$
		return suite;
	}

}
