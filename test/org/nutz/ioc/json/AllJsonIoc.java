package org.nutz.ioc.json;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({    SimpleJsonIocTest.class,
                        RecurReferJsonIocTest.class,
                        EvensJsonIocTest.class,
                        AopJsonIocTest.class,
                        ScopeJsonIocTest.class})
public class AllJsonIoc {}
