package org.nutz.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.nutz.dao.sql.SqlLiteralTest;
import org.nutz.dao.test.entity.AllEntity;
import org.nutz.dao.test.mapping.*;
import org.nutz.dao.test.normal.AllNormal;
import org.nutz.dao.test.sqls.AllSqls;
import org.nutz.dao.texp.CndTest;
import org.nutz.dao.tools.AllTools;

/**
 * Prepare a database with URL: jdbc:mysql://localhost:3306/zzhtest support user
 * root@123456
 * 
 * @author zozoh
 * 
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({AllEntity.class, AllSqls.class, AllMapping.class, AllNormal.class,
		CndTest.class, AllTools.class, SqlLiteralTest.class})
public class AllDao {}
