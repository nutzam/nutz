package org.nutz.mvc;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;
import org.nutz.mvc.init.MvcBaseTest;
import org.nutz.mvc.init.PathNodeTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({PathNodeTest.class, MvcBaseTest.class})
public class AllMvc {}
