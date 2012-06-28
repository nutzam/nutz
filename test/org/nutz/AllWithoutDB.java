package org.nutz;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.aop.AllAop;
import org.nutz.el.AllEl;
import org.nutz.filepool.AllFilePool;
import org.nutz.http.AllHttp;
import org.nutz.ioc.AllIoc;
import org.nutz.json.AllJson;
import org.nutz.lang.AllLang;
import org.nutz.log.AllLog;
import org.nutz.mvc.AllMvc;
import org.nutz.plugin.AllPlugin;
import org.nutz.resource.AllResourceScan;

@RunWith(Suite.class)
@Suite.SuiteClasses({    AllLang.class,
                        AllJson.class,
                        AllFilePool.class,
                        AllIoc.class,
                        AllAop.class,
                        AllFilePool.class,
                        AllHttp.class,
                        AllResourceScan.class,
                        AllMvc.class,
                        AllEl.class,
                        AllLog.class,
                        AllPlugin.class
                        })
public class AllWithoutDB {}
