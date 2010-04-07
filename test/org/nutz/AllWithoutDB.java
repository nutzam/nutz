package org.nutz;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.aop.AllAop;
import org.nutz.filepool.AllFilePool;
import org.nutz.http.AllHttp;
import org.nutz.ioc.AllIoc;
import org.nutz.json.AllJson;
import org.nutz.lang.AllLang;
import org.nutz.log.AllLog;
import org.nutz.mvc.AllMvc;

@RunWith(Suite.class)
@Suite.SuiteClasses({	AllLog.class,
						AllLang.class,
						AllJson.class,
						AllFilePool.class,
						AllIoc.class,
						AllMvc.class,
						AllAop.class,
						AllFilePool.class,
						AllHttp.class})
public class AllWithoutDB {}
