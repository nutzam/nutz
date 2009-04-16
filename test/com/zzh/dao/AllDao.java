package com.zzh.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.zzh.dao.test.entity.DynamicEntityParsing;
import com.zzh.dao.test.entity.EntityParsing;
import com.zzh.dao.test.mapping.*;
import com.zzh.dao.test.normal.SupportedFieldType;
import com.zzh.dao.test.sqls.SQLFileParsing;
import com.zzh.dao.texp.CndTest;

/**
 * Prepare a database with URL:
 * jdbc:mysql://localhost:3306/zzhtest support user root@123456
 * 
 * @author zozoh
 * 
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { EntityParsing.class, DynamicEntityParsing.class, SQLFileParsing.class,
		AllMapping.class, SupportedFieldType.class, CndTest.class })
public class AllDao {}
