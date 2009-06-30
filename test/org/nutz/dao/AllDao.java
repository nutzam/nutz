package org.nutz.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.nutz.dao.test.entity.DynamicEntityParsing;
import org.nutz.dao.test.entity.EntityParsing;
import org.nutz.dao.test.mapping.*;
import org.nutz.dao.test.normal.AllNormal;
import org.nutz.dao.test.sqls.AllSqls;
import org.nutz.dao.texp.CndTest;

/**
 * Prepare a database with URL: jdbc:mysql://localhost:3306/zzhtest support user
 * root@123456
 * 
 * @author zozoh
 * 
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { EntityParsing.class, DynamicEntityParsing.class, AllSqls.class,
		AllMapping.class, AllNormal.class, CndTest.class })
public class AllDao {}
