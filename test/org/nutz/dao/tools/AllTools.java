package org.nutz.dao.tools;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.dao.tools.impl.DTableProcessingTest;
import org.nutz.dao.tools.impl.NutDTableParserTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { NutDTableParserTest.class ,DTableProcessingTest.class})
public class AllTools {}
