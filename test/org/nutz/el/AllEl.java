package org.nutz.el;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.nutz.el.issue125.Issue125Test;

@RunWith(Suite.class)
@SuiteClasses({El2Test.class,RPNTest.class, Issue125Test.class})
public class AllEl {

}
