package org.nutz.mvc.init;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({    MvcBaseTest.class,
                        MvcErrorCatchTest.class,
                        MvcModuleInitTest.class,
                        RestModuleTest.class})
public class AllInit {}
