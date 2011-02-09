package org.nutz.el;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.nutz.el.impl.NutElAnalyzerTest;
import org.nutz.el.impl.NutElSpliterTest;

@RunWith(Suite.class)
@SuiteClasses({
				ElTest.class,
				NutElAnalyzerTest.class,
				NutElSpliterTest.class})
public class AllEl {}
