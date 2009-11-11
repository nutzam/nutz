package org.nutz.aop;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.nutz.aop.javassist.JavassistClassAgentTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({JavassistClassAgentTest.class})
public class AllAop {}
