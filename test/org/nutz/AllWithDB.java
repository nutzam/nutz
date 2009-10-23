package org.nutz;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;
import org.nutz.dao.AllDao;
import org.nutz.ioc.AllIocWithDB;
import org.nutz.trans.AllTrans;

@RunWith(Suite.class)
@Suite.SuiteClasses({AllIocWithDB.class, AllTrans.class, AllDao.class})
public class AllWithDB {}
