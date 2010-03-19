package org.nutz.aop.asm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = {AsmClassAgentTest.class, ClassXTest.class, RegexMethodMatcherTest.class})
public class AsmAopTest {

}
