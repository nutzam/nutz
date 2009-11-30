package org.nutz;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.aop.AllAop;
import org.nutz.ioc.AllIoc;
import org.nutz.json.AllJson;
import org.nutz.lang.AllLang;
import org.nutz.mvc.AllMvc;

@RunWith(Suite.class)
@Suite.SuiteClasses({AllLang.class, AllJson.class, AllIoc.class, AllMvc.class, AllAop.class})
public class AllWithoutDB {}
