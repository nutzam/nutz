package org.nutz.ioc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.ioc.meta.Map2ObjTest;
import org.nutz.ioc.meta.Obj2MapTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { Map2ObjTest.class, Obj2MapTest.class, AllJsonIoc.class })
public class AllIocWithoutDB {}
