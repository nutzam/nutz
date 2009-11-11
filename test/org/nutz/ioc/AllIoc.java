package org.nutz.ioc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.ioc.java.ChainParsingTest;
import org.nutz.ioc.json.AllJsonIocTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ChainParsingTest.class, AllJsonIocTest.class})
public class AllIoc {}
