package org.nutz.dao.test.mapping;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({    LinksGeneralTest.class,
                        OneTest.class,
                        ManyTest.class,
                        ManyManyTest.class,
                        DynamicOneTest.class,
                        DynamicManyTest.class,
                        DynamicManyManyTest.class,
                        Issue338Test.class})
public class AllMapping {}
