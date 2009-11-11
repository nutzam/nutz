package org.nutz;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;
import org.nutz.dao.AllDao;
import org.nutz.trans.AllTrans;

@RunWith(Suite.class)
@Suite.SuiteClasses({AllTrans.class, AllDao.class})
public class AllWithDB {}
