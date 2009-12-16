package org.nutz.ioc.json;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({SimpleJsonIocTest.class, EvensJsonIocTest.class, AopJsonIocTest.class,
		ScopeJsonIocTest.class, AdvanceJsonIocTest.class})
public class AllJsonIocTest {}
