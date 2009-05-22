package com.zzh.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.zzh.dao.test.entity.DynamicEntityParsing;
import com.zzh.dao.test.entity.EntityParsing;
import com.zzh.dao.test.mapping.*;
import com.zzh.dao.test.normal.AllNormal;
import com.zzh.dao.test.sqls.AllSqls;
import com.zzh.dao.texp.CndTest;

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
