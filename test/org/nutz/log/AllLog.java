package org.nutz.log;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({JDKloggerAdapterTest.class, Log4jAdapterTest.class})
public class AllLog {}
