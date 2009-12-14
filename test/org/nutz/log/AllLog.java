package org.nutz.log;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

//可用的adapter被作为一个静态变量保存了；所以我们暂时无法在一个进程内测试多个Adapter.
@RunWith(Suite.class)
@Suite.SuiteClasses(value={Log4jTest.class}) 
public class AllLog {}
