package org.nutz.log;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {Log4jTest.class,
                             SystemLogAdapterTest.class})
public class AllLog {}
