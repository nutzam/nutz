package com.zzh.dao.test.mapping;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { One.class, Many.class, ManyMany.class, DynamicOne.class, DynamicMany.class,
		DynamicManyMany.class })
public class AllMapping {}
