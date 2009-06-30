package org.nutz.json;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { JsonTest.class, JsonCommentTest.class, JsonRecursiveTest.class,
		CustomizedJsonTest.class, JsonCharsetTest.class })
public class AllJson {}
