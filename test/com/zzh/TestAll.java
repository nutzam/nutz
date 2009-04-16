package com.zzh;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.zzh.dao.AllDao;
import com.zzh.ioc.AllIoc;
import com.zzh.json.AllJson;
import com.zzh.lang.AllLang;
import com.zzh.service.AllService;
import com.zzh.trans.AllTrans;

/**
 * Prepare a database with URL: jdbc:mysql://localhost:3306/zzhtest support user
 * root@123456
 * 
 * @author zozoh
 * 
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { AllLang.class, AllJson.class, AllDao.class, AllService.class,
		AllTrans.class, AllIoc.class, })
public class TestAll {}
