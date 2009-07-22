package org.nutz;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.nutz.aop.AllAop;
import org.nutz.dao.AllDao;
import org.nutz.ioc.AllIoc;
import org.nutz.json.AllJson;
import org.nutz.lang.AllLang;
import org.nutz.service.AllService;
import org.nutz.trans.AllTrans;

/**
 * Prepare a database with URL: jdbc:mysql://localhost:3306/zzhtest support user
 * root@123456
 * 
 * @author zozoh
 * 
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { AllLang.class, AllJson.class, AllDao.class, AllService.class,
		AllTrans.class, AllIoc.class, AllAop.class})
public class TestAll {}
