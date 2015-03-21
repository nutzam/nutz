package org.nutz.mvc.adaptor.injector;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({NameInjectorTest.class,
                     ObjectPairInjectorTest.class,
                     ObjectNavlPairInjectorTest.class,
                     ArrayInjectorTest.class})
public class AllInjector {}
