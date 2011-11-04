package org.nutz.json;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.json.generic.GenericTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({	JsonTest.class,
						JsonCommentTest.class,
						JsonRecursiveTest.class,
						CustomizedJsonTest.class,
						JsonCharsetTest.class,
						JsonEntityTest.class,
						GenericTest.class,
						JsonMergeTest.class
						})
public class AllJson {}
