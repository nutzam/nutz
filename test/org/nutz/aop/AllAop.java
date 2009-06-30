package org.nutz.aop;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.nutz.aop.javassist.JavassistClassAgentTest;
import org.nutz.aop.javassist.NutIocAopTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { JavassistClassAgentTest.class, NutIocAopTest.class })
public class AllAop {}
