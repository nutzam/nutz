package org.nutz.dao.test.mapping;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({LinksGeneral.class, One.class, Many.class, ManyMany.class, DynamicOne.class,
		DynamicMany.class, DynamicManyMany.class})
public class AllMapping {}
