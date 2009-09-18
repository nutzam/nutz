package org.nutz.ioc;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;
import org.nutz.ioc.meta.ObjServiceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { ObjServiceTest.class, DatabaseIocTest.class })
public class AllIocWithDB {}
